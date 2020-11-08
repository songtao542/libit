package com.liabit.tablayout.indicator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px
import kotlin.math.abs

@Suppress("unused")
class BezierTabIndicator(context: Context) : View(context), TabIndicator {
    private var mLeftCircleRadius = 0f
    private var mLeftCircleX = 0f
    private var mRightCircleRadius = 0f
    private var mRightCircleX = 0f
    private var mYOffset = 0f
    private var mMaxCircleRadius = 0f
    private var mMinCircleRadius = 0f
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPath = Path()
    private var mColors: IntArray? = null
    private val mArgbEvaluator = ArgbEvaluator()

    init {
        mPaint.style = Paint.Style.FILL
        mMaxCircleRadius = dip2px(context, 3.5f).toFloat()
        mMinCircleRadius = dip2px(context, 2f).toFloat()
        mYOffset = dip2px(context, 1.5f).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(mLeftCircleX, height - mYOffset - mMaxCircleRadius, mLeftCircleRadius, mPaint)
        canvas.drawCircle(mRightCircleX, height - mYOffset - mMaxCircleRadius, mRightCircleRadius, mPaint)
        drawBezierCurve(canvas)
    }

    /**
     * 绘制贝塞尔曲线
     *
     * @param canvas
     */
    private fun drawBezierCurve(canvas: Canvas) {
        mPath.reset()
        val y = height - mYOffset - mMaxCircleRadius
        mPath.moveTo(mRightCircleX, y)
        mPath.lineTo(mRightCircleX, y - mRightCircleRadius)
        mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mLeftCircleX, y - mLeftCircleRadius)
        mPath.lineTo(mLeftCircleX, y + mLeftCircleRadius)
        mPath.quadTo(mRightCircleX + (mLeftCircleX - mRightCircleX) / 2.0f, y, mRightCircleX, y + mRightCircleRadius)
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float) {
        mColors?.let {
            if (it.isNotEmpty()) {
                val currentColor = it[abs(position) % it.size]
                val nextColor = it[abs(position + 1) % it.size]
                val color = mArgbEvaluator.evaluate(positionOffset, currentColor, nextColor) as Int
                mPaint.color = color
            }
        }
        val current = tabLayout.getTabViewAt(position)
        val next = tabLayout.getTabViewAt(position + 1)

        if (current == null) return

        val left = current.getView().left + current.getView().width / 2f
        val right = if (next != null) {
            next.getView().left + next.getView().width / 2f
        } else {
            current.getView().right + current.getView().width / 2f
        }

        val startInterpolator = tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_START)
        val endInterpolator = tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_END)
        mLeftCircleX = left + (right - left) * startInterpolator.getInterpolation(positionOffset)
        mRightCircleX = left + (right - left) * endInterpolator.getInterpolation(positionOffset)
        mLeftCircleRadius = mMaxCircleRadius + (mMinCircleRadius - mMaxCircleRadius) * endInterpolator.getInterpolation(positionOffset)
        mRightCircleRadius = mMinCircleRadius + (mMaxCircleRadius - mMinCircleRadius) * startInterpolator.getInterpolation(positionOffset)
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {}

    fun setColor(vararg colors: Int) {
        mColors = intArrayOf(*colors)
    }

    fun setYOffset(yOffset: Float) {
        mYOffset = yOffset
    }

}