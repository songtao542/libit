package com.liabit.settings

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AndroidException
import android.util.Log

/**
 * Created by song on 17-11-17.
 */
object AppSettings {

    private var mContext: Context? = null

    @JvmStatic
    fun init(context: Context) {
        mContext = context.applicationContext
    }

    private fun requireContext(): Context {
        if (mContext == null) {
            throw IllegalStateException("Please invoke AppSettings.init() method first!")
        }
        return mContext!!
    }

    @JvmStatic
    fun putFloat(name: String, value: Float): Boolean {
        return AppSettingPreference.get(requireContext()).putFloat(name, value)
    }

    @JvmStatic
    fun putInt(name: String, value: Int): Boolean {
        return AppSettingPreference.get(requireContext()).putInt(name, value)
    }

    @JvmStatic
    fun putLong(name: String, value: Long): Boolean {
        return AppSettingPreference.get(requireContext()).putLong(name, value)
    }

    @JvmStatic
    fun putBoolean(name: String, value: Boolean): Boolean {
        return AppSettingPreference.get(requireContext()).putBoolean(name, value)
    }

    @JvmStatic
    fun putString(name: String, value: String?): Boolean {
        return AppSettingPreference.get(requireContext()).putString(name, value)
    }

    @JvmStatic
    fun getFloat(name: String): Float {
        return AppSettingPreference.get(requireContext()).getFloat(name)
    }

    @JvmStatic
    fun getFloat(name: String, def: Float): Float {
        return AppSettingPreference.get(requireContext()).getFloat(name, def)
    }

    @JvmStatic
    fun getInt(name: String): Int {
        return AppSettingPreference.get(requireContext()).getInt(name)
    }

    @JvmStatic
    fun getInt(name: String, def: Int): Int {
        return AppSettingPreference.get(requireContext()).getInt(name, def)
    }

    @JvmStatic
    fun getLong(name: String): Long {
        return AppSettingPreference.get(requireContext()).getLong(name)
    }

    @JvmStatic
    fun getLong(name: String, def: Long): Long {
        return AppSettingPreference.get(requireContext()).getLong(name, def)
    }

    @JvmStatic
    fun getBoolean(name: String): Boolean {
        return AppSettingPreference.get(requireContext()).getBoolean(name)
    }

    @JvmStatic
    fun getBoolean(name: String, def: Boolean): Boolean {
        return AppSettingPreference.get(requireContext()).getBoolean(name, def)
    }

    @JvmStatic
    fun getString(name: String, def: String): String {
        return AppSettingPreference.get(requireContext()).getString(name, def)
    }

    @JvmStatic
    fun getString(name: String): String? {
        return AppSettingPreference.get(requireContext()).getString(name)
    }
}

@Suppress("unused")
class AppSettingPreference(context: Context) : SettingPreference {
    companion object {
        private const val TAG = "Settings"
        private const val NAME_EQ_PLACEHOLDER = "name=?"

        private var sInstance: AppSettingPreference? = null

        fun get(context: Context): SettingPreference {
            return sInstance ?: AppSettingPreference(context).also { sInstance = it }
        }
    }

    private val mContext: Context = context.applicationContext

    override fun putFloat(name: String, value: Float): Boolean {
        return putString(name, value.toString())
    }

    override fun putInt(name: String, value: Int): Boolean {
        return putString(name, value.toString())
    }

    override fun putLong(name: String, value: Long): Boolean {
        return putString(name, value.toString())
    }

    override fun putBoolean(name: String, value: Boolean): Boolean {
        return putString(name, value.toString())
    }

    override fun putString(name: String, value: String?): Boolean {
        try {
            val arg = Bundle()
            arg.putString(SettingsProvider.VALUE, value)
            mContext.contentResolver.call(SettingsProvider.CONTENT_URI, SettingsProvider.CALL_METHOD_PUT, name, arg)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun getFloat(name: String): Float {
        return getString(name)?.toFloatOrNull() ?: throw SettingNotFoundException(name)
    }

    override fun getFloat(name: String, def: Float): Float {
        return getString(name)?.toFloatOrNull() ?: def
    }

    override fun getInt(name: String): Int {
        return getString(name)?.toIntOrNull() ?: throw SettingNotFoundException(name)
    }

    override fun getInt(name: String, def: Int): Int {
        return getString(name)?.toIntOrNull() ?: def
    }

    override fun getLong(name: String): Long {
        return getString(name)?.toLongOrNull() ?: throw SettingNotFoundException(name)
    }

    override fun getLong(name: String, def: Long): Long {
        return getString(name)?.toLongOrNull() ?: def
    }

    override fun getBoolean(name: String): Boolean {
        return when (getString(name)) {
            "true" -> true
            "false" -> false
            else -> throw SettingNotFoundException(name)
        }
    }

    override fun getBoolean(name: String, def: Boolean): Boolean {
        return when (getString(name)) {
            "true" -> true
            "false" -> false
            else -> def
        }
    }

    override fun getString(name: String, def: String): String {
        return getString(name) ?: def
    }

    override fun getString(name: String): String? {
        val cr = mContext.contentResolver
        try {
            cr.call(SettingsProvider.CONTENT_URI, SettingsProvider.CALL_METHOD_GET, name, null)?.let { c ->
                return c.getString(SettingsProvider.VALUE)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Can't get key $name from ${SettingsProvider.CONTENT_URI}", e)
        }
        try {
            cr.query(
                    SettingsProvider.CONTENT_URI, arrayOf(SettingsProvider.VALUE), NAME_EQ_PLACEHOLDER,
                    arrayOf(name), null, null
            )?.let { c ->
                return if (c.moveToNext()) c.getString(0) else null
            }
        } catch (e: Exception) {
            Log.w(TAG, "Can't get key $name from ${SettingsProvider.CONTENT_URI}", e)
        }
        return null
    }

    private fun getUriFor(name: String): Uri {
        return getUriFor(SettingsProvider.CONTENT_URI, name)
    }

    private fun getUriFor(uri: Uri, name: String): Uri {
        return Uri.withAppendedPath(uri, name)
    }

    class SettingNotFoundException(msg: String) : AndroidException(msg)
}


interface SettingPreference {
    fun putFloat(name: String, value: Float): Boolean
    fun putInt(name: String, value: Int): Boolean
    fun putLong(name: String, value: Long): Boolean
    fun putBoolean(name: String, value: Boolean): Boolean
    fun putString(name: String, value: String?): Boolean
    fun getFloat(name: String): Float
    fun getFloat(name: String, def: Float): Float
    fun getInt(name: String): Int
    fun getInt(name: String, def: Int): Int
    fun getLong(name: String): Long
    fun getLong(name: String, def: Long): Long
    fun getBoolean(name: String): Boolean
    fun getBoolean(name: String, def: Boolean): Boolean
    fun getString(name: String, def: String): String
    fun getString(name: String): String?
}
