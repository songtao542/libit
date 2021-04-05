package com.liabit.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView

/**
 * Author:         songtao
 * CreateDate:     2020/12/8 10:00
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
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

    private var mFixedSize = false

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
        mProgressView.isClickable = false
        mTextView.isClickable = false
        mProgressView.setBackgroundColor(Color.TRANSPARENT)
        mTextView.setBackgroundColor(Color.TRANSPARENT)
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
            mFixedSize = typedArray.getBoolean(R.styleable.ProgressButton_fixedSize, false)
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

        /* mProgressView.id = generateViewId()
         mTextView.id = generateViewId()
         val tlp = LayoutParams(LayoutParams.WRAP_CONTENT, progressSize.toInt())
         tlp.addRule(CENTER_IN_PARENT)

         val plp = LayoutParams(progressSize.toInt(), progressSize.toInt())
         plp.addRule(CENTER_IN_PARENT)

         if (progressPosition == 0) {
             //tlp.addRule(RIGHT_OF, mProgressView.id)
             plp.addRule(LEFT_OF, mTextView.id)
             addView(mTextView, tlp)
             addView(mProgressView, plp)
         } else {
             plp.addRule(RIGHT_OF, mTextView.id)
             addView(mTextView, tlp)
             addView(mProgressView, plp)
         }*/

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
                //mTextView.visibility = if (mFixedSize) View.INVISIBLE else View.GONE
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

    fun setText(text: CharSequence) {
        mTextView.text = text
    }

    fun setText(@StringRes resId: Int) {
        mTextView.setText(resId)
    }
}
