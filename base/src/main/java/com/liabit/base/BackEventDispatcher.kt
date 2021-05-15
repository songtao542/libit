package com.liabit.base

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
interface BackEventDispatcher {
    fun dispatchBackEvent(): Boolean
}

interface OnBackListener {
    fun onBackPressed(): Boolean
}