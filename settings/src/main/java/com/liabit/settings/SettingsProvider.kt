package com.liabit.settings

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.*
import android.text.TextUtils
import android.util.Log
import java.io.File
import java.io.FileDescriptor
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max

/**
 *
 */
@Suppress("unused")
class SettingsProvider : ContentProvider() {

    companion object {
        internal const val NAME = "name"
        internal const val VALUE = "value"

        internal const val CALL_METHOD_GET = "method_get"
        internal const val CALL_METHOD_PUT = "method_put"

        internal var CONTENT_URI: Uri = Uri.parse("content://com.liabit.settings/settings")

        private const val MUTATION_OPERATION_INSERT = 1
        private const val MUTATION_OPERATION_DELETE = 2
        private const val MUTATION_OPERATION_UPDATE = 3

        private const val TAG = "SettingsProvider"

        private const val SETTINGS_FILE = "settings.xml"

        private const val SETTINGS_VERSION = 1

        private const val MSG_NOTIFY_URI_CHANGED = 1

        private val ALL_COLUMNS = arrayOf(NAME, VALUE)

        private val NULL_SETTING = Bundle(1).apply { putString(VALUE, null) }
    }

    private val mLock = Any()
    private val mSettingsRegistry by lazy(mLock) { SettingsRegistry() }

    override fun onCreate(): Boolean {
        CONTENT_URI = Uri.parse("content://${requireContext().packageName}.settings/settings")
        return true
    }

    fun requireContext(): Context {
        return context ?: throw IllegalStateException("Cannot find context from the provider.")
    }

    override fun call(method: String, name: String?, args: Bundle?): Bundle? {
        if (name == null) return null
        when (method) {
            CALL_METHOD_GET -> {
                val setting = getSetting(name)
                return packageValueForCallResult(setting)
            }
            CALL_METHOD_PUT -> {
                val value = getSettingValue(args)
                insertSetting(name, value)
            }
            else -> {
                Log.w(TAG, "call() with invalid method: $method")
            }
        }
        return null
    }

    override fun getType(uri: Uri): String {
        val args = Arguments(uri, null, null, true)
        return if (TextUtils.isEmpty(args.name)) {
            "vnd.android.cursor.dir/settings"
        } else {
            "vnd.android.cursor.item/settings"
        }
    }

