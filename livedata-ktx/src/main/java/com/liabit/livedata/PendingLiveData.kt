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
    private val mObserverMap by lazy { ArrayMap<Observer<in T>, ObserverWrapper<in T>>() }

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
        val wrapper = ObserverWrapper(observer, mPendingWhenObserve ?: (value != null))
        mObserverMap[observer] = wrapper
        super.observe(owner, wrapper)
    }

    override fun observeForever(observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer, mPendingWhenObserve ?: (value != null))
        mObserverMap[observer] = wrapper
        super.observeForever(wrapper)
    }

    @MainThread
    override fun removeObserver(observer: Observer<in T>) {
        val wrapper = mObserverMap.remove(observer)
        if (wrapper != null) {
            super.removeObserver(observer)
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        for (i in 0 until mObserverMap.size) {
            mObserverMap.valueAt(i).set(true)
        }
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun clear() {
        super.setValue(null)
    }

    private class ObserverWrapper<T>(private val observer: Observer<in T>, pending: Boolean) : Observer<T> {

        private val mPending = AtomicBoolean(pending)

        fun set(newValue: Boolean) {
            mPending.set(newValue)
        }

        override fun onChanged(t: T) {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }
    }
}