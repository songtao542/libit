package com.liabit.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager

/**
 *  extend the layout to the status bar,
 *  not hide status bar,
 *  not extend the layout to navigation bar,
 *  not hide navigation bar
 */
fun Activity.layoutUnderStatusBar(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    val flag = window.decorView.systemUiVisibility
    val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
    val lightNavigation = lightNavigationBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    window.decorView.systemUiVisibility = flags
}

/**
 *  extend the layout to the status bar nad navigation bar,
 *  set the navigation bar color to transparent,
 *  not hide status bar,
 *  not hide navigation bar
 */
fun Activity.layoutUnderSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    val flag = window.decorView.systemUiVisibility
    val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
    val lightNavigation = lightNavigationBar
        ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    window.navigationBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility = flags
}

/**
 *  extend the layout to the status bar and navigation bar,
 *  not hide status bar,
 *  hide navigation bar
 */
fun Activity.layoutUnderStatusBarAndHideNavigation(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    val flag = window.decorView.systemUiVisibility
    val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
    val lightNavigation = lightNavigationBar
        ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    window.decorView.systemUiVisibility = flags
}

/**
 * extend the layout to the status bar and navigation bar,
 * set the navigation bar color to transparent,
 * hide status bar
 * hide navigation bar
 */
fun Activity.hideSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    val flag = window.decorView.systemUiVisibility
    val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
    val lightNavigation = lightNavigationBar
        ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    window.navigationBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility = flags
    val lp = window.attributes
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        lp.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
    }
    window.attributes = lp
}

/**
 * extend the layout to the status bar and navigation bar,
 * set the navigation bar color to transparent,
 * not hide status bar
 * not hide navigation bar
 */
fun Activity.showSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    val flag = window.decorView.systemUiVisibility
    val lightStatus = lightStatusBar ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) != 0)
    val lightNavigation = lightNavigationBar
        ?: ((flag and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) != 0)
    var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    if (lightStatus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }
    if (lightNavigation && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
    window.navigationBarColor = Color.TRANSPARENT
    window.decorView.systemUiVisibility = flags
}

/**
 * @return status bar height
 */
fun Activity.getStatusBarHeight(): Int {
    var height = 0
    try {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            height = applicationContext.resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        height = resources.getDimension(R.dimen.status_bar_height).toInt()
    }
    return height
}

/**
 * hide soft keyboard
 */
fun Activity.hideSoftInput() {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
    window.decorView.let {
        imm?.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * show soft keyboard
 */
fun Activity.showSoftInput(view: View? = null) {
    val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
    (view ?: window.decorView).let {
        imm?.showSoftInput(it, 0)
    }
}

/**
 * @return is navigation show
 */
@SuppressLint("ObsoleteSdkInt")
fun Activity.isNavigationBarShow(): Boolean {
    val hasNavigationBar = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val display = windowManager.defaultDisplay
        val size = Point()
        val realSize = Point()
        display.getSize(size)
        display.getRealSize(realSize)
        realSize.y != size.y
    } else {
        !(ViewConfiguration.get(this).hasPermanentMenuKey()
                || KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK))
    }
    val systemUiVisibility = window.decorView.systemUiVisibility
    val show = (systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0
    Log.d("ActivityExt", "has navigation bar=$hasNavigationBar   navigation bar is show=$show")
    return hasNavigationBar && show
}

/**
 * @return navigation bar height
 */
fun Activity.getNavigationBarHeight(): Int {
    var height = 0
    try {
        if (isNavigationBarShow()) {
            val resourceId: Int = resources.getIdentifier(
                "navigation_bar_height",
                "dimen", "android"
            )
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        height = resources.getDimension(R.dimen.navigation_bar_height).toInt()
    }
    return height
}

/**
 * @return screen height
 */
fun Activity.getScreenHeight(): Int {
    return getScreenSize().y
}

/**
 * @return screen height
 */
fun Activity.getScreenWidth(): Int {
    return getScreenSize().x
}

/**
 * @return screen size
 */
fun Activity.getScreenSize(): Point {
    val point = Point()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display?.getRealSize(point)
    }
    if (point.x == 0 || point.y == 0) {
        windowManager.defaultDisplay.getRealSize(point)
    }
    if (point.x == 0 || point.y == 0) {
        point.x = resources.displayMetrics.widthPixels
        point.y = resources.displayMetrics.heightPixels
    }
    return point
}
