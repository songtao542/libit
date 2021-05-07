package com.liabit.test.base

import androidx.annotation.StringRes

interface ProgressDialog {

    fun showDialog(@StringRes resId: Int, cancellable: Boolean = true)

    fun showDialog(msg: String? = null, cancellable: Boolean = true)

    /**
     * 隐藏等待框
     */
    fun dismissDialog(delayMillis: Long = 0)

}