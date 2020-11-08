package com.liabit.tablayout.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.TextTabView

@Suppress("unused")
class WrapTabIndicator(context: Context) : View(context), TabIndicator {
    private var mColor: Int = 0x99dddddd.toInt()
    private var mRoundRadius = 0f
    private var mRoundCorner = false
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mRect = RectF()

    init {
        mPaint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        mPaint.color = mColor
        canvas.drawRoundRect(mRect, mRoundRadius, mRoundRadius, mPaint)
    }

    override fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float) {
        val current = tabLayout.getTabViewAt(position)
        val next = tabLayout.getTabViewAt(position + 1)

        if (current == null) return

        val left: Float
        var right: Float
        val top: Float
        val bottom: Float
        val nextLeft: Float
        val nextRight: Float
        val mode = tabLayout.indicatorMode
        if (mode == TabIndicator.MATCH_TAB_WIDTH) {
            left = current.getView().left.toFloat()
            right = current.getView().right.toFloat()
            top = current.getView().top.toFloat()
            bottom = current.getView().bottom.toFloat()
            if (next != null) {
                right = next.getView().left.toFloat()
                nextLeft = next.getView().left.toFloat()
                nextRight = next.getView().right.toFloat()
            } else {
                nextLeft = current.getView().right.toFloat()
                nextRight = nextLeft + current.getView().width
            }
        } else {
            val xDiff = current.getView().width - current.getContentWidth().toFloat()
            val yDiff = current.getView().height - current.getContentHeight().toFloat()
            left = current.getView().left + xDiff / 2f
            right = current.getView().right - xDiff / 2f
            top = current.getView().top + yDiff / 2f
            bottom = current.getView().bottom - yDiff / 2f
            if (next != null) {
                val nXDiff = next.getView().width - next.getContentWidth().toFloat()
                nextLeft = next.getView().left + nXDiff / 2f
                nextRight = next.getView().right - nXDiff / 2f
            } else {
                // mock next left and right
                nextLeft = current.getView().right + xDiff / 2f
                nextRight = nextLeft + current.getContentWidth()
            }
        }
        val startInterpolator = tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_START)
        val endInterpolator = tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_END)
        mRect.left = left + (nextLeft - left) * endInterpolator.getInterpolation(positionOffset)
        mRect.top = top
        mRect.right = right + (nextRight - right) * startInterpolator.getInterpolation(positionOffset)
        mRect.bottom = bottom
        if (mRoundCorner) {
            mRoundRadius = mRect.height()
        }
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {
    }

    override fun onTabCreated(tabLayout: TabLayout, position: Int) {
        post { onPageScrolled(tabLayout, position, 0f) }
    }

    fun setColor(@ColorInt color: Int) {
        mColor = color
    }

    fun setRadius(radius: Float) {
        mRoundRadius = radius
    }

    fun setRoundCorner(round: Boolean) {
        mRoundCorner = round
    }

    override fun isFront(): Boolean {
        return false
    }

}