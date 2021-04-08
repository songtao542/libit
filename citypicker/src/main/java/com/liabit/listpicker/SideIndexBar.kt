package com.liabit.listpicker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.liabit.citypicker.R
import kotlin.math.max

class SideIndexBar : View {

    companion object {
        private val DEFAULT_INDEX_ITEMS = arrayOf("A", "B", "C", "D", "E", "F", "G", "H",
                "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#")
    }

    private var mIndexItems = ArrayList<String>(listOf(*DEFAULT_INDEX_ITEMS))
    private var mItemHeight = 0f//每个index的高度

    private var mTextTouchedColor = 0
    private var mCurrentIndex = -1
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mTouchedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mWidth = 0
    private var mHeight = 0
    private var mTopMargin = 0f //居中绘制，文字绘制起点和控件顶部的间隔

    private var mVariableSection: String? = null
    private var mHotSection: String? = null
    private var mOverlayTextView: TextView? = null
    private var mOnIndexChangedListener: OnIndexTouchedChangedListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.cpIndexBarTextSize, typedValue, true)
        val textSize = context.resources.getDimension(typedValue.resourceId)
        context.theme.resolveAttribute(R.attr.cpIndexBarNormalTextColor, typedValue, true)
        val textColor = ResourcesCompat.getColor(context.resources, typedValue.resourceId, context.theme)
        context.theme.resolveAttribute(R.attr.cpIndexBarSelectedTextColor, typedValue, true)
        mTextTouchedColor = ResourcesCompat.getColor(context.resources, typedValue.resourceId, context.theme)
        mPaint.textSize = textSize
        mPaint.color = textColor
        mTouchedPaint.textSize = textSize
        mTouchedPaint.color = mTextTouchedColor
    }

    fun setHotSection(section: String?) {
        if (section.isNullOrBlank()) {
            mIndexItems.remove(mHotSection)
        } else {
            if (mVariableSection.isNullOrBlank()) {
                mIndexItems.add(0, section)
            } else {
                mIndexItems.add(1, section)
            }
        }
        postInvalidate()
    }

    fun setVariableSection(section: String?) {
        if (section.isNullOrBlank()) {
            mIndexItems.remove(mVariableSection)
        } else {
            mIndexItems.add(0, section)
        }
        postInvalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var index: String
        for (i in mIndexItems.indices) {
            index = mIndexItems[i]
            val fm = mPaint.fontMetrics
            canvas.drawText(index,
                    (mWidth - mPaint.measureText(index)) / 2,
                    mItemHeight / 2 + (fm.bottom - fm.top) / 2 - fm.bottom + mItemHeight * i + mTopMargin,
                    (if (i == mCurrentIndex) mTouchedPaint else mPaint))
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = width
        mHeight = max(h, oldh)
        mItemHeight = (mHeight / mIndexItems.size).toFloat()
        mTopMargin = (mHeight - mItemHeight * mIndexItems.size) / 2
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val y = event.y
                val indexSize = mIndexItems.size
                var touchedIndex = (y / mItemHeight).toInt()
                if (touchedIndex < 0) {
                    touchedIndex = 0
                } else if (touchedIndex >= indexSize) {
                    touchedIndex = indexSize - 1
                }
                mOnIndexChangedListener?.let {
                    if (touchedIndex in 0 until indexSize) {
                        if (touchedIndex != mCurrentIndex) {
                            mCurrentIndex = touchedIndex
                            mOverlayTextView?.let { overlay ->
                                overlay.visibility = VISIBLE
                                overlay.text = mIndexItems[touchedIndex]
                            }
                            it.onIndexChanged(mIndexItems[touchedIndex], touchedIndex)
                            invalidate()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mCurrentIndex = -1
                mOverlayTextView?.visibility = GONE
                invalidate()
            }
        }
        return true
    }

    fun setOverlayTextView(overlay: TextView?): SideIndexBar {
        mOverlayTextView = overlay
        return this
    }

    fun setOnIndexChangedListener(listener: OnIndexTouchedChangedListener?): SideIndexBar {
        mOnIndexChangedListener = listener
        return this
    }

    interface OnIndexTouchedChangedListener {
        fun onIndexChanged(index: String, position: Int)
    }

}
