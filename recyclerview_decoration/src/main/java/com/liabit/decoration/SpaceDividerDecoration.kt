package com.liabit.decoration

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @param spaceSize 间距宽度, 单位 dip
 * @param dividerSize 分割线宽度, 单位 px
 *
 * 对于 GridLayoutManager 和 LinearLayoutManager 可以保证四周的边距相等;
 * 对于 StaggeredGridLayoutManager 只能保证内部各个 View 之间边距相等,
 * 不能做到与 RecyclerView 四边接触的 View 边距相等,这种情况下请自行设置 RecyclerView 的 Padding
 */
class SpaceDividerDecoration(
        private val spaceSize: Float,
        private val dividerSize: Int,
        private val direction: Int = ALL) : RecyclerView.ItemDecoration() {

    private var mDrawable: Drawable? = null

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

    fun setDrawable(drawable: Drawable) {
        mDrawable = drawable
    }

    override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
    ) {
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceSize, parent.resources.displayMetrics).toInt()
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

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val drawable = mDrawable ?: return
        val layoutManager = parent.layoutManager ?: return
        if (layoutManager.childCount == 0) {
            return
        }
        val space = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceSize, parent.resources.displayMetrics).toInt()
        when (layoutManager) {
            is GridLayoutManager -> {
                val lookup = layoutManager.spanSizeLookup
                val spanCount = layoutManager.spanCount
                val childCount = parent.childCount
                if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        val spanSize = lookup.getSpanSize(position)
                        val spanIndex = lookup.getSpanIndex(position, layoutManager.spanCount)
                        //判断是否为第一排
                        val isFirstRow = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0
                        if (!isFirstRow && spanIndex == 0) {
                            // 绘制横向分割线
                            val left = 0
                            val right = parent.width
                            val top = (child.top - space / 2) - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        if (spanIndex + spanSize != spanCount) {
                            // 绘制竖向分割线
                            val left = child.right + space / 2 - dividerSize / 2
                            val right = left + dividerSize
                            val top = if (!isFirstRow) child.top - space / 2 else child.top
                            val bottom = child.bottom + space / 2
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                } else {
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        val spanSize = lookup.getSpanSize(position)
                        val spanIndex = lookup.getSpanIndex(position, layoutManager.spanCount)
                        //判断是否为第一列
                        val isFirstColumn = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0
                        if (!isFirstColumn && spanIndex == 0) {
                            // 绘制竖向分割线
                            val left = child.left - space / 2 - dividerSize / 2
                            val right = left + dividerSize
                            val top = 0
                            val bottom = parent.height
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        if (spanIndex + spanSize != spanCount) {
                            // 绘制横向分割线
                            val left = if (!isFirstColumn) child.left - space / 2 else child.left
                            val right = child.right + space / 2
                            val top = child.bottom + space / 2 - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val childCount = parent.childCount
                if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    for (i in 0 until childCount - 1) {
                        val child = parent.getChildAt(i)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        val left = 0
                        val right = parent.width
                        val top = child.bottom + params.bottomMargin + space / 2 - dividerSize / 2
                        val bottom = top + dividerSize
                        drawable.setBounds(left, top, right, bottom)
                        drawable.draw(c)
                    }
                } else {
                    for (i in 0 until childCount - 1) {
                        val child = parent.getChildAt(i)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        val left = child.right + params.rightMargin + space / 2 - dividerSize / 2
                        val right = left + dividerSize
                        val top = layoutManager.getTopDecorationHeight(child)
                        val bottom = parent.height - layoutManager.getTopDecorationHeight(child)
                        drawable.setBounds(left, top, right, bottom)
                        drawable.draw(c)
                    }
                }
            }
        }
    }
}