package com.liabit.recyclerview.nested

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

/**
 * 固定在顶部的View适配器
 */
interface FixedViewAdapter {
    /**
     * @return 固定在顶部的 View
     */
    fun getView(inflater: LayoutInflater, parent: LinearLayout): View
}