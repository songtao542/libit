@file:Suppress("unused")

package com.liabit.autoclear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.ArrayMap
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 *
 */
open class BroadcastRegistry : Clearable, DefaultLifecycleObserver {

    private val receivers = ArrayMap<Context, BroadcastReceiver>()

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        clear()
    }

    override fun clear() {
        for ((context, receiver) in receivers) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
        receivers.clear()
    }

    open fun clear(context: Context) {
        val iterator = receivers.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == context) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(entry.value)
                iterator.remove()
            }
        }
    }

    open fun registerReceiver(context: Context, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        registerReceiver(context, listOf(action), receiver)
    }

    open fun registerReceiver(context: Context, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
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
        LocalBroadcastManager.getInstance(context).registerReceiver(realReceiver, intentFilter)
    }
}

private fun buildAction(context: Context, action: String): String {
    return "${context.packageName}_$action"
}

private object GlobalBroadcastRegistry : BroadcastRegistry() {

    private val registries = ArrayMap<Context, BroadcastRegistry>()

    override fun clear(context: Context) {
        registries[context]?.clear()
    }

    override fun registerReceiver(context: Context, action: String, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[context] ?: BroadcastRegistry().also { registries[context] = it }
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(registry)
        }
        registry.registerReceiver(context, action, receiver)
    }

    override fun registerReceiver(context: Context, actions: List<String>, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[context] ?: BroadcastRegistry().also { registries[context] = it }
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(registry)
        }
        registry.registerReceiver(context, actions, receiver)
    }
}

fun Context.registerReceiver(receiver: ((context: Context?, intent: Intent?) -> Unit), vararg action: String) {
    GlobalBroadcastRegistry.registerReceiver(this, listOf(*action), receiver)
}

fun Context.clearBroadcastReceiver() {
    GlobalBroadcastRegistry.clear(this)
}

fun Context.registerReceiver(receiver: BroadcastReceiver, action: String) {
    LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(buildAction(this, action)))
}

fun Context.unregisterLocalBroadcastReceiver(receiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
}

class BroadcastBuilder(private val context: Context) : Intent() {
    fun send() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(this)
    }
}

fun Context.broadcast(action: String): BroadcastBuilder {
    return BroadcastBuilder(this).also {
        it.action = buildAction(this, action)
    }
}

fun Fragment.broadcast(action: String): BroadcastBuilder? {
    return context?.broadcast(action)
}