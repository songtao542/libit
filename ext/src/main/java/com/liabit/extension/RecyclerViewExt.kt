package com.liabit.extension

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewScrollPositionAndOffset(private val recyclerView: RecyclerView) {
    private var mExpandOffset: Int = 0
    private var mExpandPosition: Int = 0

    fun rememberScrollPositionAndOffset() {
        recyclerView.layoutManager?.getChildAt(0)?.let {
            //获取与该view的顶部的偏移量
            mExpandOffset = it.left
            //得到该View的数组位置
            mExpandPosition = recyclerView.layoutManager?.getPosition(it) ?: 0
        }
    }

    /**
     * 让RecyclerView滚动到指定位置
     */
    fun resetScrollPositionAndOffset() {
        val layoutManager = recyclerView.layoutManager ?: return
        when (layoutManager) {
            is LinearLayoutManager -> layoutManager.scrollToPositionWithOffset(mExpandPosition, mExpandOffset)
            is GridLayoutManager -> layoutManager.scrollToPositionWithOffset(mExpandPosition, mExpandOffset)
            is StaggeredGridLayoutManager -> layoutManager.scrollToPositionWithOffset(mExpandPosition, mExpandOffset)
        }
        mExpandOffset = 0
        mExpandPosition = 0
    }
}



