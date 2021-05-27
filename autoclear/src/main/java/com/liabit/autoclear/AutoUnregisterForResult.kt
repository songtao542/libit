package com.liabit.autoclear

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
    private val callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var value: ActivityResultLauncherProxy<I, O>? = null

    init {
        value = ActivityResultLauncherProxy(fragment.registerForActivityResult(contract, this))
        fragment.lifecycle.addObserver(this)
    }

    override fun onActivityResult(result: O) {
        val callback = value?.callback ?: callback ?: return
        callback.onActivityResult(result)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        value?.unregister()
        value = null
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): ActivityResultLauncherProxy<I, O>? {
        return value
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
    private val callback: ActivityResultCallback<O>? = null
) : ReadOnlyProperty<LifecycleOwner, ActivityResultLauncherProxy<I, O>?>, DefaultLifecycleObserver, ActivityResultCallback<O> {
    private var value: ActivityResultLauncherProxy<I, O>? = null

    init {
        value = ActivityResultLauncherProxy(activity.registerForActivityResult(contract, this))
        activity.lifecycle.addObserver(this)
    }

    override fun onActivityResult(result: O) {
        val callback = value?.callback ?: callback ?: return
        callback.onActivityResult(result)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
        value?.unregister()
        value = null
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): ActivityResultLauncherProxy<I, O>? {
        return value
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