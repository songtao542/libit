package com.liabit.util

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.lifecycle.*
import com.liabit.util.LiveBus.ObserverWrapper
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

object LiveBus {
    private val bus: MutableMap<String, BusMutableLiveData<Any>> = HashMap()

    private fun <T> get(key: String): Observable<T> {
        synchronized(bus) {
            if (!bus.containsKey(key)) {
                bus[key] = BusMutableLiveData(key)
            }
        }
        @Suppress("UNCHECKED_CAST")
        return bus[key] as Observable<T>
    }

    fun <T> postValue(key: String, value: T) {
        get<T>(key).postValue(value)
    }

    fun <T> setValue(key: String, value: T) {
        get<T>(key).setValue(value)
    }

    fun <T> postValueDelay(key: String, value: T, delay: Long) {
        get<T>(key).postValueDelay(value, delay)
    }

    fun <T> postValueDelay(key: String, value: T, delay: Long, unit: TimeUnit) {
        get<T>(key).postValueDelay(value, delay, unit)
    }

    fun <T> observe(owner: LifecycleOwner, key: String, observer: (T) -> Unit) {
        get<T>(key).observe(owner, observer)
    }

    fun <T> observeForever(key: String, observer: Observer<in T>) {
        get<T>(key).observeForever(observer)
    }

    fun <T> observeSticky(owner: LifecycleOwner, key: String, observer: (T) -> Unit) {
        get<T>(key).observeSticky(owner, observer)
    }

    fun <T> observeStickyForever(key: String, observer: Observer<in T>) {
        get<T>(key).observeStickyForever(observer)
    }

    fun <T> removeObserver(key: String, observer: Observer<in T>) {
        get<T>(key).removeObserver(observer)
    }

    internal interface Observable<T> {
        fun setValue(value: T)
        fun postValue(value: T)
        fun postValueDelay(value: T, delay: Long)
        fun postValueDelay(value: T, delay: Long, unit: TimeUnit)

        fun observe(owner: LifecycleOwner, observer: Observer<in T>)
        fun observeForever(observer: Observer<in T>)
        fun observeSticky(owner: LifecycleOwner, observer: Observer<in T>)
        fun observeStickyForever(observer: Observer<in T>)
        fun removeObserver(observer: Observer<in T>)
    }

    private class BusMutableLiveData<T>(private val key: String) : MutableLiveData<T>(), Observable<T> {
        private val observerMap: MutableMap<Observer<in T>, Observer<T>> = HashMap()

        private val mainHandler = Handler(Looper.getMainLooper())

        private inner class PostValueTask(private val newValue: T?) : Runnable {
            override fun run() {
                value = newValue
            }
        }

        override fun postValue(value: T) {
            mainHandler.post(PostValueTask(value))
        }

        override fun postValueDelay(value: T, delay: Long) {
            mainHandler.postDelayed(PostValueTask(value), delay)
        }

        override fun postValueDelay(value: T, delay: Long, unit: TimeUnit) {
            postValueDelay(value, TimeUnit.MILLISECONDS.convert(delay, unit))
        }

        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            val safeCastObserver = SafeCastObserver(observer)
            //保存LifecycleOwner的当前状态
            val lifecycle: Lifecycle = owner.lifecycle
            val currentState: Lifecycle.State = lifecycle.currentState
            val observerSize = getLifecycleObserverMapSize(lifecycle)
            val needChangeState: Boolean = currentState.isAtLeast(Lifecycle.State.STARTED)
            if (needChangeState) {
                //把LifecycleOwner的状态改为INITIALIZED
                setLifecycleState(lifecycle, Lifecycle.State.INITIALIZED)
                //set observerSize to -1，否则super.observe(owner, observer)的时候会无限循环
                setLifecycleObserverMapSize(lifecycle, -1)
            }
            super.observe(owner, safeCastObserver)
            if (needChangeState) {
                //重置LifecycleOwner的状态
                setLifecycleState(lifecycle, currentState)
                //重置observer size，因为又添加了一个observer，所以数量+1
                setLifecycleObserverMapSize(lifecycle, observerSize + 1)
                //把Observer置为active
                hookObserverActive(safeCastObserver, true)
            }
            //更改Observer的version
            hookObserverVersion(safeCastObserver)
        }

