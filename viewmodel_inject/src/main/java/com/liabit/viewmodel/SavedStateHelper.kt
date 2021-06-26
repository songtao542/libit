package com.liabit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

class SavedStateHelper(private val savedStateHandle: SavedStateHandle) : SavedStateHandler {

    override fun onRestoreInstanceState(savedStateHandle: SavedStateHandle) {
    }

    override fun <T> setValue(key: String, value: T) {
        savedStateHandle.set(key, value)
    }

    override fun <T> set(key: String, value: T) {
        setValue(key, value)
    }

    override fun <T> getLiveData(key: String, initialValue: T?): MutableLiveData<T> {
        return if (initialValue == null) {
            savedStateHandle.getLiveData(key)
        } else {
            savedStateHandle.getLiveData(key, initialValue)
        }
    }

    override fun <T> get(key: String): T? {
        return savedStateHandle.get(key)
    }

    override fun <T> get(key: String, valueIfKeyNotFound: T): T {
        return savedStateHandle.get(key) ?: valueIfKeyNotFound
    }
}
