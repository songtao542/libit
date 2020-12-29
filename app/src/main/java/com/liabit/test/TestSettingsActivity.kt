package com.liabit.test

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.settings.AppSettings
import com.liabit.test.databinding.ActivityTestSettingsBinding
import com.liabit.viewbinding.inflate

class TestSettingsActivity : AppCompatActivity() {
    companion object {
        const val TAG = "TTTT"
    }

    private val binding by inflate<ActivityTestSettingsBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        AppSettings.init(applicationContext)

        AppSettings.putFloat("test-float", 1.5f)
        AppSettings.putInt("test-int", 2)
        AppSettings.putLong("test-long", 34L)
        AppSettings.putBoolean("test-boolean", true)
        AppSettings.putString("test-string", "test string")

        Log.d(TAG, "test-float: 1.5f == ${AppSettings.getFloat("test-float")}")
        Log.d(TAG, "test-float: 1.5f == ${AppSettings.getFloat("test-float", 99f)}")
        Log.d(TAG, "test-int: 2 == ${AppSettings.getInt("test-int")}")
        Log.d(TAG, "test-int: 2 == ${AppSettings.getInt("test-int", 99)}")
        Log.d(TAG, "test-long: 34L == ${AppSettings.getLong("test-long")}")
        Log.d(TAG, "test-long: 34L == ${AppSettings.getLong("test-long", 99)}")
        Log.d(TAG, "test-boolean: ${AppSettings.getBoolean("test-boolean")}")
        Log.d(TAG, "test-boolean: ${AppSettings.getBoolean("test-boolean", true)}")
        Log.d(TAG, "test-string: test string == ${AppSettings.getString("test-string", "99")}")
        Log.d(TAG, "test-string: test string == ${AppSettings.getString("test-string")}")

    }
}