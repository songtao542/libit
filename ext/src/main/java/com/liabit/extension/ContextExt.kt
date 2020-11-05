package com.liabit.extension

import android.content.Context
import android.content.Intent
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