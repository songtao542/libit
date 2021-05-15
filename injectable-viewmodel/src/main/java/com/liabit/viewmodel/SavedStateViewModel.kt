package com.liabit.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle

open class SavedStateViewModel(private val savedStateHandle: SavedStateHandle) : ApplicationViewModel(),
    SavedStateHandler by SavedStateHelper(savedStateHandle) {

    override fun onCreate(context: Context) {
        onRestoreInstanceState(savedStateHandle)
    }

}