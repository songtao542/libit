package com.liabit.autoclear

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleOwner
import java.io.Closeable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates an [AutoClearValue] associated with this LifecycleOwner.
 */
inline fun <reified T> autoClear(): AutoClearValue<T> {
    return AutoClearValue { newInstance(T::class.java) }
}

/**
 * 反射方式创建对象
 */
inline fun <reified T> newInstance(valueClass: Class<T>): T {
    try {
        return valueClass.getConstructor().newInstance()
    } catch (e: Throwable) {
        throw RuntimeException("Cannot create an instance of $valueClass", e)
    }
}

/**
 * Creates an [AutoClearValue] associated with this LifecycleOwner.
 */
fun <T> autoClear(value: T) = AutoClearValue { value }

/**
 * Creates an [AutoClearValue] associated with this LifecycleOwner.
 */
@Suppress("unused")
fun <T> autoClear(valueProvider: () -> T) = AutoClearValue(valueProvider)

/**
 * A lazy property that gets cleaned up when the LifecycleOwner is at state of [DESTROYED].
 *
 * Accessing this variable at state of [DESTROYED] LifecycleOwner will result in variable leak.
 *
 * If the [T] is instance of [Clearable] or [LifecycleSensitiveClearable], the [Clearable.clear] method will be invoke before clean up.
 * If the [T] is instance of [Closeable] or [AutoCloseable], the [Closeable.close] method will be invoke before clean up.
 *
 * @param valueProvider The initializer of the variable, when get the variable, if the variable value is null, the initializer will be called to initialize.
 */
class AutoClearValue<T>(private var valueProvider: () -> T) : ReadWriteProperty<LifecycleOwner, T> {
    private var value: T? = null
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        @SuppressLint("ObsoleteSdkInt")
        override fun onDestroy(owner: LifecycleOwner) {
            owner.lifecycle.removeObserver(this)
            value?.let {
                if (it is LifecycleSensitiveClearable) {
                    try {
                        it.clear(owner)
                    } catch (e: Throwable) {
                        Log.d("AutoClearedValue", "clear error: ", e)
                    }
                } else if (it is Clearable) {
                    try {
                        it.clear()
                    } catch (e: Throwable) {
                        Log.d("AutoClearedValue", "clear error: ", e)
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    if (it is AutoCloseable) {
                        try {
                            it.close()
                        } catch (e: Throwable) {
                            Log.d("AutoClearedValue", "close error: ", e)
                        }
                    }
                } else {
                    if (it is Closeable) {
                        try {
                            it.close()
                        } catch (e: Throwable) {
                            Log.d("AutoClearedValue", "close error: ", e)
                        }
                    }
                }
            }
            value = null
        }
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        value?.let { return it }
        val lifecycle = if (thisRef is Fragment) thisRef.viewLifecycleOwner.lifecycle else thisRef.lifecycle
        lifecycle.addObserver(lifecycleObserver)
        return valueProvider.invoke().also { value = it }
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        val lifecycle = if (thisRef is Fragment) thisRef.viewLifecycleOwner.lifecycle else thisRef.lifecycle
        lifecycle.addObserver(lifecycleObserver)
        this.value = value
    }
}

interface Clearable {
    fun clear()
}

interface LifecycleSensitiveClearable {
    fun clear(owner: LifecycleOwner)
}
