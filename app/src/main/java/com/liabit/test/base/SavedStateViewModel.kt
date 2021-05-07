package com.liabit.test.base

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.liabit.test.base.AppViewModel

open class SavedStateViewModel(private val savedStateHandle: SavedStateHandle) : AppViewModel() {

    @CallSuper
    override fun onCreate(context: Context) {
        onRestoreInstanceState(savedStateHandle)
    }

    open fun onRestoreInstanceState(savedStateHandle: SavedStateHandle) {
    }

    fun <T> setValue(key: String, value: T) {
        savedStateHandle.set(key, value)
    }

    fun <T> set(key: String, value: T) {
        setValue(key, value)
    }

    fun <T> getLiveData(key: String, initialValue: T? = null): MutableLiveData<T> {
        return if (initialValue == null) {
            savedStateHandle.getLiveData(key)
        } else {
            savedStateHandle.getLiveData(key, initialValue)
        }
    }

    fun <T> get(key: String): T? {
        return savedStateHandle.get(key)
    }

    fun <T> get(key: String, valueIfKeyNotFound: T): T {
        return savedStateHandle.get(key) ?: valueIfKeyNotFound
    }
}
