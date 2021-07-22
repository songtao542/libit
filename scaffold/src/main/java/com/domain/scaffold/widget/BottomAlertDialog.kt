package com.domain.scaffold.widget

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes
import com.domain.scaffold.R
import com.domain.scaffold.databinding.BottomDialogBinding
import com.domain.scaffold.extension.setCornerRadius

class BottomAlertDialog(context: Context) : Dialog(context, R.style.Widget_Dialog_Bottom_Style) {

    private var binding: BottomDialogBinding? = null

    private var mCancelText: CharSequence? = null
    private var mCancelTextResId: Int = R.string.cancel
    private var mOkText: CharSequence? = null
    private var mOkTextResId: Int = R.string.ok
    private var mMessageText: CharSequence? = null
    private var mMessageTextResId: Int? = null
    private var mTitleText: CharSequence? = null
    private var mTitleTextResId: Int = 0

    private var mOkClickListener: View.OnClickListener? = null
    private var mCancelClickListener: View.OnClickListener? = null
    private var mDismissListener: DialogInterface.OnDismissListener? = null

    private var mTextGravity: Int = Gravity.CENTER

    private fun getString(resId: Int?): CharSequence {
        if (resId == null || resId == 0) return ""
        return context.getString(resId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BottomDialogBinding.inflate(LayoutInflater.from(context)).also {
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
        binding.message.gravity = mTextGravity
        binding.message.text = messageText
        if (messageText is Spanned) {
            binding.message.movementMethod = LinkMovementMethod.getInstance()
        }
        val titleText = mTitleText ?: if (mTitleTextResId != 0) getString(mTitleTextResId) else null
        titleText?.let {
            binding.title.visibility = View.VISIBLE
            binding.title.text = it
        } ?: kotlin.run {
            binding.title.visibility = View.GONE
        }
    }

    fun setCancelText(text: CharSequence): BottomAlertDialog {
        mCancelText = text
        return this
    }

    fun setCancelText(@StringRes resId: Int): BottomAlertDialog {
        mCancelTextResId = resId
        return this
    }

    fun setOkText(text: CharSequence): BottomAlertDialog {
        mOkText = text
        return this
    }

    fun setOkText(@StringRes resId: Int): BottomAlertDialog {
        mOkTextResId = resId
        return this
    }

    fun setMessage(text: CharSequence): BottomAlertDialog {
        mMessageText = text
        return this
    }

    fun setMessage(@StringRes resId: Int): BottomAlertDialog {
        mMessageTextResId = resId
        return this
    }

    fun setMessageGravity(gravity: Int): BottomAlertDialog {
        mTextGravity = gravity
        return this
    }

    fun setDialogTitle(title: CharSequence?): BottomAlertDialog {
        mTitleText = title
        return this
    }

    fun setDialogTitle(@StringRes titleId: Int): BottomAlertDialog {
        mTitleTextResId = titleId
        return this
    }

    override fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        mDismissListener = listener
    }

    fun setDismissListener(listener: DialogInterface.OnDismissListener): BottomAlertDialog {
        mDismissListener = listener
        return this
    }

    fun setOnOkClickListener(listener: View.OnClickListener): BottomAlertDialog {
        mOkClickListener = listener
        return this
    }

    fun setOnCancelClickListener(listener: View.OnClickListener): BottomAlertDialog {
        mCancelClickListener = listener
        return this
    }

}