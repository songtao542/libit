package com.liabit.tablayout.indicator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.viewpager.widget.ViewPager
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px

class ScaleCircleIndicator(context: Context) : CircleIndicator(context) {

    private var mMaxRadius: Float = dip2px(context, 4f).toFloat()
    private val mArgbEvaluator = ArgbEvaluator()

    init {
        mSelectColor = 0xffff4444.toInt()
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 0f
        for (i in mCircles.indices) {
            val circle: Circle = mCircles[i]
            mPaint.color = circle.color
            canvas.drawCircle(circle.x, circle.y, circle.radius, mPaint)
        }
    }

    override fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float) {
        if (mCircles.isEmpty()) {
            return
        }
        val current: Circle = mCircles.get(position)
        current.radius = mRadius + (mMaxRadius - mRadius) * (1f - positionOffset)
        current.color = mArgbEvaluator.evaluate(1f - positionOffset, mColor, mSelectColor) as Int
        if (position + 1 < mCount) {
            val next: Circle = mCircles.get(position + 1)
            next.radius = mRadius + (mMaxRadius - mRadius) * positionOffset
            next.color = mArgbEvaluator.evaluate(positionOffset, mColor, mSelectColor) as Int
        }
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {
        mCurrentIndex = position
        if (mCircles.isNotEmpty()) {
            val circle: Circle = mCircles[position]
            circle.radius = mMaxRadius
            invalidate()
        }
    }

    override fun prepare() {
        mCircles.clear()
        if (mCount > 0) {
            val startX = (width - mRadius * 2 * mCount - mSpacing * (mCount - 1)) / 2
            val y = height / 2
            val currentIndex = getTabLayout()?.viewPager?.currentItem ?: 0
            for (i in 0 until mCount) {
                val radius = if (i == currentIndex) mMaxRadius else mRadius
                val color: Int = if (i == currentIndex) mSelectColor else mColor
                val circle = Circle(startX + (mRadius * 2f + mSpacing) * i, y.toFloat(), radius, color)
                mCircles.add(circle)
            }
        }
    }

    fun setMaxRadius(maxRadius: Float) {
        this.mMaxRadius = maxRadius
    }

}