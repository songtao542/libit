package com.liabit.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.parcel.Parcelize

fun buildAction(context: Context, action: String): String {
    return "${context.packageName}_$action"
}

fun Context.sendBroadcast(action: String, event: Event? = null) {
    val intent = Intent(buildAction(action)).apply {
        val ev = event?.apply { this.action = action } ?: Event(action = action)
        putExtra(EXTRA_EVENT, ev)
    }
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
}

@Suppress("unused")
fun Fragment.sendBroadcast(action: String, event: Event? = null) {
    context?.sendBroadcast(action, event)
}

fun Context.registerReceiver(receiver: BroadcastReceiver, action: String) {
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(buildAction(action)))
}

fun Context.unregisterLocalBroadcastReceiver(receiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}
