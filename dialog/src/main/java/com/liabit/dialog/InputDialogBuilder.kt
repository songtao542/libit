package com.liabit.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog

@Suppress("unused")
class InputDialogBuilder(private val context: Context) {

    private var mDialogTheme = R.style.DefaultInputDialogTheme
    private var mDialogTitle: CharSequence? = null
    private var mText: CharSequence? = null

    private var mOnConfirmListener: ((dialog: DialogInterface, text: String) -> Unit)? = null
    private var mOnCancelListener: ((dialog: DialogInterface) -> Unit)? = null

    private var mAutoDismissWhenCancel = true
    private var mAutoDismissWhenConfirm = true

    fun setTheme(@StyleRes themeResId: Int): InputDialogBuilder {
        mDialogTheme = themeResId
        return this
    }

    fun setTitle(title: CharSequence): InputDialogBuilder {
        mDialogTitle = title
        return this
    }

    fun setTitle(@StringRes resId: Int): InputDialogBuilder {
        mDialogTitle = context.getString(resId)
        return this
    }

    fun setText(text: CharSequence) {
        mText = text
    }

    fun setText(@StringRes resId: Int) {
        mText = context.getString(resId)
    }

    fun setOnConfirmListener(listener: ((dialog: DialogInterface, text: String) -> Unit)? = null): InputDialogBuilder {
        mOnConfirmListener = listener
        return this
    }

    fun setOnCancelListener(listener: ((dialog: DialogInterface) -> Unit)? = null): InputDialogBuilder {
        mOnCancelListener = listener
        return this
    }

    fun setDismissWhenCancel(dismiss: Boolean = true): InputDialogBuilder {
        mAutoDismissWhenCancel = dismiss
        return this
    }

    fun setDismissWhenConfirm(dismiss: Boolean = true): InputDialogBuilder {
        mAutoDismissWhenConfirm = dismiss
        return this
    }

    fun show(): AlertDialog {
        val context = ContextThemeWrapper(context, mDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null)
        val editText = view.findViewById<EditText>(R.id.input_dialog_editText)
        mText?.let { editText.setText(mText) }
        val dialog = AlertDialog.Builder(context, mDialogTheme)
                .setView(view)
                .setTitle(mDialogTitle ?: context.getString(R.string.input_dialog_title))
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
                    mOnConfirmListener?.invoke(d, editText.text?.toString() ?: "")
                }
                .create()
        editText.requestFocus()
        dialog.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            it.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        }
        dialog.show()
        return dialog
    }
}