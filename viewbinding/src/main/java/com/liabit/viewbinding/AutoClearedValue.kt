package com.liabit.viewbinding

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.Closeable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates an [AutoClearedValue] associated with this LifecycleOwner.
 */
inline fun <reified T> autoCleared(): AutoClearedValue<T> {
    return AutoClearedValue { newInstance(T::class.java) }
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
 * Creates an [AutoClearedValue] associated with this LifecycleOwner.
 */
fun <T> autoCleared(value: T) = AutoClearedValue { value }

/**
 * A lazy property that gets cleaned up when the fragment is destroyed.
 *
 * Accessing this variable in a destroyed fragment will throw NPE.
 */
class AutoClearedValue<T>(private var valueProvider: () -> T) : ReadWriteProperty<LifecycleOwner, T> {
    private var thisRef: LifecycleOwner? = null
    private var value: T? = null
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            thisRef?.lifecycle?.removeObserver(this)
            value?.let {
                if (it is Clearable) {
                    it.clear()
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
        this.thisRef = thisRef
        thisRef.lifecycle.addObserver(lifecycleObserver)
        return valueProvider.invoke().also { value = it }
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        this.value = value
    }
}

interface Clearable {
    fun clear()
}
