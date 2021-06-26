package com.liabit.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle

interface SavedStateHandler {

    fun onRestoreInstanceState(savedStateHandle: SavedStateHandle)

    fun <T> setValue(key: String, value: T)

    fun <T> set(key: String, value: T)

    fun <T> getLiveData(key: String, initialValue: T? = null): MutableLiveData<T>

    fun <T> get(key: String): T?

    fun <T> get(key: String, valueIfKeyNotFound: T): T

}