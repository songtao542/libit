package com.liabit.util

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @param size the space width
 * @param unit default TypedValue.COMPLEX_UNIT_DIP
 *
 * 对于 GridLayoutManager 和 LinearLayoutManager 可以保证四周的边距相等;
 * 对于 StaggeredGridLayoutManager 只能保证内部各个 View 之间边距相等,
 * 不能做到与 RecyclerView 四边接触的 View 边距相等,这种情况下请自行设置 RecyclerView 的 Padding
 */
class SpaceDecoration(
        private val size: Float,
        private val unit: Int = TypedValue.COMPLEX_UNIT_DIP,
        private val direction: Int = ALL) : RecyclerView.ItemDecoration() {

    companion object {
        /**
         * 只考虑横向分割线
         */
        const val HORIZONTAL = 0

        /**
         * 只考虑纵向分割线
         */
        const val VERTICAL = 1

        /**
         * 横纵向皆有分割线
         */
        const val ALL = 2
    }

    constructor(size: Float, direction: Int = ALL) : this(size, TypedValue.COMPLEX_UNIT_DIP, direction)

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val space = if (unit == TypedValue.COMPLEX_UNIT_DIP) {
            TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    size,
                    parent.resources.displayMetrics
            ).toInt()
        } else {
            size.toInt()
        }
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val spanCount = layoutManager.spanCount
                val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, spanCount)
                val spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
                val spanGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                val lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(layoutManager.itemCount - 1, spanCount)
                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    val left = if (spanIndex == 0) space else space / 2
                    val right = if ((spanIndex + spanSize) == spanCount) space else space / 2
                    val top = if (spanGroupIndex == 0) space else space / 2
                    val bottom = if (spanGroupIndex == lastGroupIndex) space else space / 2
                    when (direction) {
                        VERTICAL -> outRect.set(left, 0, right, 0)
                        HORIZONTAL -> outRect.set(0, top, right, bottom)
                        else -> outRect.set(left, top, right, bottom)
                    }
                } else {
                    val left = if (spanGroupIndex == 0) space else space / 2
                    val right = if (spanGroupIndex == lastGroupIndex) space else space / 2
                    val top = if (spanIndex == 0) space else space / 2
                    val bottom = if ((spanIndex + spanSize) == spanCount) space else space / 2
                    when (direction) {
                        VERTICAL -> outRect.set(left, 0, right, 0)
                        HORIZONTAL -> outRect.set(0, top, 0, bottom)
                        else -> outRect.set(left, top, right, bottom)
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                when (direction) {
                    VERTICAL -> outRect.set(space / 2, 0, space / 2, 0)
                    HORIZONTAL -> outRect.set(0, space / 2, 0, space / 2)
                    else -> outRect.set(space / 2, space / 2, space / 2, space / 2)
                }
            }
            is LinearLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val top = if (position == 0) space else space / 2
                    val bottom = if (position == layoutManager.itemCount - 1) space else space / 2
                    when (direction) {
                        VERTICAL -> outRect.set(space, 0, space, 0)
                        HORIZONTAL -> outRect.set(0, top, 0, bottom)
                        else -> outRect.set(space, top, space, bottom)
                    }
                } else {
                    val left = if (position == 0) space else space / 2
                    val right = if (position == layoutManager.itemCount - 1) space else space / 2
                    when (direction) {
                        VERTICAL -> outRect.set(left, 0, right, 0)
                        HORIZONTAL -> outRect.set(0, space, 0, space)
                        else -> outRect.set(left, space, right, space)
                    }
                }
            }
            else -> {
                super.getItemOffsets(outRect, view, parent, state)
            }
        }
    }
}