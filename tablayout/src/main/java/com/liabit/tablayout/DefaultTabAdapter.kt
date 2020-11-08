package com.liabit.tablayout

import android.content.Context
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.liabit.tablayout.indicator.LineTabIndicator

@Suppress("unused")
open class DefaultTabAdapter : TabAdapter {

    private val mStartInterpolator: Interpolator = LinearInterpolator()
    private val mEndInterpolator: Interpolator = LinearInterpolator()
    private var mSelectColor = 0xff007cee.toInt()
    private var mNormalColor = 0xff000000.toInt()
    private var mSelectTextSize = 16f
    private var mNormalTextSize = 16f

    override fun onCreateTabView(context: Context, index: Int, title: CharSequence?): TextTabView {
        return TextTabView(context).apply {
            setSelectTextSize(mSelectTextSize)
            setNormalTextSize(mNormalTextSize)
            setNormalTextColor(mNormalColor)
            setSelectTextColor(mSelectColor)
            text = title
        }
    }

    fun setSelectTextColor(color: Int) {
        mSelectColor = color
    }

    fun setNormalTextColor(color: Int) {
        mNormalColor = color
    }

    fun setSelectTextSize(textSize: Float) {
        mSelectTextSize = textSize
    }

    fun setNormalTextSize(textSize: Float) {
        mNormalTextSize = textSize
    }

    override fun getTabWeight(context: Context, index: Int): Float {
        return 1f
    }

    override fun getTabMinWidth(context: Context, index: Int): Int {
        return 0
    }

    override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
        return LineTabIndicator(context)
    }

    override fun onCreateInterpolator(type: Int): Interpolator {
        return if (type == TabIndicator.INTERPOLATOR_START) mStartInterpolator else mEndInterpolator
    }
}