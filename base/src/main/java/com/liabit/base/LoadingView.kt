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
        init(context, null, 0, 0, true)
    }

    constructor(context: Context, circularIndicatorBar: Boolean) : super(context) {
        init(context, null, 0, 0, circularIndicatorBar)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0, true)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0, true)
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
        init(context, attrs, defStyleAttr, defStyleRes, true)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int, circularIndicatorBar: Boolean) {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.loading_view, this, true)
        orientation = VERTICAL
        var isCircularIndicator = circularIndicatorBar
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, defStyleRes)
            isCircularIndicator = typedArray.getBoolean(R.styleable.LoadingView_circularIndicatorBar, true)
            typedArray.recycle()
        }

        setBackgroundResource(R.drawable.loading_view_background)
        mTextView = findViewById(R.id.textView)
        mProgressBar = if (isCircularIndicator) {
            findViewById(R.id.progressBar)
        } else {
            removeView(findViewById(R.id.progressBar))
            val progressBar = inflater.inflate(R.layout.loading_progress_bar, null, false) as ProgressBar
            addView(progressBar, 1)
            progressBar
        }
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
