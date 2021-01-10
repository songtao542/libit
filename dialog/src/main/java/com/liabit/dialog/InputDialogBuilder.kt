package com.liabit.dialog

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class InputDialogBuilder(private val context: Context) {

    private var mDialogTheme = R.style.DefaultInputDialogTheme
    private var mDialogTitle: String? = null

    private var mOnConfirmListener: ((text: String) -> Unit)? = null

    fun setTheme(themeResId: Int): InputDialogBuilder {
        mDialogTheme = themeResId
        return this
    }

    fun setTitle(title: String): InputDialogBuilder {
        mDialogTitle = title
        return this
    }

    fun setOnConfirmListener(listener: ((text: String) -> Unit)? = null): InputDialogBuilder {
        mOnConfirmListener = listener
        return this
    }

    fun show(): AlertDialog {
        val context = ContextThemeWrapper(context, mDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null)
        val editText = view.findViewById<EditText>(R.id.input_dialog_editText)
        val dialog = AlertDialog.Builder(context, mDialogTheme)
                .setView(view)
                .setTitle(mDialogTitle ?: context.getString(R.string.input_dialog_title))
                .setNegativeButton(R.string.dialog_cancel) { d, _ ->
                    d.dismiss()
                }
                .setPositiveButton(R.string.dialog_confirm) { d, _ ->
                    d.dismiss()
                    mOnConfirmListener?.invoke(editText.text?.toString() ?: "")
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