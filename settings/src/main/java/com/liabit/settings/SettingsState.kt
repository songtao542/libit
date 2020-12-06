package com.liabit.settings

import android.annotation.SuppressLint
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.os.SystemClock
import android.text.TextUtils
import android.util.*
import android.util.Base64
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * This class contains the state for one type of settings. It is responsible
 * for saving the state asynchronously to an XML file after a mutation and
 * loading the from an XML file on construction.
 *
 * This class uses the same lock as the settings provider to ensure that
 * multiple changes made by the settings provider, e,g, upgrade, bulk insert,
 * etc, are atomically persisted since the asynchronous persistence is using
 * the same lock to grab the current state to write to disk.
 */
@Suppress("unused")
internal class SettingsState(private val mLock: Any, private val mStatePersistFile: File) {

    companion object {
        private const val TAG = "SettingsState"
        private const val WRITE_SETTINGS_DELAY_MILLIS = 200L
        private const val MAX_WRITE_SETTINGS_DELAY_MILLIS = 2000L
        private const val MAX_BYTES_LIMITED = 20000
        private const val VERSION_UNDEFINED = -1

        private const val MSG_PERSIST_SETTINGS = 1

        private const val TAG_SETTINGS = "settings"
        private const val TAG_SETTING = "setting"

        private const val ATTR_NAME = "name"
        private const val ATTR_VERSION = "version"

        /**
         * Non-binary value will be written in this attribute.
         */
        private const val ATTR_VALUE = "value"

        /**
         * KXmlSerializer won't like some characters.  We encode such characters in base64 and
         * store in this attribute.
         * NOTE: A null value will have NEITHER ATTR_VALUE nor ATTR_VALUE_BASE64.
         */
        private const val ATTR_VALUE_BASE64 = "valueBase64"

        private const val NULL_VALUE = "null"

        /**
         * @return TRUE if a string is considered "binary" from KXML's point of view.
         */
        @JvmStatic
        fun isBinary(s: String): Boolean {
            // See KXmlSerializer.writeEscaped
            for (element in s) {
                val allowedInXml = element.toInt() in 0x20..0xd7ff || element.toInt() in 0xe000..0xfffd
                if (!allowedInXml) {
                    return true
                }
            }
            return false
        }
    }

    private val mHandler: Handler = BackgroundHandler()
    private val mSettings = ArrayMap<String, Setting>()
    private var mVersion = VERSION_UNDEFINED
    private var mLastNotWrittenMutationTimeMillis: Long = 0
    private var mDirty = false
    private var mWriteScheduled = false
    private var mBytes: Int = 0

    init {
        synchronized(mLock) { readStateSyncLocked() }
    }

    // The settings provider must hold its lock when calling here.
    // The settings provider must hold its lock when calling here.
    var versionLocked: Int
        get() = mVersion
        set(version) {
            if (version == mVersion) {
                return
            }
            mVersion = version
            scheduleWriteIfNeededLocked()
        }

    // The settings provider must hold its lock when calling here.
    val settingNamesLocked: List<String>
        get() {
            val names = ArrayList<String>()
            val settingsCount = mSettings.size
            for (i in 0 until settingsCount) {
                val name = mSettings.keyAt(i)
                names.add(name)
            }
            return names
        }

    // The settings provider must hold its lock when calling here.
    fun getSettingLocked(name: String): Setting? {
        Log.i(TAG, "getSettingLocked, name = $name")
        return if (TextUtils.isEmpty(name)) null else mSettings[name]
    }

    // The settings provider must hold its lock when calling here.
    fun updateSettingLocked(name: String, value: String): Boolean {
        return if (!hasSettingLocked(name)) false else insertSettingLocked(name, value)
    }

    // The settings provider must hold its lock when calling here.
    fun insertSettingLocked(name: String, value: String): Boolean {
        Log.i(TAG, "insertSettingLocked, name = $name, value = $value")
        if (TextUtils.isEmpty(name)) {
            return false
        }
        val oldState = mSettings[name]
        val oldValue = oldState?.value
        if (oldState != null) {
            if (!oldState.update(value)) {
                return false
            }
        } else {
            val state = Setting(name, value)
            mSettings[name] = state
        }
        updateMemoryUsageLocked(oldValue, value)
        scheduleWriteIfNeededLocked()
        return true
    }

    // The settings provider must hold its lock when calling here.
    fun persistSyncLocked() {
        mHandler.removeMessages(MSG_PERSIST_SETTINGS)
        doWriteState()
    }

    // The settings provider must hold its lock when calling here.
    fun deleteSettingLocked(name: String): Boolean {
        Log.i(TAG, "deleteSettingLocked, name = $name")
        if (TextUtils.isEmpty(name) || !hasSettingLocked(name)) {
            return false
        }
        val oldState = mSettings.remove(name)
        updateMemoryUsageLocked(oldState?.value, null)
        scheduleWriteIfNeededLocked()
        return true
    }

