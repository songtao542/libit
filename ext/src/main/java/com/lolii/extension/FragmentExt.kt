package com.lolii.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import com.lolii.ext.R

/**
 *  extend the layout to the status bar,
 *  not hide status bar,
 *  not extend the layout to navigation bar,
 *  not hide navigation bar
 */
fun Fragment.layoutUnderStatusBar(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    activity?.layoutUnderStatusBar(lightStatusBar, lightNavigationBar)
}

/**
 *  extend the layout to the status bar nad navigation bar,
 *  set the navigation bar color to transparent,
 *  not hide status bar,
 *  not hide navigation bar
 */
fun Fragment.layoutUnderSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    activity?.layoutUnderSystemUI(lightStatusBar, lightNavigationBar)
}

/**
 *  extend the layout to the status bar and navigation bar,
 *  not hide status bar,
 *  hide navigation bar
 */
fun Fragment.layoutUnderStatusBarAndHideNavigation(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    activity?.layoutUnderStatusBarAndHideNavigation(lightStatusBar, lightNavigationBar)
}

/**
 * extend the layout to the status bar and navigation bar,
 * set the navigation bar color to transparent,
 * hide status bar
 * hide navigation bar
 */
fun Fragment.hideSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    activity?.hideSystemUI(lightStatusBar, lightNavigationBar)
}

/**
 * extend the layout to the status bar and navigation bar,
 * set the navigation bar color to transparent,
 * not hide status bar
 * not hide navigation bar
 */
fun Fragment.showSystemUI(
    lightStatusBar: Boolean? = null,
    lightNavigationBar: Boolean? = null
) {
    activity?.showSystemUI(lightStatusBar, lightNavigationBar)
}

/**
 * @return status bar height
 */
fun Fragment.getStatusBarHeight(): Int {
    return activity?.getStatusBarHeight()
        ?: resources.getDimension(R.dimen.status_bar_height).toInt()
}

/**
 * hide soft keyboard
 */
fun Fragment.hideSoftInput() {
    activity?.hideSoftInput()
}

/**
 * show soft keyboard
 */
fun Fragment.showSoftInput(view: View? = null) {
    activity?.showSoftInput(view)
}

/**
 * @return is navigation show
 */
@SuppressLint("ObsoleteSdkInt")
fun Fragment.isNavigationBarShow(): Boolean {
    return activity?.isNavigationBarShow() ?: false
}

/**
 * @return navigation bar height
 */
fun Fragment.getNavigationBarHeight(): Int {
    return activity?.getNavigationBarHeight() ?: 0
}

/**
 * @return screen height
 */
fun Fragment.getScreenWidth(): Int {
    return activity?.getScreenWidth() ?: resources.displayMetrics.widthPixels
}

/**
 * @return screen height
 */
fun Fragment.getScreenHeight(): Int {
    return activity?.getScreenHeight() ?: resources.displayMetrics.heightPixels
}

/**
 * @return Returns true if the intent has been scheduled for delivery to one or more
 * broadcast receivers.  (Note tha delivery may not ultimately take place if one of those
 * receivers is unregistered before it is dispatched.)
 */
fun Fragment.sendLocalBroadcast(intent: Intent): Boolean {
    return activity?.sendLocalBroadcast(intent) ?: false
}


