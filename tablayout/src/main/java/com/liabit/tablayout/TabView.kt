package com.liabit.tablayout

import android.view.View

interface TabView {
    /**
     * @param positionOffset 该Tab对应的页面显示在屏幕中的比例
     */
    fun onPageScrolled(positionOffset: Float)

    fun getView(): View

    fun getContentWidth(): Int

    fun getContentHeight(): Int
}