    // The settings provider must hold its lock when calling here.
    fun destroyLocked(callback: Runnable?) {
        mHandler.removeMessages(MSG_PERSIST_SETTINGS)
        if (callback != null) {
            if (mDirty) {
                // Do it without a delay.
                mHandler.obtainMessage(MSG_PERSIST_SETTINGS, callback).sendToTarget()
                return
            }
            callback.run()
        }
    }

    private fun updateMemoryUsageLocked(oldValue: String?, newValue: String?) {
        val oldValueSize = oldValue?.length ?: 0
        val newValueSize = newValue?.length ?: 0
        val deltaSize = newValueSize - oldValueSize
        mBytes = (mBytes + deltaSize).coerceAtLeast(0)
        check(mBytes <= MAX_BYTES_LIMITED) {
            ("You are adding too many system settings. You should stop using system settings for app specific data")
        }
        Log.i(TAG, "Settings size: $mBytes bytes.")
    }

    private fun hasSettingLocked(name: String): Boolean {
        return mSettings.indexOfKey(name) >= 0
    }

    private fun scheduleWriteIfNeededLocked() {
        // If dirty then we have a write already scheduled.
        if (!mDirty) {
            mDirty = true
            writeStateAsyncLocked()
        }
    }

    private fun writeStateAsyncLocked() {
        val currentTimeMillis = SystemClock.uptimeMillis()
        if (mWriteScheduled) {
            mHandler.removeMessages(MSG_PERSIST_SETTINGS)

            // If enough time passed, write without holding off anymore.
            val timeSinceLastNotWrittenMutationMillis = (currentTimeMillis - mLastNotWrittenMutationTimeMillis)
            if (timeSinceLastNotWrittenMutationMillis >= MAX_WRITE_SETTINGS_DELAY_MILLIS) {
                mHandler.obtainMessage(MSG_PERSIST_SETTINGS).sendToTarget()
                return
            }

            // Hold off a bit more as settings are frequently changing.
            val maxDelayMillis = (mLastNotWrittenMutationTimeMillis + MAX_WRITE_SETTINGS_DELAY_MILLIS - currentTimeMillis).coerceAtLeast(0)
            val writeDelayMillis = WRITE_SETTINGS_DELAY_MILLIS.coerceAtMost(maxDelayMillis)
            val message = mHandler.obtainMessage(MSG_PERSIST_SETTINGS)
            mHandler.sendMessageDelayed(message, writeDelayMillis)
        } else {
            mLastNotWrittenMutationTimeMillis = currentTimeMillis
            val message = mHandler.obtainMessage(MSG_PERSIST_SETTINGS)
            mHandler.sendMessageDelayed(message, WRITE_SETTINGS_DELAY_MILLIS)
            mWriteScheduled = true
        }
    }

    private fun doWriteState() {
        Log.i(TAG, "[PERSIST START]")
        val destination = AtomicFile(mStatePersistFile)
        var version: Int
        var settings: ArrayMap<String, Setting>
        synchronized(mLock) {
            version = mVersion
            settings = ArrayMap(mSettings)
            mDirty = false
            mWriteScheduled = false
        }
        var out: FileOutputStream? = null
        try {
            out = destination.startWrite()
            val serializer = Xml.newSerializer()
            serializer.setOutput(out, StandardCharsets.UTF_8.name())
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
            serializer.startDocument(null, true)
            serializer.startTag(null, TAG_SETTINGS)
            serializer.attribute(null, ATTR_VERSION, version.toString())
            val settingCount = settings.size
            for (i in 0 until settingCount) {
                val setting = settings.valueAt(i)
                writeSingleSetting(mVersion, serializer, setting.name, setting.value)
                Log.i(TAG, "[PERSISTED]" + setting.name + "=" + setting.value)
            }
            serializer.endTag(null, TAG_SETTINGS)
            serializer.endDocument()
            destination.finishWrite(out)
            Log.i(TAG, "[PERSIST END]")
        } catch (t: Throwable) {
            Log.wtf(TAG, "Failed to write settings, restoring backup", t)
            destination.failWrite(out)
        } finally {
            closeQuietly(out)
        }
    }

    private fun getValueAttribute(parser: XmlPullParser): String? {
        val value = parser.getAttributeValue(null, ATTR_VALUE)
        if (value != null) {
            return if (value != NULL_VALUE) value else null
        }
        val base64 = parser.getAttributeValue(null, ATTR_VALUE_BASE64)
        return if (base64 != null) base64Decode(base64) else null
    }

