package com.scaffold.util

import android.content.Context
import com.scaffold.TheApp

object Toast {
    @JvmStatic
    fun show(text: String?) {
        if (text == null) return
        val context: Context = TheApp.applicationContext ?: return
        android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show()
    }

    @JvmStatic
    fun show(textResId: Int) {
        if (textResId == 0) return
        val context: Context = TheApp.applicationContext ?: return
        val text = context.getString(textResId)
        android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show()
    }
}