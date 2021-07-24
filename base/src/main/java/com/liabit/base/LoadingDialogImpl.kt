package com.liabit.base

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.StringRes
import okhttp3.internal.toHexString

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 * ```
 * 修改 progress bar 样式
 * 第一步：设置 circularIndicatorBar 为 false
 * <style name="LoadingView.Style">
 *     <item name="circularIndicatorBar">false</item>
 * </style>
 *
 * 第二步：修改 LoadingView.ProgressBarStyle
 * <style name="LoadingView.ProgressBarStyle">
 *     <item name="android:indeterminateDrawable">@drawable/progressbar_loading</item>
 *     <item name="android:minWidth">24dp</item>
 *     <item name="android:minHeight">24dp</item>
 *     <item name="android:layout_width">40dp</item>
 *     <item name="android:layout_height">40dp</item>
 *     <item name="layout_goneMarginBottom">8dp</item>
 * </style>
 *
 * 第三步：定义 indeterminateDrawable
 * <?xml version="1.0" encoding="utf-8"?>
 * <rotate xmlns:android="http://schemas.android.com/apk/res/android"
 *     android:drawable="@mipmap/ic_loading"
 *     android:fromDegrees="0"
 *     android:pivotX="50%"
 *     android:pivotY="50%"
 *     android:toDegrees="720">
 * </rotate>
 * ```
 */
class LoadingDialogImpl(context: Context) : Dialog(context, R.style.LoadingDialog), LoadingDialog {

    private var mLoadingView: LoadingView? = null
    private var mText: CharSequence? = null
    private var mCancellable = true

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = FrameLayout(context)
        val ta = context.obtainStyledAttributes(R.style.LoadingView_Style, R.styleable.LoadingView)
        val circularIndicatorBar = ta.getBoolean(R.styleable.LoadingView_circularIndicatorBar, true)
        ta.recycle()
        val loadingView = LoadingView(context, circularIndicatorBar)
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