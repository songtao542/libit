package com.liabit.test.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.StringRes
import com.liabit.test.R

class LoadingDialog(context: Context) : Dialog(context, R.style.LoadingDialog) {

    private var mLoadingView: LoadingView? = null
    private var mText: CharSequence? = null
    private var mCancellable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = FrameLayout(context)
        val loadingView = LoadingView(context)
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

    override fun setCancelable(flag: Boolean) {
        mCancellable = flag
        setCanceledOnTouchOutside(mCancellable)
        if (mCancellable) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
        super.setCancelable(flag)
    }

    fun setText(text: String?) {
        mText = text
        mLoadingView?.setText(text)
    }

    fun setText(@StringRes resId: Int) {
        mText = context.getString(resId)
        mLoadingView?.setText(resId)
    }

}