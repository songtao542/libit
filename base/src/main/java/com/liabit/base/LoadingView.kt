package com.liabit.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.material.progressindicator.CircularProgressIndicator

/**
 * Author:         songtao
 * CreateDate:     2020/9/22 14:57
 */
@Suppress("MemberVisibilityCanBePrivate")
class LoadingView : LinearLayout {

    private var mTextView: TextView? = null
    private var mProgressBar: ProgressBar? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    @Suppress("unused")
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(
        context, attrs, defStyleAttr, defStyleRes
    ) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.loading_view, this, true)
        orientation = VERTICAL
        setBackgroundResource(R.drawable.loading_view_background)
        mTextView = findViewById(R.id.textView)
        mProgressBar = findViewById(R.id.progressBar)
        mTextView?.visibility = View.GONE
    }

    fun setText(@StringRes resId: Int) {
        setText(context.getString(resId))
    }

    fun setText(text: CharSequence?) {
        mTextView?.let {
            if (text.isNullOrEmpty()) {
                it.visibility = View.GONE
            } else {
                it.text = text
                it.visibility = View.VISIBLE
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        (mProgressBar as? CircularProgressIndicator)?.show()
    }

}
