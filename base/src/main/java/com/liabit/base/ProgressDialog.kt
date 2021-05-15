package com.liabit.base

import androidx.annotation.StringRes

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
interface ProgressDialog {

    fun showDialog(@StringRes resId: Int, cancellable: Boolean = true)

    fun showDialog(msg: String? = null, cancellable: Boolean = true)

    /**
     * 隐藏等待框
     */
    fun dismissDialog(delayMillis: Long = 0)
}