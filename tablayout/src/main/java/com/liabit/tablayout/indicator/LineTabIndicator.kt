package com.liabit.tablayout.indicator

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.TabLayout
import com.liabit.tablayout.Util.dip2px
import kotlin.math.abs

@Suppress("unused")
class LineTabIndicator(context: Context) : View(context), TabIndicator {
    private var mLineHeight = 0f
    private val mRadius = 0f
    private var mPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mColors: IntArray? = null
    private val mArgbEvaluator = ArgbEvaluator()
    private val mRect = RectF()

    init {
        mPaint.style = Paint.Style.FILL
        mLineHeight = dip2px(context, 3f).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(mRect, mRadius, mRadius, mPaint)
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

        val left: Float
        val right: Float
        var nextLeft = 0f
        var nextRight = 0f

        val mode = tabLayout.indicatorMode

        if (mode == TabIndicator.MATCH_TAB_WIDTH) {
            left = current.getView().left.toFloat()
            right = current.getView().right.toFloat()
            if (next != null) {
                nextLeft = next.getView().left.toFloat()
                nextRight = next.getView().right.toFloat()
            }
        } else {
            val currentDiff = (current.getView().width - current.getContentWidth()) / 2f
            left = current.getView().left + currentDiff
            right = current.getView().right - currentDiff
            if (next != null) {
                val nextDiff = (next.getView().width - next.getContentWidth()) / 2f
                nextLeft = next.getView().left + nextDiff
                nextRight = next.getView().right - nextDiff
            }
        }

        mRect.left = left + (nextLeft - left) * tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_START).getInterpolation(positionOffset)
        mRect.right = right + (nextRight - right) * tabLayout.getInterpolator(TabIndicator.INTERPOLATOR_END).getInterpolation(positionOffset)
        mRect.top = height - mLineHeight
        mRect.bottom = height.toFloat()
        invalidate()
    }

    override fun onPageSelected(tabLayout: TabLayout, position: Int) {}

    fun setColor(vararg colors: Int) {
        mColors = intArrayOf(*colors)
    }

}