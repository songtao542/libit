package com.irun.runker.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.irun.runker.R

/**
 * Implementation of App Widget functionality.
 */
class ProgressBar : ConstraintLayout {

    private var mProgressIndicator: CircularProgressIndicator
    private var mTextView: TextView

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.progress_bar, this, true)
        setBackgroundResource(R.drawable.progress_bar_background)
        mProgressIndicator = findViewById(R.id.progressIndicator)
        mTextView = findViewById(R.id.messageText)
    }

    fun setText(text: CharSequence?) {
        text?.also {
            mTextView.visibility = View.VISIBLE
            mTextView.text = text
        } ?: kotlin.run {
            mTextView.visibility = View.GONE
        }
    }
}

