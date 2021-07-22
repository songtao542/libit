package com.scaffold.widget

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import com.scaffold.R
import com.scaffold.databinding.BottomMenuDialogBinding
import com.scaffold.extension.setCornerRadius
import com.scaffold.extension.setSelectableItemBackground


class BottomMenuDialog(context: Context) : Dialog(context, R.style.Widget_Dialog_Bottom_Style) {

    private var binding: BottomMenuDialogBinding? = null

    private var mCancelText: CharSequence? = null
    private var mCancelTextResId: Int = R.string.cancel
    private var mOkText: CharSequence? = null
    private var mOkTextResId: Int = R.string.ok
    private var mMessageText: CharSequence? = null
    private var mMessageTextResId: Int? = null
    private var mTitleText: CharSequence? = null
    private var mTitleTextResId: Int = R.string.notify

    private var mOkClickListener: View.OnClickListener? = null
    private var mCancelClickListener: View.OnClickListener? = null
    private var mDismissListener: DialogInterface.OnDismissListener? = null

    private var mMenus = ArrayList<MenuItem>()

    private fun getString(resId: Int?): CharSequence {
        if (resId == null || resId == 0) return ""
        return context.getString(resId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomMenuDialogBinding.inflate(LayoutInflater.from(context)).also {
            setContentView(it.root)
            it.container.setCornerRadius(R.dimen.bottom_dialog_corner_radius)
            it.ok.setOnClickListener { v ->
                dismiss()
                mOkClickListener?.onClick(v)
            }
            it.cancel.setOnClickListener { v ->
                dismiss()
                mCancelClickListener?.onClick(v)
            }
        }
        setup()
        window?.attributes?.gravity = Gravity.BOTTOM
        super.setOnDismissListener {
            mDismissListener?.onDismiss(it)
        }
    }

    override fun show() {
        setup()
        super.show()
    }

    private fun setup() {
        val binding = binding ?: return
        val cancelText = mCancelText ?: getString(mCancelTextResId)
        binding.cancel.text = cancelText
        val okText = mOkText ?: getString(mOkTextResId)
        binding.ok.text = okText
        val messageText = mMessageText ?: getString(mMessageTextResId)
        binding.message.text = messageText
        val titleText = mTitleText ?: getString(mTitleTextResId)
        binding.title.text = titleText

        for (menu in mMenus) {
            binding.contentLayout.addView(MenuItemView(context).apply {
                text = menu.getTitle(context)
                setOnClickListener {
                    menu.listener.invoke(this@BottomMenuDialog)
                }
            })
        }
    }

    fun setCancelText(text: CharSequence): BottomMenuDialog {
        mCancelText = text
        return this
    }

    fun setCancelText(@StringRes resId: Int): BottomMenuDialog {
        mCancelTextResId = resId
        return this
    }

    fun setOkText(text: CharSequence): BottomMenuDialog {
        mOkText = text
        return this
    }

    fun setOkText(@StringRes resId: Int): BottomMenuDialog {
        mOkTextResId = resId
        return this
    }

    fun setMessage(text: CharSequence): BottomMenuDialog {
        mMessageText = text
        return this
    }

    fun setMessage(@StringRes resId: Int): BottomMenuDialog {
        mMessageTextResId = resId
        return this
    }

    fun setDialogTitle(title: CharSequence?): BottomMenuDialog {
        mTitleText = title
        return this
    }

    fun setDialogTitle(@StringRes titleId: Int): BottomMenuDialog {
        mTitleTextResId = titleId
        return this
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        mDismissListener = listener
    }

    fun setDismissListener(listener: DialogInterface.OnDismissListener): BottomMenuDialog {
        setOnDismissListener(listener)
        return this
    }

    fun setOnOkClickListener(listener: View.OnClickListener): BottomMenuDialog {
        mOkClickListener = listener
        return this
    }

    fun setOnCancelClickListener(listener: View.OnClickListener): BottomMenuDialog {
        mCancelClickListener = listener
        return this
    }

    fun addMenu(text: CharSequence, listener: (dialog: DialogInterface) -> Unit): BottomMenuDialog {
        mMenus.add(MenuItem(text = text, listener = listener))
        return this
    }

    fun addMenu(@StringRes resId: Int, listener: (dialog: DialogInterface) -> Unit): BottomMenuDialog {
        mMenus.add(MenuItem(textResId = resId, listener = listener))
        return this
    }

    inner class MenuItem(val text: CharSequence? = null, val textResId: Int = 0, val listener: (dialog: DialogInterface) -> Unit) {
        fun getTitle(context: Context): CharSequence {
            return text ?: if (textResId != 0) context.getText(textResId) else ""
        }
    }

    class MenuItemView(context: Context) : AppCompatTextView(context) {
        init {
            init(context)
        }

        @SuppressLint("SetTextI18n")
        private fun init(context: Context) {
            val ph = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()
            val pv = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics).toInt()
            setPadding(ph, pv, ph, pv)
            gravity = Gravity.CENTER
            setSelectableItemBackground()
        }
    }

}