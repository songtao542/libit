package com.liabit.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog

@Suppress("unused")
class AlertDialogBuilder(private val context: Context) {

    private var mDialogTheme = R.style.DefaultAlertDialogTheme
    private var mDialogTitle: CharSequence? = null
    private var mDialogMessage: CharSequence? = null
    private var mCancelable: Boolean = true

    private var mOnConfirmListener: ((dialog: DialogInterface) -> Unit)? = null
    private var mOnCancelListener: ((dialog: DialogInterface) -> Unit)? = null
    private var mAutoDismissWhenCancel = true
    private var mAutoDismissWhenConfirm = true

    fun setTheme(@StyleRes themeResId: Int): AlertDialogBuilder {
        mDialogTheme = themeResId
        return this
    }

    fun setTitle(title: CharSequence): AlertDialogBuilder {
        mDialogTitle = title
        return this
    }

    fun setTitle(@StringRes resId: Int): AlertDialogBuilder {
        mDialogTitle = context.getString(resId)
        return this
    }

    fun setMessage(message: CharSequence): AlertDialogBuilder {
        mDialogMessage = message
        return this
    }

    fun setMessage(@StringRes resId: Int): AlertDialogBuilder {
        mDialogMessage = context.getString(resId)
        return this
    }

    fun setCancelable(cancelable: Boolean): AlertDialogBuilder {
        mCancelable = cancelable
        return this
    }

    fun setOnConfirmListener(listener: ((dialog: DialogInterface) -> Unit)? = null): AlertDialogBuilder {
        mOnConfirmListener = listener
        return this
    }

    fun setOnCancelListener(listener: ((dialog: DialogInterface) -> Unit)? = null): AlertDialogBuilder {
        mOnCancelListener = listener
        return this
    }

    fun setDismissWhenCancel(dismiss: Boolean = true): AlertDialogBuilder {
        mAutoDismissWhenCancel = dismiss
        return this
    }

    fun setDismissWhenConfirm(dismiss: Boolean = true): AlertDialogBuilder {
        mAutoDismissWhenConfirm = dismiss
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
                    if (mAutoDismissWhenCancel) {
                        d.dismiss()
                    }
                    mOnCancelListener?.invoke(d)
                }
                .setPositiveButton(R.string.dialog_confirm) { d, _ ->
                    if (mAutoDismissWhenConfirm) {
                        d.dismiss()
                    }
                    mOnConfirmListener?.invoke(d)
                }
                .show()
    }
}