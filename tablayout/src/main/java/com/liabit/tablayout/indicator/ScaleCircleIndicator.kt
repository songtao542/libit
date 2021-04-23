package com.liabit.tablayout.indicator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px
import com.liabit.tablayout.ViewPagerProxy

class ScaleCircleIndicator(context: Context) : CircleIndicator(context) {

    private var mMaxRadius: Float = dip2px(context, 4f).toFloat()
    private val mArgbEvaluator = ArgbEvaluator()
    private var mTargetPosition: Int? = null
    private var mLastPosition: Int? = null

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
        val current: Circle = mCircles[position]
        current.radius = mRadius + (mMaxRadius - mRadius) * (1f - positionOffset)
        current.color = mArgbEvaluator.evaluate(1f - positionOffset, mColor, mSelectColor) as Int
        if (position + 1 < mCount) {
            val next: Circle = mCircles[position + 1]
            next.radius = mRadius + (mMaxRadius - mRadius) * positionOffset
            next.color = mArgbEvaluator.evaluate(positionOffset, mColor, mSelectColor) as Int
        }
//        mLastPosition?.let {
//            val gap = if (position > it) 1 else -1
//            for (i in it until position) {
//                val circle = mCircles[i]
//                circle.radius = mRadius + (mMaxRadius - mRadius) * positionOffset
//            }
//        }
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {
        mCurrentIndex = position
        resetCircle(position)
    }

    override fun onPageScrollStateChanged(tabLayout: TabLayout, position: Int, state: Int) {
        if (state == ViewPager.SCROLL_STATE_IDLE && mCircles.isNotEmpty()) {
            resetCircle(position)
        }
    }

    override fun onCircleClick(viewPager: ViewPagerProxy, position: Int) {
        mLastPosition = viewPager.currentItem
        mTargetPosition = position
        super.onCircleClick(viewPager, position)
    }

    private fun resetCircle(position: Int) {
        for (index in mCircles.indices) {
            val circle = mCircles[index]
            if (index == position) {
                circle.radius = mMaxRadius
                circle.color = mSelectColor
            } else {
                circle.radius = mRadius
                circle.color = mColor
            }
        }
        invalidate()
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