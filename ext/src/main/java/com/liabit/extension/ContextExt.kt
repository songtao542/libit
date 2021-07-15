package com.liabit.extension

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Point
import android.os.Build
import android.os.Process
import android.util.Log
import android.util.TypedValue
import android.view.WindowManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * @return Returns true if the intent has been scheduled for delivery to one or more
 * broadcast receivers.  (Note tha delivery may not ultimately take place if one of those
 * receivers is unregistered before it is dispatched.)
 */
fun Context.sendLocalBroadcast(intent: Intent): Boolean {
    return LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return when {
        resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
        else -> 24.dip(this)
    }
}

fun Context.getNavigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return when {
        resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
        else -> 48.dip(this)
    }
}

/**
 * @return screen width
 */
fun Context.getScreenWidth(): Int {
    return getScreenSize().x
}

/**
 * @return screen height
 */
fun Context.getScreenHeight(): Int {
    return getScreenSize().y
}

/**
 * @return screen size
 */
fun Context.getScreenSize(): Point {
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display?.getRealSize(point)
    }
    if (point.x == 0 || point.y == 0) {
        (getSystemService(Context.WINDOW_SERVICE) as? WindowManager)?.defaultDisplay?.getRealSize(point)
    }
    if (point.x == 0 || point.y == 0) {
        point.x = resources.displayMetrics.widthPixels
        point.y = resources.displayMetrics.heightPixels
    }
    return point
}

fun Context.color(id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(id)
    } else {
        @Suppress("DEPRECATION")
        resources.getColor(id)
    }
}

fun Context.dip(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}

fun Context.dp(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.getVersionCode(): Long {
    var verCode = -1L
    try {
        verCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(packageName, 0).longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0).versionCode.toLong()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        Log.w(TAG, "getVersionCode error: ", e)
    }
    return verCode
}

fun Context.getVersionName(): String {
    var verName = ""
    try {
        verName = packageManager.getPackageInfo(packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        Log.w(TAG, "getVersionName error: ", e)
    }
    return verName
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
fun Context.getProcessName(): String {
    var processName: String? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        processName = Application.getProcessName()
    } else {
        try {
            val currentProcessName = Class.forName(
                "android.app.ActivityThread",
                false,
                Application::class.java.classLoader
            ).getDeclaredMethod("currentProcessName", *arrayOfNulls<Class<*>?>(0))
            currentProcessName.isAccessible = true
            val invoke = currentProcessName.invoke(null, arrayOfNulls<Any>(0))
            if (invoke is String) {
                processName = invoke
            }
        } catch (e: Throwable) {
            Log.w(TAG, "getProcessName by reflect error: ", e)
        }
        if (processName == null) {
            val pid: Int = Process.myPid()
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningAppList = am.runningAppProcesses
            if (runningAppList != null) {
                for (processInfo in runningAppList) {
                    if (processInfo.pid == pid) {
                        processName = processInfo.processName
                    }
                }
            }
        }
    }
    return processName ?: ""
}

