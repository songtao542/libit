package com.liabit.autoclear

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * ```
 * 使用示例
 * val request by register<String, Boolean>(ActivityResultContracts.RequestPermission())
 * ```
 * Creates an [AutoUnregisterForActivityResult] associated with this LifecycleOwner.
 */
inline fun <reified I, reified O> LifecycleOwner.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForActivityResult<I, O> {
    return AutoUnregisterForActivityResult(this, contract, callback)
}

/**
 * ```
 * 使用示例
 * val request by register<String, Boolean>(ActivityResultContracts.RequestPermission())
 * ```
 * Creates an [AutoUnregisterForActivityResult] associated with this Fragment.
 */
inline fun <reified I, reified O> Fragment.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForActivityResult<I, O> {
    return AutoUnregisterForActivityResult(this, contract, callback)
}

/**
 * ```
 * 使用示例
 * val request by register<String, Boolean>(ActivityResultContracts.RequestPermission())
 * ```
 * Creates an [AutoUnregisterForActivityResult] associated with this ComponentActivity.
 */
inline fun <reified I, reified O> ComponentActivity.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForActivityResult<I, O> {
    return AutoUnregisterForActivityResult(this, contract, callback)
}

/**
 * ```
 * 使用示例
 * val request by register<String, Boolean>(ActivityResultContracts.RequestPermission())
 * ```
 */
class AutoUnregisterForActivityResult<I, O>(
    lifecycleOwner: LifecycleOwner,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var mValue: ActivityResultLauncherProxy<I, O>? = null
    private var mCallback: WeakReference<ActivityResultCallback<O>>? = null

    init {
        val activityResultLauncher = when (lifecycleOwner) {
            is Fragment -> {
                lifecycleOwner.registerForActivityResult(contract, this)
            }
            is ComponentActivity -> {
                lifecycleOwner.registerForActivityResult(contract, this)
            }
            is ActivityResultCaller -> {
                lifecycleOwner.registerForActivityResult(contract, this)
            }
            else -> {
                throw IllegalStateException("${lifecycleOwner.javaClass.simpleName} not support registerForActivityResult")
            }
        }
        mValue = ActivityResultLauncherProxy(activityResultLauncher)
        if (callback != null) {
            mCallback = WeakReference(callback)
        }
        lifecycleOwner.lifecycle.addObserver(this)
    }

    override fun onActivityResult(result: O) {
        val callback = mValue?.callback ?: mCallback?.get() ?: return
        callback.onActivityResult(result)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        mValue?.unregister()
        mValue = null
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): ActivityResultLauncherProxy<I, O>? {
        return mValue
    }
}

class ActivityResultLauncherProxy<I, O>(private val proxy: ActivityResultLauncher<I>) : ActivityResultLauncher<I>() {
    private var mCallback: ActivityResultCallback<O>? = null

    val callback: ActivityResultCallback<O>? get() = mCallback

    override fun launch(input: I, options: ActivityOptionsCompat?) {
        proxy.launch(input, options)
    }

    override fun launch(input: I) {
        super.launch(input)
    }

    fun launch(input: I, options: ActivityOptionsCompat, callback: ActivityResultCallback<O>) {
        mCallback = callback
        proxy.launch(input, options)
    }

    fun launch(options: ActivityOptionsCompat, callback: ActivityResultCallback<O>) {
        mCallback = callback
        proxy.launch(null, options)
    }

    fun launch(input: I, callback: ActivityResultCallback<O>) {
        mCallback = callback
        proxy.launch(input)
    }

    fun launch(callback: ActivityResultCallback<O>) {
        mCallback = callback
        proxy.launch(null)
    }

    override fun unregister() {
        proxy.unregister()
    }

    override fun getContract(): ActivityResultContract<I, *> {
        return proxy.contract
    }
}

internal class CallbackProxy<I, O> : ActivityResultCallback<O> {

    private var mActivityResultLauncherProxy: ActivityResultLauncherProxy<I, O>? = null

    fun setActivityResultLauncherProxy(launcher: ActivityResultLauncherProxy<I, O>) {
        mActivityResultLauncherProxy = launcher
    }

    override fun onActivityResult(result: O) {
        mActivityResultLauncherProxy?.callback?.onActivityResult(result)
    }
}

/**
 * 必须在 [Lifecycle.State.STARTED] 之前调用, 所以最好在 [ComponentActivity.onCreate] 中调用
 */
fun <I, O> ComponentActivity.registerForActivityResult(contract: ActivityResultContract<I, O>): ActivityResultLauncherProxy<I, O> {
    val callback = CallbackProxy<I, O>()
    val launcher = registerForActivityResult(contract, callback)
    val launcherProxy = ActivityResultLauncherProxy<I, O>(launcher)
    callback.setActivityResultLauncherProxy(launcherProxy)
    return launcherProxy
}

/**
 * 必须在 [Lifecycle.State.STARTED] 之前调用, 所以最好在 [Fragment.onCreate] 中调用
 */
fun <I, O> Fragment.registerForActivityResult(contract: ActivityResultContract<I, O>): ActivityResultLauncherProxy<I, O> {
    val callback = CallbackProxy<I, O>()
    val launcher = registerForActivityResult(contract, callback)
    val launcherProxy = ActivityResultLauncherProxy<I, O>(launcher)
    callback.setActivityResultLauncherProxy(launcherProxy)
    return launcherProxy
}


