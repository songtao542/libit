package com.liabit.listpicker.model

import androidx.annotation.IntDef

object VariableState {
    const val LOCATING = 123
    const val SUCCESS = 132
    const val FAILURE = 321

    @IntDef(SUCCESS, FAILURE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class State
}
