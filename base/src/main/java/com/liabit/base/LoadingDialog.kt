package com.liabit.base

import androidx.annotation.StringRes

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
interface LoadingDialog {
    fun setText(@StringRes resId: Int)

    fun setText(text: CharSequence?)

    fun show()

    fun dismiss()

    fun setCancelable(cancelable: Boolean)
}