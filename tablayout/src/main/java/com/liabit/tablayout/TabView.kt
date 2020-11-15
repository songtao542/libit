package com.liabit.tablayout

import android.view.View

interface TabView {
    /**
     * @param index 当前TabView的位置
     * @param positionOffset 该Tab对应的页面显示在屏幕中的比例
     */
    fun onPageScrolled(index: Int, positionOffset: Float)

    /**
     * @param index 当前TabView的位置
     * @param position 被选中的页面位置
     */
    fun onPageSelected(index: Int, position: Int)

    fun getView(): View

    fun getContentWidth(): Int

    fun getContentHeight(): Int
}