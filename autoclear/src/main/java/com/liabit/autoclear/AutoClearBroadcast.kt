@file:Suppress("unused")

package com.liabit.autoclear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 *
 */
@Suppress("unused")
class AutoClearBroadcastRegistrar : Clearable {

    private var receivers = ArrayMap<Context, BroadcastReceiver>()

    override fun clear() {
        for ((context, receiver) in receivers) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
        receivers.clear()
    }

    fun registerReceiver(context: Context, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        val realReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                receiver.invoke(context, intent)
            }
        }
        receivers[context] = realReceiver
        LocalBroadcastManager.getInstance(context).registerReceiver(realReceiver, IntentFilter(buildAction(context, action)))
    }

    fun registerReceiver(context: Context, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        val realReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                receiver.invoke(context, intent)
            }
        }
        receivers[context] = realReceiver
        val intentFilter = IntentFilter()
        for (action in actions) {
            intentFilter.addAction(buildAction(context, action))
        }
        LocalBroadcastManager.getInstance(context).registerReceiver(realReceiver, IntentFilter())
    }
}

class BroadcastBuilder(private val context: Context) : Intent() {
    fun send() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(this)
    }
}

private fun buildAction(context: Context, action: String): String {
    return "${context.packageName}_$action"
}

fun Context.broadcast(action: String): BroadcastBuilder {
    return BroadcastBuilder(this).also {
        it.action = buildAction(this, action)
    }
}

fun Fragment.broadcast(action: String): BroadcastBuilder? {
    return context?.broadcast(action)
}

fun Context.registerReceiver(receiver: BroadcastReceiver, action: String) {
    LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(buildAction(this, action)))
}

fun Context.unregisterLocalBroadcastReceiver(receiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}