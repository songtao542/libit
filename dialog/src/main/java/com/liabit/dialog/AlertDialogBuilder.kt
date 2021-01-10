package com.liabit.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class AlertDialogBuilder(private val context: Context) {

    private var mDialogTheme = R.style.DefaultAlertDialogTheme
    private var mDialogTitle: String? = null
    private var mDialogMessage: String? = null
    private var mCancelable: Boolean = true

    private var mOnConfirmListener: (() -> Unit)? = null
    private var mOnCancelListener: (() -> Unit)? = null

    fun setTheme(themeResId: Int): AlertDialogBuilder {
        mDialogTheme = themeResId
        return this
    }

    fun setTitle(title: String): AlertDialogBuilder {
        mDialogTitle = title
        return this
    }

    fun setMessage(message: String): AlertDialogBuilder {
        mDialogMessage = message
        return this
    }

    fun setCancelable(cancelable: Boolean): AlertDialogBuilder {
        mCancelable = cancelable
        return this
    }

    fun setOnConfirmListener(listener: (() -> Unit)? = null): AlertDialogBuilder {
        mOnConfirmListener = listener
        return this
    }

    fun setOnCancelListener(listener: (() -> Unit)? = null): AlertDialogBuilder {
        mOnCancelListener = listener
        return this
    }

    fun show(): AlertDialog {
        val context = ContextThemeWrapper(context, mDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null)
        val textView = view.findViewById<TextView>(R.id.dialog_message_text)
        textView.text = mDialogMessage
        return AlertDialog.Builder(context, mDialogTheme)
                .setView(view)
                .setCancelable(mCancelable)
                .setTitle(mDialogTitle ?: context.getString(R.string.alert_dialog_title))
                .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                    d.dismiss()
                    mOnCancelListener?.invoke()
                }
                .setPositiveButton(R.string.dialog_confirm) { d, _ ->
                    d.dismiss()
                    mOnConfirmListener?.invoke()
                }
                .show()
    }
}