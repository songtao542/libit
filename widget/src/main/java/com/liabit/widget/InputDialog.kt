package com.liabit.widget

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class InputDialog(private val context: Context) {

    private var mDialogTheme = R.style.InputDialog
    private var mDialogTitle: String? = null

    private var mOnConfirmListener: ((text: String) -> Unit)? = null

    fun setTheme(themeResId: Int): InputDialog {
        mDialogTheme = themeResId
        return this
    }

    fun setTitle(title: String): InputDialog {
        mDialogTitle = title
        return this
    }

    fun setOnConfirmListener(listener: ((text: String) -> Unit)? = null): InputDialog {
        mOnConfirmListener = listener
        return this
    }

    fun show(): AlertDialog {
        val context = ContextThemeWrapper(context, mDialogTheme)
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null)
        val editText = view.findViewById<EditText>(R.id.input_dialog_editText)

        return AlertDialog.Builder(context, mDialogTheme)
                .setView(view)
                .setTitle(mDialogTitle ?: context.getString(R.string.input_dialog_title))
                .setNegativeButton(R.string.input_dialog_cancel) { d, _ ->
                    d.dismiss()
                }
                .setPositiveButton(R.string.input_dialog_confirm) { d, _ ->
                    d.dismiss()
                    mOnConfirmListener?.invoke(editText.text?.toString() ?: "")
                }
                .show()
    }
}