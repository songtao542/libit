package com.liabit.tagview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max

/**
 * Author:         songtao
 * CreateDate:     2020/11/17 17:50
 */
class BadgeIcon : AppCompatImageView {

    private var mBadgeDrawable = BadgeDrawable()
    private var mBadgeTopOffset: Float = 0f
    private var mBadgeRightOffset: Float = 0f

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeIcon, defStyleAttr, 0)
            typedArray.getString(R.styleable.BadgeIcon_badge)?.let {
                mBadgeDrawable.setBadge(it)
            }
            mBadgeTopOffset = typedArray.getDimension(R.styleable.BadgeIcon_badgeTopOffset, 0f)
            mBadgeRightOffset = typedArray.getDimension(R.styleable.BadgeIcon_badgeRightOffset, 0f)
            typedArray.recycle()
        }
    }

    fun setBadge(badge: String) {
        mBadgeDrawable.setBadge(badge)
        invalidate()
    }

    fun setBadge(badge: Int) {
        mBadgeDrawable.setBadge(badge.toString())
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val bounds = mBadgeDrawable.bounds
            val dw = bounds.width()
            it.save()
            it.translate(width - dw - paddingLeft - mBadgeRightOffset, paddingTop + mBadgeTopOffset)
            mBadgeDrawable.draw(it)
            it.restore()
        }
    }

    fun setBadgeOffset(topOffset: Float, rightOffset: Float) {
        mBadgeTopOffset = topOffset
        mBadgeRightOffset = rightOffset
    }

    private fun dp2px(dip: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.resources.displayMetrics).toInt()
    }

    private fun sp2px(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics)
    }


    private inner class BadgeDrawable : Drawable() {

        private val mTextPaint: TextPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG)
        private val mTagPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var mTagBounds: RectF = RectF()

        private var mText: String = ""

        private var mTextSize: Float = sp2px(9f)
        private var mTextColor: Int = Color.WHITE
        private var mBold: Boolean = false
        private var mTagColor: Int = Color.RED
        private var mTagPaddingLeft: Int
        private var mTagPaddingTop: Int
        private var mTagPaddingRight: Int
        private var mTagPaddingBottom: Int
        private var mTagRadius: Float = 0f

        init {
            val padding = dp2px(2f)
            mTagPaddingLeft = padding
            mTagPaddingTop = padding
            mTagPaddingRight = padding
            mTagPaddingBottom = padding

            mTextPaint.color = mTextColor
            mTextPaint.textSize = mTextSize
            mTextPaint.isFakeBoldText = mBold
            mTextPaint.style = Paint.Style.FILL
            mTextPaint.textAlign = Paint.Align.LEFT

            mTagPaint.color = mTagColor
            mTagPaint.style = Paint.Style.FILL
        }

        fun setBadge(badge: String) {
            mText = badge
            var textWidth = (mTextPaint.measureText(mText) + mTagPaddingLeft + mTagPaddingRight).toInt()
            val textHeight = (mTextPaint.textSize + mTagPaddingTop + mTagPaddingBottom).toInt()
            if (textWidth < textHeight) {
                textWidth = textHeight
            }
            setBounds(0, 0, textWidth, textHeight)
            mTagBounds.set(bounds)
            mTagRadius = max(textWidth, textHeight).toFloat()
        }

        override fun draw(canvas: Canvas) {
            if (mText.isBlank()) return
            canvas.drawRoundRect(mTagBounds, mTagRadius, mTagRadius, mTagPaint)
            val fm = mTextPaint.fontMetrics
            val textWidth: Float = mTextPaint.measureText(mText)
            val width = mTagBounds.width()
            val height = mTagBounds.height()
            val x = (width - textWidth) / 2f
            val y = height / 2f - fm.descent + (fm.descent - fm.ascent) / 2f - fm.descent / 5f
            canvas.drawText(mText, x, y + 2, mTextPaint)
        }

        override fun setAlpha(alpha: Int) {
            mTextPaint.alpha = alpha
            mTagPaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            mTextPaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }
    }

}