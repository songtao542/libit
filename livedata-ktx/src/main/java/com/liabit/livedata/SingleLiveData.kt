package com.liabit.livedata

import android.util.ArrayMap
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like SnackBar messages.
 * <p>
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 * <p>
 * Note that only one observer is going to be notified of changes.
 */
@Suppress("unused")
open class SingleLiveData<T> : MutableLiveData<T> {

    private val mPending = AtomicBoolean(false)

    private val mObserverMap by lazy { ArrayMap<Observer<in T>, ObserverWrapper<in T>>() }

    constructor() : super()

    constructor(value: T) : super(value)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Observe the internal MutableLiveData
        val wrapper = ObserverWrapper(observer)
        mObserverMap[observer] = wrapper
        super.observe(owner, wrapper)
    }

    override fun observeForever(observer: Observer<in T>) {
        val wrapper = ObserverWrapper(observer)
        mObserverMap[observer] = wrapper
        super.observeForever(wrapper)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val wrapper = mObserverMap.remove(observer)
        if (wrapper != null) {
            super.removeObserver(wrapper)
        }
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    fun setPending(pending: Boolean) {
        mPending.set(pending)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun clear() {
        super.setValue(null)
    }

    private inner class ObserverWrapper<T>(val observer: Observer<in T>) : Observer<T> {

        override fun onChanged(t: T) {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        }

    }
}