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
open class PendingLiveData<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)
    private val mPendingMap = ArrayMap<ObserverWrapper<T>, AtomicBoolean>()

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Observe the internal MutableLiveData
        val pending = AtomicBoolean(mPending.get())
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
            mPending.set(true)
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