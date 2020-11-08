package com.liabit.tablayout

import android.content.Context
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.liabit.tablayout.TabIndicator.InterpolatorType

interface TabAdapter {

    fun onCreateTabView(context: Context, index: Int, title: CharSequence?): TabView? {
        return null
    }

    fun getTabWeight(context: Context, index: Int): Float {
        return 1f
    }

    fun getTabMinWidth(context: Context, index: Int): Int {
        return 0
    }

    fun onCreateTabIndicator(context: Context, count: Int): TabIndicator

    fun onCreateInterpolator(@InterpolatorType type: Int): Interpolator {
        return LinearInterpolator()
    }
}