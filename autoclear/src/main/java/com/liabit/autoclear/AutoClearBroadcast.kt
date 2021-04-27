@file:Suppress("unused")

package com.liabit.autoclear

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.ArrayMap
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager

/**
 * 使用示例
 * ```
 * class YourFragment : Fragment() {
 *     // 配合 autoClear 使用, 当 Activity 或 Fragment onDestroy/onDestroyView 时会自动反注册广播
 *     val broadcastRegistry by autoClear { BroadcastRegistry() }
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         broadcastRegistry.registerReceiver(this, "action") { ctx, intent ->
 *             Log.d("TAG", "received broadcast")
 *         }
 *     }
 * }
 * ```
 * or
 * ```
 * class YourFragment : Fragment() {
 *     val broadcastRegistry = BroadcastRegistry(true)
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         broadcastRegistry.registerReceiver(this, "action") { ctx, intent ->
 *             Log.d("TAG", "received broadcast")
 *         }
 *     }
 * }
 * ```
 * or
 * ```
 * class YourFragment : Fragment() {
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *         // 注册广播接收器，该广播接收器会保存在一个全局的容器里，并将在 onDestroyView 时进行反注册
 *         registerReceiver({ ctx, intent ->
 *             Log.d("TAG", "received broadcast")
 *         }, "action")
 *     }
 * }
 * ```
 *
 */
open class BroadcastRegistry(autoClear: Boolean) : LifecycleSensitiveClearable, Clearable, DefaultLifecycleObserver {

    /**
     * 如果为 true 则会根据注册者（Activity，Fragment）的生命周期进行自动反注册
     */
    private var mAutoClear = autoClear

    private val receivers = ArrayList<Entry>()

    constructor() : this(false)

