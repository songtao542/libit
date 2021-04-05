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
        private val mBadgePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private var mBadgeBounds: RectF = RectF()

        private var mText: String = ""

        private var mTextSize: Float = sp2px(9f)
        private var mTextColor: Int = Color.WHITE
        private var mBold: Boolean = false
        private var mBadgeColor: Int = Color.RED
        private var mBadgePaddingLeft: Int
        private var mBadgePaddingTop: Int
        private var mBadgePaddingRight: Int
        private var mBadgePaddingBottom: Int
        private var mBadgeRadius: Float = 0f

        init {
            val padding = dp2px(2f)
            mBadgePaddingLeft = padding
            mBadgePaddingTop = padding
            mBadgePaddingRight = padding
            mBadgePaddingBottom = padding

            mTextPaint.color = mTextColor
            mTextPaint.textSize = mTextSize
            mTextPaint.isFakeBoldText = mBold
            mTextPaint.style = Paint.Style.FILL
            mTextPaint.textAlign = Paint.Align.LEFT

            mBadgePaint.color = mBadgeColor
            mBadgePaint.style = Paint.Style.FILL
        }

        fun setBadge(badge: String) {
            mText = badge
            var textWidth = (mTextPaint.measureText(mText) + mBadgePaddingLeft + mBadgePaddingRight).toInt()
            val textHeight = (mTextPaint.textSize + mBadgePaddingTop + mBadgePaddingBottom).toInt()
            if (textWidth < textHeight) {
                textWidth = textHeight
            }
            setBounds(0, 0, textWidth, textHeight)
            mBadgeBounds.set(bounds)
            mBadgeRadius = max(textWidth, textHeight).toFloat()
        }

        override fun draw(canvas: Canvas) {
            if (mText.isBlank()) return
            canvas.drawRoundRect(mBadgeBounds, mBadgeRadius, mBadgeRadius, mBadgePaint)
            val fm = mTextPaint.fontMetrics
            val textWidth: Float = mTextPaint.measureText(mText)
            val width = mBadgeBounds.width()
            val height = mBadgeBounds.height()
            val x = (width - textWidth) / 2f
            val y = height / 2f - fm.descent + (fm.descent - fm.ascent) / 2f - fm.descent / 5f
            canvas.drawText(mText, x, y + 2, mTextPaint)
        }

        override fun setAlpha(alpha: Int) {
            mTextPaint.alpha = alpha
            mBadgePaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            mTextPaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSLUCENT
        }
    }

}