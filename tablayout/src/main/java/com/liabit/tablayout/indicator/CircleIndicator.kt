package com.liabit.tablayout.indicator

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px
import com.liabit.tablayout.Util.dp2px
import com.liabit.tablayout.ViewPagerProxy
import java.util.*
import kotlin.math.abs
import kotlin.math.min

@Suppress("unused")
open class CircleIndicator(context: Context) : View(context), TabIndicator {
    protected var mRadius = 0f
    protected var mColor = 0
    protected var mSelectColor = 0
    private var mFill = false
    private var mStrokeWidth = 0
    protected var mSpacing = 0
    protected var mCurrentIndex = 0
    protected var mCount = 0
    private var mSmoothScroll = true
    protected var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mCurrentX = 0f
    protected var mCircles: MutableList<Circle> = ArrayList()
    private var mDownX = 0f
    private var mDownY = 0f
    private var mTouchSlop = 0

    /**
     * 圆点是否跟随ViewPager变化
     */
    var isFollowViewPager = true

    init {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mRadius = dp2px(context, 3f)
        mSpacing = dip2px(context, 8f)
        mStrokeWidth = dip2px(context, 1f)
        mSelectColor = -0x1
        mColor = -0x1
    }

    override fun onDraw(canvas: Canvas) {
        if (mFill) {
            mPaint.style = Paint.Style.FILL
            mPaint.strokeWidth = 0f
        } else {
            mPaint.style = Paint.Style.STROKE
            mPaint.strokeWidth = mStrokeWidth.toFloat()
        }
        mPaint.color = mColor
        for (i in mCircles.indices) {
            val circle = mCircles[i]
            canvas.drawCircle(circle.x, circle.y, mRadius, mPaint)
        }
        mPaint.color = mSelectColor
        mPaint.style = Paint.Style.FILL
        if (mCircles.size > 0) {
            canvas.drawCircle(mCurrentX, height / 2.toFloat(), mRadius, mPaint)
        }
    }

    override fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float) {
        if (isFollowViewPager) {
            if (mCircles.isEmpty()) {
                return
            }
            val currentPosition = min(mCircles.size - 1, position)
            val nextPosition = min(mCircles.size - 1, position + 1)
            val current = mCircles[currentPosition]
            val next = mCircles[nextPosition]
            mCurrentX = current.x + (next.x - current.x) * tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_START).getInterpolation(positionOffset)
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = x
                mDownY = y
            }
            MotionEvent.ACTION_UP ->
                if (isClickable && abs(x - mDownX) <= mTouchSlop && abs(y - mDownY) <= mTouchSlop) {
                    var i = 0
                    while (i < mCircles.size) {
                        val circle = mCircles[i]
                        if (isInCircle(circle, x, y)) {
                            getTabLayout()?.viewPager?.let {
                                onCircleClick(it, i)
                            }
                            break
                        }
                        i++
                    }
                }
        }
        return isClickable || super.onTouchEvent(event)
    }

    protected open  fun onCircleClick(viewPager: ViewPagerProxy, position: Int) {
        viewPager.setCurrentItem(position, mSmoothScroll)
    }

    private fun isInCircle(point: Circle, x: Float, y: Float): Boolean {
        return x >= point.x - mRadius - mSpacing / 2f &&
                x <= point.x + mRadius + mSpacing / 2f &&
                y >= point.y - mRadius - mSpacing / 2f &&
                y <= point.y + mRadius + mSpacing / 2f
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {
        mCurrentIndex = position
        if (!isFollowViewPager) {
            mCurrentX = mCircles[mCurrentIndex].x
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        prepare()
    }

    protected open fun prepare() {
        mCircles.clear()
        if (mCount > 0) {
            val startX = (width - mRadius * 2 * mCount - mSpacing * (mCount - 1)) / 2
            val y = height / 2
            for (i in 0 until mCount) {
                val circle = Circle((startX + (mRadius * 2 + mSpacing) * i), y.toFloat(), mRadius, mColor)
                mCircles.add(circle)
            }
            mCurrentX = mCircles[mCurrentIndex].x
        }
    }

    var radius: Float
        get() = mRadius
        set(radius) {
            mRadius = radius
            prepare()
            invalidate()
        }

    fun setFill(fill: Boolean) {
        mFill = fill
    }

    var color: Int
        get() = mColor
        set(circleColor) {
            mColor = circleColor
            invalidate()
        }

    var selectColor: Int
        get() = mSelectColor
        set(circleColor) {
            mSelectColor = circleColor
            invalidate()
        }

    var strokeWidth: Int
        get() = mStrokeWidth
        set(strokeWidth) {
            mStrokeWidth = strokeWidth
            invalidate()
        }

    var spacing: Int
        get() = mSpacing
        set(value) {
            mSpacing = value
            prepare()
            invalidate()
        }

    fun setCount(count: Int) {
        mCount = count
    }

    var smoothScroll: Boolean
        get() = mSmoothScroll
        set(value) {
            mSmoothScroll = value
        }

    protected data class Circle(var x: Float, var y: Float, var radius: Float, var color: Int)

}