    class Entry(val key: Any, val value: BroadcastReceiver, var local: Boolean = true, var lifecycleOwner: LifecycleOwner? = null)

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        clear(owner)
    }

    /**
     * 只反注册该 LifecycleOwner 对应的广播
     */
    override fun clear(owner: LifecycleOwner) {
        val iterator = receivers.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            // 只反注册该 LifecycleOwner 对应的广播
            if (entry.lifecycleOwner == owner) {
                unregisterReceiver(entry)
                // 将 entry 从列表移除
                iterator.remove()
            }
        }
    }

    /**
     * 反注册所有BroadcastReceiver
     */
    override fun clear() {
        for (entry in receivers) {
            unregisterReceiver(entry)
        }
        receivers.clear()
    }

    /**
     * 反注册所有与该 [key] 绑定的 BroadcastReceiver
     */
    open fun unregisterReceiver(key: Any) {
        val iterator = receivers.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.key == key) {
                unregisterReceiver(entry)
                // 将 entry 从列表移除
                iterator.remove()
            }
        }
    }

    /**
     * 反注册当前 [entry] 对应的 BroadcastReceiver
     */
    private fun unregisterReceiver(entry: Entry) {
        val key = entry.key
        if (key is Context) {
            LocalBroadcastManager.getInstance(key).unregisterReceiver(entry.value)
        } else if (key is Fragment) {
            key.context?.let {
                LocalBroadcastManager.getInstance(it).unregisterReceiver(entry.value)
            }
        }
    }

    /****本地广播注册*****************************************/

    /**
     * 注册 Local BroadcastReceiver ，fragment 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 进行反注册
     */
    open fun registerReceiver(fragment: Fragment, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        registerReceiver(fragment, listOf(action), receiver)
    }

    /**
     * 注册 Local BroadcastReceiver，[fragment] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [fragment] 作为参数进行反注册
     */
    open fun registerReceiver(fragment: Fragment, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        val context = fragment.context ?: return
        register(fragment, true, context, actions, receiver)
    }

    /**
     * 注册 Local BroadcastReceiver，[context] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [context] 作为参数进行反注册
     */
    open fun registerReceiver(context: Context, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        registerReceiver(context, listOf(action), receiver)
    }

    /**
     * 注册 Local BroadcastReceiver，[context] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [context] 作为参数进行反注册
     */
    open fun registerReceiver(context: Context, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        register(context, true, context, actions, receiver)
    }

    /****非本地广播注册*****************************************/

    /**
     * 注册 BroadcastReceiver ，fragment 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 进行反注册
     */
    open fun registerGlobalReceiver(fragment: Fragment, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        registerGlobalReceiver(fragment, listOf(action), receiver)
    }

    /**
     * 注册 BroadcastReceiver，[fragment] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [fragment] 作为参数进行反注册
     */
    open fun registerGlobalReceiver(fragment: Fragment, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        val context = fragment.context ?: return
        register(fragment, false, context, actions, receiver)
    }

    /**
     * 注册 BroadcastReceiver，[context] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [context] 作为参数进行反注册
     */
    open fun registerGlobalReceiver(context: Context, action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        registerGlobalReceiver(context, listOf(action), receiver)
    }

    /**
     * 注册 BroadcastReceiver，[context] 作为 key 绑定一个对应的 BroadcastReceiver
     * 可以通过 [unregisterReceivers] 传入 [context] 作为参数进行反注册
     */
    open fun registerGlobalReceiver(context: Context, actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
        register(context, false, context, actions, receiver)
    }

    /**
     * @param key      与 [receiver] 绑定的 key
     * @param local    是否注册本地广播
     * @param context  用于注册广播的 Context
     * @param actions
     * @param receiver
     */
    private fun register(key: Any, local: Boolean, context: Context, actions: List<String>, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val realReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                receiver.invoke(context, intent)
            }
        }
        val entry = Entry(key, realReceiver, local)
        receivers.add(entry)
        val intentFilter = IntentFilter()
        for (action in actions) {
            intentFilter.addAction(buildAction(context, action))
        }
        if (mAutoClear) {
            if (key is ComponentActivity) {
                entry.lifecycleOwner = key
                key.lifecycle.addObserver(this)
            } else if (key is Fragment) {
                entry.lifecycleOwner = key.viewLifecycleOwner
                key.viewLifecycleOwner.lifecycle.addObserver(this)
            }
        }
        if (local) {
            LocalBroadcastManager.getInstance(context).registerReceiver(realReceiver, intentFilter)
        } else {
            context.registerReceiver(realReceiver, intentFilter)
        }
    }

    /**
     * 反注册所有 key 为 [fragment] 的广播接收器
     */
    open fun unregisterReceivers(fragment: Fragment) {
        unregisterReceiver(fragment)
    }

    /**
     * 反注册所有 key 为 [context] 的广播接收器
     */
    open fun unregisterReceivers(context: Context) {
        unregisterReceiver(context)
    }
}

private fun buildAction(context: Context, action: String): String {
    return "${context.packageName}_$action"
}

private object GlobalBroadcastRegistry {

    private val registries = ArrayMap<Any, BroadcastRegistry>()

    fun unregisterReceiver(key: Any) {
        registries[key]?.clear()
    }

    fun registerReceiver(context: Context, local: Boolean, action: String, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[context] ?: BroadcastRegistry().also { registries[context] = it }
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(registry)
        }
        if (local) {
            registry.registerReceiver(context, action, receiver)
        } else {
            registry.registerGlobalReceiver(context, action, receiver)
        }
    }

    fun registerReceiver(context: Context, local: Boolean, actions: List<String>, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[context] ?: BroadcastRegistry().also { registries[context] = it }
        if (context is LifecycleOwner) {
            context.lifecycle.addObserver(registry)
        }
        if (local) {
            registry.registerReceiver(context, actions, receiver)
        } else {
            registry.registerGlobalReceiver(context, actions, receiver)
        }
    }

    fun registerReceiver(fragment: Fragment, local: Boolean, action: String, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[fragment] ?: BroadcastRegistry().also { registries[fragment] = it }
        fragment.viewLifecycleOwner.lifecycle.addObserver(registry)
        if (local) {
            registry.registerReceiver(fragment, action, receiver)
        } else {
            registry.registerGlobalReceiver(fragment, action, receiver)
        }
    }

    fun registerReceiver(fragment: Fragment, local: Boolean, actions: List<String>, receiver: (context: Context?, intent: Intent?) -> Unit) {
        val registry = registries[fragment] ?: BroadcastRegistry().also { registries[fragment] = it }
        fragment.viewLifecycleOwner.lifecycle.addObserver(registry)
        if (local) {
            registry.registerReceiver(fragment, actions, receiver)
        } else {
            registry.registerGlobalReceiver(fragment, actions, receiver)
        }
    }
}

