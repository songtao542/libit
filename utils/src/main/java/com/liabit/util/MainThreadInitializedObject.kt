package com.liabit.util

import android.content.Context
import android.os.Looper
import com.liabit.util.Executors.MAIN_EXECUTOR
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException

/**
 * Author:         songtao
 * CreateDate:     2020/6/15 14:59
 */
class MainThreadInitializedObject<T>(private val mProvider: ObjectProvider<T>) {
    private var mValue: T? = null

    operator fun get(context: Context): T? {
        if (mValue == null) {
            mValue = if (Looper.myLooper() == Looper.getMainLooper()) {
                mProvider[context.applicationContext]
            } else {
                return try {
                    MAIN_EXECUTOR.submit(Callable { get(context) }).get()
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                } catch (e: ExecutionException) {
                    throw RuntimeException(e)
                }
            }
        }
        return mValue
    }

    interface ObjectProvider<T> {
        operator fun get(context: Context): T
    }
}
