package com.liabit.livedata

import android.util.ArrayMap
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 */
@Suppress("unused")
open class PendingLiveData<T> : MutableLiveData<T> {

    private var mPendingWhenObserve: Boolean? = null
    private val mPendingMap = ArrayMap<ObserverWrapper<T>, AtomicBoolean>()

    constructor() : super()

    constructor(value: T) : super(value)

    constructor(pendingWhenObserve: Boolean) : super() {
        mPendingWhenObserve = pendingWhenObserve
    }

    constructor(pendingWhenObserve: Boolean, value: T) : super(value) {
        mPendingWhenObserve = pendingWhenObserve
    }

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Observe the internal MutableLiveData
        val pending = AtomicBoolean(mPendingWhenObserve ?: (value != null))
        val observerWrapper = ObserverWrapper(observer, pending)
        super.observe(owner, observerWrapper)
        mPendingMap[observerWrapper] = pending
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        super.removeObserver(observer)
        if (observer is ObserverWrapper<*>) {
            mPendingMap.remove(observer)
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        t?.let {
            for (i in 0 until mPendingMap.size) {
                mPendingMap.valueAt(i).set(true)
            }
        }
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun clear() {
        value = null
    }

    private class ObserverWrapper<T>(
        private val observer: Observer<in T>,
        private val pending: AtomicBoolean
    ) : Observer<T> {
        override fun onChanged(t: T) {
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
}