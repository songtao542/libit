package com.liabit.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatTextView

/**
 * Author:         songtao
 * CreateDate:     2020/12/8 10:00
 */
class ProgressButton : LinearLayout {

    companion object {
        const val TEXT = 0
        const val PROGRESS = 1
        const val TEXT_PROGRESS = 2
    }

    @IntDef(TEXT, PROGRESS, TEXT_PROGRESS)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    private lateinit var mProgressView: MaterialProgressView
    private lateinit var mTextView: AppCompatTextView

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        isClickable = true

        mProgressView = MaterialProgressView(context, attrs)
        mTextView = AppCompatTextView(context, attrs)
        var progressPosition = 1
        var progressVisibility = 1
        var progressSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, context.resources.displayMetrics)
        var progressMode = -1
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, defStyleAttr, defStyleRes)
            progressPosition = typedArray.getInt(R.styleable.ProgressButton_progressPosition, 1)
            progressVisibility = typedArray.getInt(R.styleable.ProgressButton_progressVisibility, View.GONE)
            progressSize = typedArray.getDimension(R.styleable.ProgressButton_progressSize, progressSize)
            progressMode = typedArray.getInt(R.styleable.ProgressButton_mode, -1)
            typedArray.recycle()
        }
        val tlp = LayoutParams(LayoutParams.WRAP_CONTENT, progressSize.toInt())
        tlp.gravity = Gravity.CENTER

        val plp = LayoutParams(progressSize.toInt(), progressSize.toInt())
        plp.gravity = Gravity.CENTER

        if (progressPosition == 0) {
            addView(mProgressView, plp)
            addView(mTextView, tlp)
        } else {
            addView(mTextView, tlp)
            addView(mProgressView, plp)
        }
        mProgressView.setStrokeWidth(progressSize / 24)
        mTextView.setPadding(0, 0, 0, 0)
        mTextView.gravity = Gravity.CENTER
        mProgressView.setPadding(0, 0, 0, 0)
        if (progressMode != -1) {
            setMode(progressMode)
        } else {
            setProgressVisibility(progressVisibility)
        }
    }

    fun setProgressVisibility(visibility: Int) {
        when (visibility) {
            View.VISIBLE -> {
                mProgressView.visibility = View.VISIBLE
                mProgressView.resume()
            }
            View.GONE -> {
                mProgressView.visibility = View.GONE
                mProgressView.pause()
            }
            View.INVISIBLE -> {
                mProgressView.visibility = View.INVISIBLE
                mProgressView.pause()
            }
        }
    }

    fun setTextVisibility(visibility: Int) {
        when (visibility) {
            View.VISIBLE -> {
                mTextView.visibility = View.VISIBLE
            }
            View.GONE -> {
                mTextView.visibility = View.GONE
            }
            View.INVISIBLE -> {
                mTextView.visibility = View.INVISIBLE
            }
        }
    }

    fun setMode(@Mode mode: Int) {
        when (mode) {
            TEXT -> {
                mTextView.visibility = View.VISIBLE
                mProgressView.visibility = View.GONE
                mProgressView.pause()
            }
            PROGRESS -> {
                mTextView.visibility = View.GONE
                mProgressView.visibility = View.VISIBLE
                mProgressView.resume()
            }
            else -> {
                mTextView.visibility = View.VISIBLE
                mProgressView.visibility = View.VISIBLE
                mProgressView.resume()
            }
        }
    }
}
