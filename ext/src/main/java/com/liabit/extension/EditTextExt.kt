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
        val px = if (width > 1) x + width - 1 else x
        val py = if (height > 1) y + height - 1 else y
        onTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, MotionEvent.ACTION_DOWN, px, py, 0))
        currentTimeMillis = System.currentTimeMillis()
        onTouchEvent(MotionEvent.obtain(currentTimeMillis, currentTimeMillis, MotionEvent.ACTION_UP, px, py, 0))
    }
}