        override fun observeSticky(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, SafeCastObserver(observer))
        }

        override fun observeForever(observer: Observer<in T>) {
            val wrapper = observerMap[observer] ?: ObserverWrapper(observer).also { observerMap[observer] = it }
            super.observeForever(wrapper)
        }

        override fun observeStickyForever(observer: Observer<in T>) {
            super.observeForever(observer)
        }

        override fun removeObserver(observer: Observer<in T>) {
            val realObserver: Observer<in T> = observerMap.remove(observer) ?: observer
            super.removeObserver(realObserver)
            if (!hasObservers()) {
                bus.remove(key)
            }
        }

        private fun setLifecycleObserverMapSize(lifecycle: Lifecycle?, size: Int) {
            if (lifecycle == null) {
                return
            }
            if (lifecycle !is LifecycleRegistry) {
                return
            }
            try {
                val observerMapField = LifecycleRegistry::class.java.getDeclaredField("mObserverMap")
                observerMapField.isAccessible = true
                val mObserverMap = observerMapField[lifecycle]
                val superclass: Class<*>? = mObserverMap.javaClass.superclass
                val mSizeField = superclass?.getDeclaredField("mSize")
                mSizeField?.isAccessible = true
                mSizeField?.set(mObserverMap, size)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun getLifecycleObserverMapSize(lifecycle: Lifecycle?): Int {
            if (lifecycle == null) {
                return 0
            }
            return if (lifecycle !is LifecycleRegistry) {
                0
            } else try {
                val observerMapField = LifecycleRegistry::class.java.getDeclaredField("mObserverMap")
                observerMapField.isAccessible = true
                val mObserverMap = observerMapField[lifecycle]
                val superclass: Class<*>? = mObserverMap.javaClass.superclass
                val mSizeField = superclass?.getDeclaredField("mSize")
                mSizeField?.isAccessible = true
                mSizeField?.get(mObserverMap) as Int
            } catch (e: Exception) {
                e.printStackTrace()
                0
            }
        }

        private fun setLifecycleState(lifecycle: Lifecycle?, state: Lifecycle.State) {
            if (lifecycle == null) {
                return
            }
            if (lifecycle !is LifecycleRegistry) {
                return
            }
            try {
                val mState = LifecycleRegistry::class.java.getDeclaredField("mState")
                mState.isAccessible = true
                mState[lifecycle] = state
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @Throws(Exception::class)
        private fun getObserverWrapper(@NonNull observer: Observer<T>): Any? {
            val fieldObservers: Field = LiveData::class.java.getDeclaredField("mObservers")
            fieldObservers.isAccessible = true
            val objectObservers = fieldObservers[this]
            val classObservers: Class<*> = objectObservers.javaClass
            val methodGet = classObservers.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            return objectWrapper
        }

        private fun hookObserverVersion(@NonNull observer: Observer<T>) {
            try {
                //get wrapper's version
                val objectWrapper = getObserverWrapper(observer) ?: return
                val classObserverWrapper: Class<*>? = objectWrapper.javaClass.superclass
                val fieldLastVersion = classObserverWrapper?.getDeclaredField("mLastVersion")
                fieldLastVersion?.isAccessible = true
                //get livedata's version
                val fieldVersion: Field = LiveData::class.java.getDeclaredField("mVersion")
                fieldVersion.isAccessible = true
                val objectVersion = fieldVersion[this]
                //set wrapper's version
                fieldLastVersion?.set(objectWrapper, objectVersion)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        @Suppress("SameParameterValue")
        private fun hookObserverActive(@NonNull observer: Observer<T>, active: Boolean) {
            try {
                //get wrapper's version
                val objectWrapper = getObserverWrapper(observer) ?: return
                val classObserverWrapper: Class<*>? = objectWrapper.javaClass.superclass
                val mActive = classObserverWrapper?.getDeclaredField("mActive")
                mActive?.isAccessible = true
                mActive?.set(objectWrapper, active)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private class ObserverWrapper<T> constructor(private val observer: Observer<in T>) : Observer<T> {

        override fun onChanged(t: T) {
            if (isCallOnObserve) {
                return
            }
            try {
                observer.onChanged(t)
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }

        private val isCallOnObserve: Boolean
            get() {
                val stackTrace = Thread.currentThread().stackTrace
                if (stackTrace.isNotEmpty()) {
                    for (element in stackTrace) {
                        if ("android.arch.lifecycle.LiveData" == element.className && "observeForever" == element.methodName) {
                            return true
                        }
                    }
                }
                return false
            }
    }

    private class SafeCastObserver<T> constructor(private val observer: Observer<in T>) : Observer<T> {
        override fun onChanged(t: T) {
            try {
                observer.onChanged(t)
            } catch (e: ClassCastException) {
                e.printStackTrace()
            }
        }
    }
}