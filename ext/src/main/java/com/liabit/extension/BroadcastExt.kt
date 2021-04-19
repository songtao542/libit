package com.liabit.extension

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.parcel.Parcelize

const val ACTION = "com.snt.phoney.ACTION."
const val EXTRA_EVENT = "extra_event"



fun buildAction(action: String): String {
    return "$ACTION$action"
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

@Parcelize
data class Event(
        var id: Int = 0,
        var action: String,
        var message: String? = null
) : Parcelable
