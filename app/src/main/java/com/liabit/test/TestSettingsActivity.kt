package com.liabit.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liabit.settings.AppSettings
import org.junit.Assert.assertEquals

class TestSettingsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "TestSettingsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_settings)

        AppSettings.init(applicationContext)

        AppSettings.putFloat("test-float", 1.5f)
        AppSettings.putInt("test-int", 2)
        AppSettings.putLong("test-long", 34L)
        AppSettings.putBoolean("test-boolean", true)
        AppSettings.putString("test-string", "test string")

        Log.d(TAG, "test-float: ${1.5f == AppSettings.getFloat("test-float")}")
        Log.d(TAG, "test-float: ${1.5f == AppSettings.getFloat("test-float", 99f)}")
        Log.d(TAG, "test-int: ${2 == AppSettings.getInt("test-int")}")
        Log.d(TAG, "test-int: ${2 == AppSettings.getInt("test-int", 99)}")
        Log.d(TAG, "test-long: ${34L == AppSettings.getLong("test-long")}")
        Log.d(TAG, "test-long: ${34L == AppSettings.getLong("test-long", 99)}")
        Log.d(TAG, "test-boolean: ${AppSettings.getBoolean("test-boolean")}")
        Log.d(TAG, "test-boolean: ${AppSettings.getBoolean("test-boolean", true)}")
        Log.d(TAG, "test-string: ${"test string" == AppSettings.getString("test-string", "99")}")
        Log.d(TAG, "test-string: ${"test string" == AppSettings.getString("test-string")}")

    }
}