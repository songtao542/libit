package com.liabit.tablayout

import android.animation.ArgbEvaluator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.max
import kotlin.math.min

@Suppress("unused", "MemberVisibilityCanBePrivate")
class TextTabView : AppCompatTextView, TabView {
    private var mTextWidth = 0f
    private var mWidth = 0f
    private var mNormalColor = 0xff000000.toInt()
    private var mSelectColor = 0xff000000.toInt()
    private val mArgbEvaluator = ArgbEvaluator()
    private var mNormalTextSize = 0f
    private var mSelectTextSize = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val attrsArray = intArrayOf(android.R.attr.textColor)
        val typedArray = context.obtainStyledAttributes(attrsArray)
        mNormalColor = typedArray.getColor(0, mNormalColor)
        typedArray.recycle()
        gravity = Gravity.CENTER
        setSingleLine()
        setTextColor(mNormalColor)
        background = RippleDrawable(ColorStateList.valueOf(0xffdddddd.toInt()),
                ColorDrawable(0x00ffffff), ColorDrawable(0xff000000.toInt()))
    }

    override fun onPageScrolled(positionOffset: Float) {
        if (mSelectTextSize != 0f) {
            if (mNormalTextSize == 0f) {
                mNormalTextSize = textSize
            }
            val textSize = mNormalTextSize + (mSelectTextSize - mNormalTextSize) * (1f - positionOffset)
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }

        val color = mArgbEvaluator.evaluate(positionOffset, mSelectColor, mNormalColor) as Int
        super.setTextColor(color)
    }

    fun setSelectTextColor(color: Int) {
        mSelectColor = color
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mNormalColor = color
    }

    /**
     * @param size 单位 SP
     */
    fun setSelectTextSize(size: Float) {
        setSelectTextSize(TypedValue.COMPLEX_UNIT_SP, size)
    }

    fun setSelectTextSize(unit: Int, size: Float) {
        val textSize = getDimension(unit, size)
        if (textSize == mSelectTextSize) {
            return
        }
        mSelectTextSize = textSize
        measureMinWidth()
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        val textSize = getDimension(unit, size)
        if (textSize == mNormalTextSize) {
            return
        }
        mNormalTextSize = textSize
        measureMinWidth()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        measureMinWidth()
    }

    private fun measureMinWidth() {
        if (text.isNullOrEmpty()) return
        val width = context.resources.displayMetrics.widthPixels
        val widthMeasureSpec: Int = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
        val heightMeasureSpec: Int = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST)
        val textsize = textSize
        val maxTextSize = maxTextSize()
        paint.textSize = maxTextSize
        mTextWidth = paint.measureText(text.toString())
        val padding = (maxTextSize / 2f).toInt()
        setPadding(padding, 0, padding, 0)
        measure(widthMeasureSpec, heightMeasureSpec)
        minWidth = measuredWidth
        paint.textSize = textsize
    }

    private fun maxTextSize(): Float {
        return max(max(mSelectTextSize, mNormalTextSize), textSize)
    }

    private fun getDimension(unit: Int, size: Float): Float {
        return if (unit == TypedValue.COMPLEX_UNIT_PX) size else TypedValue.applyDimension(unit, size, resources.displayMetrics)
    }

    override fun getContentWidth(): Int {
        return if (width > 0) {
            min(width, (mTextWidth + maxTextSize() * 2f).toInt())
        } else {
            (mTextWidth + maxTextSize() * 2f).toInt()
        }
    }

    override fun getContentHeight(): Int {
        return if (height > 0) {
            min(height, (maxTextSize() * 2f).toInt())
        } else {
            (maxTextSize() * 2f).toInt()
        }
    }

    override fun getView(): TextTabView {
        return this
    }
}