    private fun readStateSyncLocked() {
        if (!mStatePersistFile.exists()) {
            return
        }
        val inputStream: FileInputStream = try {
            AtomicFile(mStatePersistFile).openRead()
        } catch (e: FileNotFoundException) {
            Log.i(TAG, "No settings state")
            return
        }
        try {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, StandardCharsets.UTF_8.name())
            parseStateLocked(parser)
        } catch (e: XmlPullParserException) {
            throw IllegalStateException("Failed parsing settings file: $mStatePersistFile", e)
        } catch (e: IOException) {
            throw IllegalStateException("Failed parsing settings file: $mStatePersistFile", e)
        } finally {
            closeQuietly(inputStream)
        }
    }

    private fun parseStateLocked(parser: XmlPullParser) {
        val outerDepth = parser.depth
        var type: Int = parser.next()
        while (type != XmlPullParser.END_DOCUMENT && (type != XmlPullParser.END_TAG || parser.depth > outerDepth)) {
            if (type != XmlPullParser.END_TAG && type != XmlPullParser.TEXT) {
                val tagName = parser.name
                if (tagName == TAG_SETTINGS) {
                    parseSettingsLocked(parser)
                }
            }
            type = parser.next()
        }
    }

    private fun parseSettingsLocked(parser: XmlPullParser) {
        mVersion = parser.getAttributeValue(null, ATTR_VERSION).toInt()
        val outerDepth = parser.depth
        var type: Int = parser.next()
        while (type != XmlPullParser.END_DOCUMENT && (type != XmlPullParser.END_TAG || parser.depth > outerDepth)) {
            if (type != XmlPullParser.END_TAG && type != XmlPullParser.TEXT) {
                val tagName = parser.name
                if (tagName == TAG_SETTING) {
                    val name = parser.getAttributeValue(null, ATTR_NAME)
                    val value = getValueAttribute(parser)
                    mSettings[name] = Setting(name, value)
                    Log.i(TAG, "[RESTORED] $name=$value")
                }
            }
            type = parser.next()
        }
    }

    /**
     * Closes 'closeable', ignoring any checked exceptions. Does nothing if 'closeable' is null.
     */
    private fun closeQuietly(closeable: AutoCloseable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }

    private fun writeSingleSetting(
            version: Int,
            serializer: XmlSerializer,
            name: String,
            value: String?
    ) {
        Log.i(TAG, "writeSingleSetting, version = $version, name = $name, value = $value")
        if (isBinary(name)) {
            // This shouldn't happen.
            return
        }
        serializer.startTag(null, TAG_SETTING)
        serializer.attribute(null, ATTR_NAME, name)
        setValueAttribute(serializer, value)
        serializer.endTag(null, TAG_SETTING)
    }

    private fun setValueAttribute(serializer: XmlSerializer, value: String?) {
        when {
            value == null -> serializer.attribute(null, ATTR_VALUE, NULL_VALUE)// Null value -> No ATTR_VALUE nor ATTR_VALUE_BASE64.
            isBinary(value) -> serializer.attribute(null, ATTR_VALUE_BASE64, base64Encode(value))
            else -> serializer.attribute(null, ATTR_VALUE, value)
        }
    }

    private fun base64Encode(s: String): String {
        return Base64.encodeToString(toBytes(s), Base64.NO_WRAP)
    }

    private fun base64Decode(s: String): String {
        return fromBytes(Base64.decode(s, Base64.DEFAULT))
    }

    // Note the followings are basically just UTF-16 encode/decode.  But we want to preserve
    // contents as-is, even if it contains broken surrogate pairs, we do it by ourselves,
    // since I don't know how Charset would treat them.
    private fun toBytes(s: String): ByteArray {
        val result = ByteArray(s.length * 2)
        var resultIndex = 0
        for (element in s) {
            result[resultIndex++] = (element.toInt() shr 8).toByte()
            result[resultIndex++] = element.toByte()
        }
        return result
    }

    private fun fromBytes(bytes: ByteArray): String {
        val sb = StringBuffer(bytes.size / 2)
        val last = bytes.size - 1
        var i = 0
        while (i < last) {
            val ch = (bytes[i].toInt() and 0xff shl 8 or (bytes[i + 1].toInt() and 0xff)).toChar()
            sb.append(ch)
            i += 2
        }
        return sb.toString()
    }

    @SuppressLint("HandlerLeak")
    private inner class BackgroundHandler : Handler(BackgroundThread.handler.looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                MSG_PERSIST_SETTINGS -> {
                    val callback = message.obj as? Runnable
                    doWriteState()
                    callback?.run()
                }
            }
        }
    }

    object BackgroundThread : HandlerThread("background") {
        val handler: Handler

        init {
            start()
            handler = Handler(looper)
        }
    }

    data class Setting(var name: String, var value: String? = null) {
        fun update(value: String): Boolean {
            if (value == this.value) {
                return false
            }
            this.value = value
            return true
        }
    }
}