/**
 * 注册本地广播接收器
 */
fun Context.registerReceiver(action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, action, receiver)
}

/**
 * 注册本地广播接收器
 */
fun Context.registerReceiver(actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, actions, receiver)
}

/**
 * 注册本地广播接收器
 */
fun Context.registerReceiver(actions: Array<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, actions.toList(), receiver)
}

/**
 * 注册广播接收器
 */
fun Context.registerGlobalReceiver(action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, action, receiver)
}

/**
 * 注册广播接收器
 */
fun Context.registerGlobalReceiver(actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, actions, receiver)
}

/**
 * 注册广播接收器
 */
fun Context.registerGlobalReceiver(actions: Array<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, actions.toList(), receiver)
}

/**
 * 反注册广播接收器
 */
fun Context.unregisterReceiver() {
    GlobalBroadcastRegistry.unregisterReceiver(this)
}

/**
 * 注册本地广播接收器
 */
fun Fragment.registerReceiver(action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, action, receiver)
}

/**
 * 注册本地广播接收器
 */
fun Fragment.registerReceiver(actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, actions, receiver)
}

/**
 * 注册本地广播接收器
 */
fun Fragment.registerReceiver(actions: Array<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, true, actions.toList(), receiver)
}

/**
 * 注册广播接收器
 */
fun Fragment.registerGlobalReceiver(action: String, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, action, receiver)
}

/**
 * 注册广播接收器
 */
fun Fragment.registerGlobalReceiver(actions: List<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, actions, receiver)
}

/**
 * 注册广播接收器
 */
fun Fragment.registerGlobalReceiver(actions: Array<String>, receiver: ((context: Context?, intent: Intent?) -> Unit)) {
    GlobalBroadcastRegistry.registerReceiver(this, false, actions.toList(), receiver)
}

/**
 * 反注册广播接收器
 */
fun Fragment.unregisterReceiver() {
    GlobalBroadcastRegistry.unregisterReceiver(this)
}

/**
 * 注册本地广播接收器，不会自动反注册
 */
fun Context.registerReceiver(receiver: BroadcastReceiver, vararg action: String) {
    val intentFilter = IntentFilter()
    for (act in action) {
        intentFilter.addAction(buildAction(this, act))
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter)
}

/**
 * 注册广播接收器，不会自动反注册
 */
fun Context.registerGlobalReceiver(receiver: BroadcastReceiver, vararg action: String) {
    val intentFilter = IntentFilter()
    for (act in action) {
        intentFilter.addAction(buildAction(this, act))
    }
    try {
        registerReceiver(receiver, intentFilter)
    } catch (e: Throwable) {
    }
}

/**
 * 反注册广播接收器
 */
fun Context.unregisterReceiver(receiver: BroadcastReceiver, local: Boolean = true) {
    try {
        if (local) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        } else {
            unregisterReceiver(receiver)
        }
    } catch (e: Throwable) {
    }
}

class BroadcastBuilder(private val context: Context) : Intent() {

    var local = true

    fun send() {
        if (local) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(this)
        } else {
            context.sendBroadcast(this)
        }
    }
}

/**
 * 发送广播
 */
fun Context.broadcast(action: String): BroadcastBuilder {
    return BroadcastBuilder(this).also {
        it.action = buildAction(this, action)
    }
}

/**
 * 发送广播
 */
fun Fragment.broadcast(action: String): BroadcastBuilder? {
    return context?.broadcast(action)
}

/**
 * 发送广播
 */
fun Context.broadcast(action: String, local: Boolean): BroadcastBuilder {
    return BroadcastBuilder(this).also {
        it.local = local
        it.action = buildAction(this, action)
    }
}

/**
 * 发送广播
 */
fun Fragment.broadcast(action: String, local: Boolean): BroadcastBuilder? {
    return context?.broadcast(action, local)
}