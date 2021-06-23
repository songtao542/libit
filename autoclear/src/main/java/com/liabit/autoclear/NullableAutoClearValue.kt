package com.liabit.autoclear

import android.annotation.SuppressLint
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.io.Closeable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Creates an [NullableAutoClearValue] associated with this LifecycleOwner.
 */
inline fun <reified T> autoClearValue(): NullableAutoClearValue<T> {
    return NullableAutoClearValue()
}

inline fun <reified T> autoClearValue(bindToViewLifecycleIfFragment: Boolean): NullableAutoClearValue<T> {
    return NullableAutoClearValue(bindToViewLifecycleIfFragment)
}

/**
 * 需要手动初始化值
 */
class NullableAutoClearValue<T>(
    private val bindToViewLifecycleIfFragment: Boolean = true
) : ReadWriteProperty<LifecycleOwner, T?> {
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
                        Log.d("NullableAutoClearValue", "clear error: ", e)
                    }
                } else if (it is Clearable) {
                    try {
                        it.clear()
                    } catch (e: Throwable) {
                        Log.d("NullableAutoClearValue", "clear error: ", e)
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    if (it is AutoCloseable) {
                        try {
                            it.close()
                        } catch (e: Throwable) {
                            Log.d("NullableAutoClearValue", "close error: ", e)
                        }
                    }
                } else {
                    if (it is Closeable) {
                        try {
                            it.close()
                        } catch (e: Throwable) {
                            Log.d("NullableAutoClearValue", "close error: ", e)
                        }
                    }
                }
            }
            value = null
        }
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T? {
        return value
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T?) {
        val lifecycle = if (thisRef is Fragment && bindToViewLifecycleIfFragment) {
            thisRef.viewLifecycleOwner.lifecycle
        } else {
            thisRef.lifecycle
        }
        if (value == null) {
            lifecycle.removeObserver(lifecycleObserver)
        } else {
            lifecycle.addObserver(lifecycleObserver)
        }
        this.value = value
    }
}