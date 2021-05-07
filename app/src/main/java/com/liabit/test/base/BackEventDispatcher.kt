package com.liabit.test.base

interface BackEventDispatcher {
    fun dispatchBackEvent(): Boolean
}

interface OnBackListener {
    fun onBackPressed(): Boolean
}