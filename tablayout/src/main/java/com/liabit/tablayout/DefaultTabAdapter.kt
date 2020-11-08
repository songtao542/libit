package com.liabit.tablayout

import android.content.Context
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.liabit.tablayout.indicator.LineTabIndicator

open class DefaultTabAdapter : TabAdapter {

    private val mStartInterpolator: Interpolator = LinearInterpolator()
    private val mEndInterpolator: Interpolator = LinearInterpolator()

    override fun onCreateTabView(context: Context, index: Int, title: CharSequence?): TextTabView {
        val simpleTabView = TextTabView(context)
        simpleTabView.setSelectTextSize(16f)
        simpleTabView.text = title
        return simpleTabView
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