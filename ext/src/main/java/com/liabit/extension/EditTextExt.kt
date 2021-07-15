package com.liabit.extension

import android.view.MotionEvent
import android.widget.EditText

fun EditText.showKeyboard() {
    requestKeyboard()
}

fun EditText.showSoftInput() {
    requestKeyboard()
}

fun EditText.requestKeyboard() {
    post {
        requestFocus()
        var currentTimeMillis = System.currentTimeMillis()
        onTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, MotionEvent.ACTION_DOWN, x, y, 0))
        currentTimeMillis = System.currentTimeMillis()
        onTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, MotionEvent.ACTION_UP, x, y, 0))
    }
}