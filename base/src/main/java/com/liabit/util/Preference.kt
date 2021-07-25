package com.liabit.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object Preference {

    private const val mPreferenceName = "prefs"

    private lateinit var mPreference: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        mPreference = context.applicationContext.getSharedPreferences(mPreferenceName, Context.MODE_PRIVATE)
    }

    @JvmStatic
    fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mPreference.registerOnSharedPreferenceChangeListener(listener)
    }

    @JvmStatic
    fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        mPreference.unregisterOnSharedPreferenceChangeListener(listener)
    }

    @JvmStatic
    fun getString(key: String, defaultValue: String? = null): String {
        return mPreference.getString(key, defaultValue) ?: ""
    }

    @JvmStatic
    fun getStringOrNull(key: String, defaultValue: String? = null): String? {
        return mPreference.getString(key, defaultValue)
    }

    @JvmStatic
    fun getInt(key: String, defaultValue: Int? = null): Int {
        return mPreference.getInt(key, defaultValue ?: -1)
    }

    @JvmStatic
    fun getLong(key: String, defaultValue: Long? = null): Long {
        return mPreference.getLong(key, defaultValue ?: -1)
    }

    @JvmStatic
    fun getFloat(key: String, defaultValue: Float? = null): Float {
        return mPreference.getFloat(key, defaultValue ?: -1f)
    }

    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean? = null): Boolean {
        return mPreference.getBoolean(key, defaultValue ?: false)
    }

    @JvmStatic
    fun putString(key: String, value: String) {
        mPreference.edit { putString(key, value) }
    }

    @JvmStatic
    fun putInt(key: String, value: Int) {
        mPreference.edit { putInt(key, value) }
    }

    @JvmStatic
    fun putLong(key: String, value: Long) {
        mPreference.edit { putLong(key, value) }
    }

    @JvmStatic
    fun putFloat(key: String, value: Float) {
        mPreference.edit { putFloat(key, value) }
    }

    @JvmStatic
    fun putBoolean(key: String, value: Boolean) {
        mPreference.edit { putBoolean(key, value) }
    }

}