package com.liabit.autoclear

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Creates an [AutoUnregisterForLifecycleOwner] associated with this LifecycleOwner.
 */
inline fun <reified I, reified O> LifecycleOwner.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForLifecycleOwner<I, O> {
    return AutoUnregisterForLifecycleOwner(this, contract, callback)
}

class AutoUnregisterForLifecycleOwner<I, O>(
    lifecycleOwner: LifecycleOwner,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var mValue: ActivityResultLauncherProxy<I, O>? = null
    private var mCallback: WeakReference<ActivityResultCallback<O>>? = null

    init {
        val activityResultLauncher = when (this) {
            is Fragment -> {
                this.registerForActivityResult(contract, this)
            }
            is ComponentActivity -> {
                this.registerForActivityResult(contract, this)
            }
            else -> {
                throw IllegalStateException("${this.javaClass.simpleName} not support registerForActivityResult")
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

/**
 * Creates an [AutoUnregisterForFragment] associated with this Fragment.
 */
inline fun <reified I, reified O> Fragment.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForFragment<I, O> {
    return AutoUnregisterForFragment(this, contract, callback)
}

class AutoUnregisterForFragment<I, O>(
    fragment: Fragment,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var mValue: ActivityResultLauncherProxy<I, O>? = null
    private var mCallback: WeakReference<ActivityResultCallback<O>>? = null

    init {
        mValue = ActivityResultLauncherProxy(fragment.registerForActivityResult(contract, this))
        if (callback != null) {
            mCallback = WeakReference(callback)
        }
        fragment.lifecycle.addObserver(this)
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

/**
 * Creates an [AutoUnregisterForActivity] associated with this ComponentActivity.
 */
inline fun <reified I, reified O> ComponentActivity.register(
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
): AutoUnregisterForActivity<I, O> {
    return AutoUnregisterForActivity(this, contract, callback)
}

class AutoUnregisterForActivity<I, O>(
    activity: ComponentActivity,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var mValue: ActivityResultLauncherProxy<I, O>? = null
    private var mCallback: WeakReference<ActivityResultCallback<O>>? = null

    init {
        mValue = ActivityResultLauncherProxy(activity.registerForActivityResult(contract, this))
        if (callback != null) {
            mCallback = WeakReference(callback)
        }
        activity.lifecycle.addObserver(this)
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