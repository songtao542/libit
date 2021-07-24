package com.liabit.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.StringRes

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
class LoadingDialogImpl(context: Context) : Dialog(context, R.style.LoadingDialog), LoadingDialog {

    private var mLoadingView: LoadingView? = null
    private var mText: CharSequence? = null
    private var mCancellable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = FrameLayout(context)

        val ta = context.theme.obtainStyledAttributes(R.style.LoadingDialog, intArrayOf(R.attr.useCircularIndicator))
        val useCircularIndicator = ta.getBoolean(0, true)
        ta.recycle()
        android.util.Log.d("TTTT", "uuuuuuuuuuuuuu: $useCircularIndicator")
        val loadingView = LoadingView(context, useCircularIndicator)

        val size = context.resources.getDimension(R.dimen.loading_view_size).toInt()
        loadingView.layoutParams = FrameLayout.LayoutParams(size, size).apply {
            gravity = Gravity.CENTER
        }
        contentView.addView(loadingView)
        setContentView(contentView)
        contentView.setOnClickListener {
            if (mCancellable) {
                onBackPressed()
            }
        }
        loadingView.setOnClickListener {
        }
        mLoadingView = loadingView
        window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            //it.setLayout(size, size)
            it.addFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        }

        mText?.let {
            mLoadingView?.setText(it)
        }
    }

    override fun setCancelable(cancelable: Boolean) {
        mCancellable = cancelable
        setCanceledOnTouchOutside(mCancellable)
        if (mCancellable) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        super.setCancelable(cancelable)
    }

    override fun setText(text: CharSequence?) {
        mText = text
        mLoadingView?.setText(text)
    }

    override fun setText(@StringRes resId: Int) {
        mText = context.getString(resId)
        mLoadingView?.setText(resId)
    }

}