package com.liabit.settings

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.liabit.settings.test", appContext.packageName)

        AppSettings.init(appContext)

        AppSettings.putFloat("test-float", 1.5f)
        AppSettings.putInt("test-int", 2)
        AppSettings.putLong("test-long", 34L)
        AppSettings.putBoolean("test-boolean", true)
        AppSettings.putString("test-string", "test string")

        assertEquals(1.5f, AppSettings.getFloat("test-float"))
        assertEquals(1.5f, AppSettings.getFloat("test-float", 99f))
        assertEquals(2, AppSettings.getInt("test-int"))
        assertEquals(2, AppSettings.getInt("test-int", 99))
        assertEquals(34L, AppSettings.getLong("test-long"))
        assertEquals(34L, AppSettings.getLong("test-long", 99))
        assertEquals(true, AppSettings.getBoolean("test-boolean"))
        assertEquals(true, AppSettings.getBoolean("test-boolean", true))
        assertEquals("test string", AppSettings.getString("test-string", "99"))
        assertEquals("test string", AppSettings.getString("test-string"))
    }
}