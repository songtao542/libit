package com.liabit.tablayout.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px

class TriangularTabIndicator(context: Context) : View(context), TabIndicator {
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mLineHeight = 0
    private var mLineColor = 0xff239834.toInt()
    private var mTriangleHeight = 0
    private var mTriangleWidth = 0
    private var mInverted = false
    private var mYOffset = 0f
    private val mPath = Path()
    private val mStartInterpolator: Interpolator = LinearInterpolator()
    private var mAnchorX = 0f

    init {
        mPaint.style = Paint.Style.FILL
        mLineHeight = dip2px(context, 3f)
        mTriangleWidth = dip2px(context, 14f)
        mTriangleHeight = dip2px(context, 8f)
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mLineColor
        if (mInverted) {
            canvas.drawRect(0f, mYOffset, width.toFloat(), mYOffset + mLineHeight, mPaint)
        } else {
            canvas.drawRect(0f, height - mYOffset - mLineHeight, width.toFloat(), height - mYOffset, mPaint)
        }
        mPath.reset()
        if (mInverted) {
            mPath.moveTo(mAnchorX - mTriangleWidth / 2f, mYOffset)
            mPath.lineTo(mAnchorX, mYOffset + mTriangleHeight)
            mPath.lineTo(mAnchorX + mTriangleWidth / 2f, mYOffset)
        } else {
            mPath.moveTo(mAnchorX - mTriangleWidth / 2f, height - mYOffset)
            mPath.lineTo(mAnchorX, height - mTriangleHeight - mYOffset)
            mPath.lineTo(mAnchorX + mTriangleWidth / 2f, height - mYOffset)
        }
        mPath.close()
        canvas.drawPath(mPath, mPaint)
    }

    override fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float) {
        val current = tabLayout.getTabViewAt(position)
        val next = tabLayout.getTabViewAt(position + 1)

        if (current == null) return

        val left = current.getView().left + current.getView().width / 2.toFloat()
        val right: Float
        right = if (next != null) {
            next.getView().left + next.getView().width / 2.toFloat()
        } else {
            current.getView().right + current.getView().width / 2.toFloat()
        }
        mAnchorX = left + (right - left) * mStartInterpolator.getInterpolation(positionOffset)
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {}

    fun setLineColor(lineColor: Int) {
        mLineColor = lineColor
    }

    fun setInverted(inverted: Boolean) {
        mInverted = inverted
    }

    fun setYOffset(yOffset: Float) {
        mYOffset = yOffset
    }


}