    override fun query(uri: Uri, projection: Array<String>?, where: String?, whereArgs: Array<String>?, order: String?): Cursor? {
        val args = Arguments(uri, where, whereArgs, true)
        val normalizedProjection = normalizeProjection(projection)
        val name = args.name
        return if (name != null) packageSettingForQuery(getSetting(name), normalizedProjection) else null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) return null
        val name = values.getAsString(NAME)
        if (!isKeyValid(name)) {
            return null
        }
        val value = values.getAsString(VALUE)
        return if (insertSetting(name, value)) Uri.withAppendedPath(CONTENT_URI, name) else null
    }

    override fun bulkInsert(uri: Uri, allValues: Array<ContentValues>): Int {
        var insertionCount = 0
        val valuesCount = allValues.size
        for (i in 0 until valuesCount) {
            val values = allValues[i]
            if (insert(uri, values) != null) {
                insertionCount++
            }
        }
        return insertionCount
    }

    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {
        val args = Arguments(uri, where, whereArgs, false)
        val name = args.name
        if (name == null || !isKeyValid(args.name)) {
            return 0
        }
        return if (deleteSetting(name)) 1 else 0
    }

    override fun update(uri: Uri, values: ContentValues?, where: String?, whereArgs: Array<String>?): Int {
        val args = Arguments(uri, where, whereArgs, false)
        val name = values?.getAsString(NAME) ?: args.name
        if (name == null || !isKeyValid(name)) {
            return 0
        }
        val value = values?.getAsString(VALUE)
        return if (updateSetting(name, value)) 1 else 0
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        throw FileNotFoundException("Direct file access no longer supported; ringtone playback is available through android.media.Ringtone")
    }

    override fun dump(fd: FileDescriptor, pw: PrintWriter, args: Array<String>) {
        synchronized(mLock) {
            try {
                dump(pw)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Exception:" + e.message)
                }
            }
        }
    }

    private fun dump(pw: PrintWriter) {
        val globalCursor = getAllSettings()
        dumpSettings(globalCursor, pw)
        pw.println()
    }

    private fun dumpSettings(cursor: Cursor?, pw: PrintWriter) {
        if (cursor == null || !cursor.moveToFirst()) {
            return
        }
        val nameColumnIdx = cursor.getColumnIndex(NAME)
        val valueColumnIdx = cursor.getColumnIndex(VALUE)
        do {
            pw.append(" name:").append(toDumpString(cursor.getString(nameColumnIdx)))
            pw.append(" value:").append(toDumpString(cursor.getString(valueColumnIdx)))
            pw.println()
        } while (cursor.moveToNext())
    }

    private fun getAllSettings(): Cursor {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getAllSettings()")
        }
        synchronized(mLock) {
            // Get the settings.
            val settingsState = mSettingsRegistry.settingsLocked
            val names = settingsState.settingNamesLocked
            val nameCount = names.size
            val normalizedProjection = normalizeProjection(ALL_COLUMNS)
            val result = MatrixCursor(normalizedProjection, nameCount)
            // Anyone can get the global settings, so no security checks.
            for (i in 0 until nameCount) {
                val name = names[i]
                val setting = settingsState.getSettingLocked(name)
                if (setting != null) {
                    appendSettingToCursor(result, setting)
                }
            }
            return result
        }
    }

    private fun getSetting(name: String): SettingsState.Setting? {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "getSetting($name)")
        }
        synchronized(mLock) { return mSettingsRegistry.getSettingLocked(name) }
    }

    private fun updateSetting(name: String, value: String?): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "updateSetting($name, $value)")
        }
        return mutateSetting(name, value, MUTATION_OPERATION_UPDATE)
    }

    private fun insertSetting(name: String, value: String?): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "insertSetting($name, $value)")
        }
        return mutateSetting(name, value, MUTATION_OPERATION_INSERT)
    }

    private fun deleteSetting(name: String): Boolean {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "deleteGlobalSettingLocked($name)")
        }
        return mutateSetting(name, null, MUTATION_OPERATION_DELETE)
    }

    private fun mutateSetting(name: String, value: String?, operation: Int): Boolean {
        // Make sure the caller can change the settings - treated as secure.
        // enforceWritePermission(Manifest.permission.WRITE_SECURE_SETTINGS);
        // Perform the mutation.
        synchronized(mLock) {
            when (operation) {
                MUTATION_OPERATION_INSERT -> {
                    return mSettingsRegistry.insertSettingLocked(name, value)
                }
                MUTATION_OPERATION_DELETE -> {
                    return mSettingsRegistry.deleteSettingLocked(name)
                }
                MUTATION_OPERATION_UPDATE -> {
                    return mSettingsRegistry.updateSettingLocked(name, value)
                }
                else -> {
                }
            }
        }
        return false
    }

    private fun enforceWritePermission(permission: String) {
        if (context!!.checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            throw SecurityException("Permission denial: writing to settings requires:$permission")
        }
    }

    private fun toDumpString(s: String?): String {
        return s ?: "{null}"
    }

    private fun packageValueForCallResult(setting: SettingsState.Setting?): Bundle {
        if (setting == null) {
            return NULL_SETTING
        }
        if (BuildConfig.DEBUG) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "packageValueForCallResult, name = " + setting.name + ", value : " + setting.value)
            }
        }
        return Bundle(1).apply { putString(VALUE, setting.value) }
    }

    private fun getSettingValue(args: Bundle?): String? {
        return args?.getString(VALUE)
    }

    private fun packageSettingForQuery(setting: SettingsState.Setting?, projection: Array<String>): MatrixCursor {
        if (setting == null) {
            return MatrixCursor(projection, 0)
        }
        val cursor = MatrixCursor(projection, 1)
        appendSettingToCursor(cursor, setting)
        return cursor
    }

    private fun normalizeProjection(projection: Array<String>?): Array<String> {
        if (projection == null) {
            return ALL_COLUMNS
        }
        val columnCount = projection.size
        for (i in 0 until columnCount) {
            val column = projection[i]
            require(isColumnValid(column)) { "Invalid column: $column" }
        }
        return projection
    }

    private fun isColumnValid(value: String): Boolean {
        for (i in ALL_COLUMNS.indices) {
            if (ALL_COLUMNS[i] == value) return true
        }
        return false
    }

    private fun appendSettingToCursor(cursor: MatrixCursor, setting: SettingsState.Setting) {
        val columnCount = cursor.columnCount
        val values = arrayOfNulls<String>(columnCount)
        for (i in 0 until columnCount) {
            when (cursor.getColumnName(i)) {
                NAME -> values[i] = setting.name
                VALUE -> values[i] = setting.value
            }
        }
        cursor.addRow(values)
    }

    private fun isKeyValid(key: String?): Boolean {
        return !(key.isNullOrBlank() || SettingsState.isBinary(key))
    }

    private class Arguments(uri: Uri, where: String?, whereArgs: Array<String>?, supportAll: Boolean) {
        var name: String? = null

        companion object {
            private val WHERE_WITH_PARAM_NO_BRACKETS = Pattern.compile("[\\s]*name[\\s]*=[\\s]*\\?[\\s]*")
            private val WHERE_WITH_PARAM_IN_BRACKETS = Pattern.compile("[\\s]*\\([\\s]*name[\\s]*=[\\s]*\\?[\\s]*\\)[\\s]*")
            private val WHERE_NO_PARAM_IN_BRACKETS = Pattern.compile("[\\s]*\\([\\s]*name[\\s]*=[\\s]*['\"].*['\"][\\s]*\\)[\\s]*")
            private val WHERE_NO_PARAM_NO_BRACKETS = Pattern.compile("[\\s]*name[\\s]*=[\\s]*['\"].*['\"][\\s]*")
        }

        init {
            name = when (uri.pathSegments.size) {
                1 -> {
                    if (where != null && isWhereWithParam(where) && whereArgs != null && whereArgs.size == 1) {
                        whereArgs[0]
                    } else if (where != null && isWhereNoParam(where)) {
                        val startIndex = max(where.indexOf("'"), where.indexOf("\"")) + 1
                        val endIndex = max(where.lastIndexOf("'"), where.lastIndexOf("\""))
                        where.substring(startIndex, endIndex)
                    } else {
                        null
                    }
                }
                2 -> {
                    if (where == null && whereArgs == null) uri.pathSegments[1] else null
                }
                else -> null
            }
            val skip = supportAll && where == null && whereArgs == null
            if (name.isNullOrBlank() && !skip) {
                val message = String.format(
                        "Supported SQL:" +
                                "uri content://some_table/some_property with null where and where args" +
                                "uri content://some_table with query name=? and single name as arg" +
                                "uri content://some_table with query name=some_name and null args" +
                                "but got - uri:%1s, where:%2s whereArgs:%3s", uri, where, Arrays.toString(whereArgs)
                )
                throw IllegalArgumentException(message)
            }
        }

        private fun isWhereWithParam(where: String): Boolean {
            return WHERE_WITH_PARAM_NO_BRACKETS.matcher(where).matches() || WHERE_WITH_PARAM_IN_BRACKETS.matcher(where).matches()
        }

        private fun isWhereNoParam(where: String): Boolean {
            return WHERE_NO_PARAM_NO_BRACKETS.matcher(where).matches() || WHERE_NO_PARAM_IN_BRACKETS.matcher(where).matches()
        }
    }

    private inner class NotifyUriHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_NOTIFY_URI_CHANGED -> {
                    val userId = msg.arg1
                    val uri = msg.obj as Uri
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        requireContext().contentResolver.notifyChange(uri, null, 0)
                    } else {
                        @Suppress("DEPRECATION")
                        requireContext().contentResolver.notifyChange(uri, null, false)
                    }
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Notifying for $userId: $uri")
                    }
                }
            }
        }
    }

    internal inner class SettingsRegistry {
        private var mSettingsState: SettingsState? = null
        private val mHandler by lazy { NotifyUriHandler(requireContext().mainLooper) }
        private val mSettingsFile by lazy { File(requireContext().filesDir, SETTINGS_FILE) }

        val settingsLocked: SettingsState get() = peekSettingsStateLocked()

        private fun peekSettingsStateLocked(): SettingsState {
            return mSettingsState ?: SettingsState(mLock, mSettingsFile).also {
                mSettingsState = it
                // Upgrade the settings to the latest version.
                upgradeIfNeededLocked(it)
            }
        }

        fun insertSettingLocked(name: String, value: String?): Boolean {
            val settingsState = peekSettingsStateLocked()
            val success = settingsState.insertSettingLocked(name, value!!)
            if (success) {
                notifyForSettingsChange(name)
            }
            return success
        }

        fun deleteSettingLocked(name: String): Boolean {
            val settingsState = peekSettingsStateLocked()
            val success = settingsState.deleteSettingLocked(name)
            if (success) {
                notifyForSettingsChange(name)
            }
            return success
        }

        fun getSettingLocked(name: String): SettingsState.Setting? {
            val settingsState = peekSettingsStateLocked()
            return settingsState.getSettingLocked(name)
        }

        fun updateSettingLocked(name: String, value: String?): Boolean {
            val settingsState = peekSettingsStateLocked()
            val success = settingsState.updateSettingLocked(name, value!!)
            if (success) {
                notifyForSettingsChange(name)
            }
            return success
        }

        private fun notifyForSettingsChange(name: String) {
            // Now send the notification through the content framework.
            val uri = getNotificationUriFor(name)
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "notifyForSettingsChange uri=$uri")
            }
            mHandler.obtainMessage(MSG_NOTIFY_URI_CHANGED, uri).sendToTarget()
        }

        private fun getNotificationUriFor(name: String?): Uri {
            return if (name != null) Uri.withAppendedPath(CONTENT_URI, name) else CONTENT_URI
        }

        private fun upgradeIfNeededLocked(settingsState: SettingsState) {
            // The version of all settings for a user is the same (all users have secure).
            // Try an update from the current state.
            val oldVersion = settingsState.versionLocked
            val newVersion = SETTINGS_VERSION

            // If up do date - done.
            if (oldVersion == newVersion) {
                return
            }

            // Try to upgrade.
            onUpgradeLocked(oldVersion, newVersion)

            // Set the global settings version if owner.
            settingsState.versionLocked = newVersion
        }

        @Suppress("SameParameterValue")
        private fun onUpgradeLocked(oldVersion: Int, newVersion: Int): Int {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "oldVersion: $oldVersion  newVersion: $newVersion")
            }
            return oldVersion
        }
    }
}
