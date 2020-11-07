package com.liabit.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 间距装饰器，使每一个 Item 的四边保持相等的边距;
 *
 * @param space 间距宽度, 单位 dip
 * @param spaceDirection 在哪个方向上添加间距
 * @param divider 分割线宽度, 单位 px
 * @param dividerColor 分割线颜色, 单位 px
 */
class SpaceDecoration(
        private val space: Float,
        private val spaceDirection: Direction,
        private val divider: Int,
        @ColorInt dividerColor: Int
) : RecyclerView.ItemDecoration() {

    private var mDrawable: Drawable? = null

    constructor(space: Float) : this(space, Direction.ALL, 0, Color.TRANSPARENT)

    constructor(space: Float, spaceDirection: Direction) : this(space, spaceDirection, 0, Color.TRANSPARENT)

    constructor(space: Float, divider: Int) : this(space, Direction.ALL, divider, Color.GRAY)

    constructor(space: Float, spaceDirection: Direction, divider: Int)
            : this(space, spaceDirection, divider, Color.GRAY)

    init {
        if (dividerColor != Color.TRANSPARENT) {
            mDrawable = ColorDrawable(dividerColor)
        }
    }

    enum class Direction {
        /**
         * 只考虑横向分割线
         */
        HORIZONTAL,

        /**
         * 只考虑纵向分割线
         */
        VERTICAL,

        /**
         * 与 RecyclerView 接壤的地方不加分割线
         */
        IGNORE_EDGE,

        /**
         * 横纵向皆有分割线
         */
        ALL
    }

    fun setDividerColor(@ColorInt color: Int) {
        mDrawable = ColorDrawable(color)
    }

    fun setDrawable(drawable: Drawable) {
        mDrawable = drawable
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        var spaceSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, parent.resources.displayMetrics).toInt()
        // 间距大小必须大于等于 分割线宽度
        if (spaceSize < divider) {
            spaceSize = divider
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
                    if (spaceDirection == Direction.IGNORE_EDGE) {
                        val ew = (spanCount - 1) * spaceSize / spanCount
                        val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = if (spanIndex + spanSize == spanCount) 0 else ew - left
                        val top = if (spanGroupIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if (spanGroupIndex == lastGroupIndex) spaceSize else spaceSize / 2
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        val top = if (spanGroupIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if (spanGroupIndex == lastGroupIndex) spaceSize else spaceSize / 2
                        when (spaceDirection) {
                            Direction.VERTICAL -> outRect.set(left, 0, right, 0)
                            Direction.HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                } else {
                    if (spaceDirection == Direction.IGNORE_EDGE) {
                        val eh = (spanCount - 1) * spaceSize / spanCount
                        val left = if (spanGroupIndex == 0) spaceSize else spaceSize / 2
                        val right = if (spanGroupIndex == lastGroupIndex) spaceSize else spaceSize / 2
                        val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = if ((spanIndex + spanSize) == spanCount) 0 else eh - top
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanGroupIndex == 0) spaceSize else spaceSize / 2
                        val right = if (spanGroupIndex == lastGroupIndex) spaceSize else spaceSize / 2
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        when (spaceDirection) {
                            Direction.VERTICAL -> outRect.set(left, 0, right, 0)
                            Direction.HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                val layoutParams = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
                val position = layoutParams.viewLayoutPosition
                val spanCount = layoutManager.spanCount
                val spanIndex = layoutParams.spanIndex
                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    if (spaceDirection == Direction.IGNORE_EDGE) {
                        val ew = (spanCount - 1) * spaceSize / spanCount
                        val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = ew - left
                        val top = if (position < spanCount) spaceSize else spaceSize / 2
                        val bottom = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        val top = if (position < spanCount) spaceSize else spaceSize / 2
                        val bottom = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                        when (spaceDirection) {
                            Direction.VERTICAL -> outRect.set(left, 0, right, 0)
                            Direction.HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                } else {
                    if (spaceDirection == Direction.IGNORE_EDGE) {
                        val eh = (spanCount - 1) * spaceSize / spanCount
                        val left = if (position < spanCount) spaceSize else spaceSize / 2
                        val right = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                        val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = eh - top
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (position < spanCount) spaceSize else spaceSize / 2
                        val right = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        when (spaceDirection) {
                            Direction.VERTICAL -> outRect.set(left, 0, right, 0)
                            Direction.HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val top = if (position == 0) spaceSize else spaceSize / 2
                    val bottom = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                    when (spaceDirection) {
                        Direction.VERTICAL -> outRect.set(spaceSize, 0, spaceSize, 0)
                        Direction.HORIZONTAL, Direction.IGNORE_EDGE -> outRect.set(0, top, 0, bottom)
                        else -> outRect.set(spaceSize, top, spaceSize, bottom)
                    }
                } else {
                    val left = if (position == 0) spaceSize else spaceSize / 2
                    val right = if (position == layoutManager.itemCount - 1) spaceSize else spaceSize / 2
                    when (spaceDirection) {
                        Direction.VERTICAL, Direction.IGNORE_EDGE -> outRect.set(left, 0, right, 0)
                        Direction.HORIZONTAL -> outRect.set(0, spaceSize, 0, spaceSize)
                        else -> outRect.set(left, spaceSize, right, spaceSize)
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
        val spaceSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, space, parent.resources.displayMetrics).toInt()
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
                        val spanGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                        val lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(layoutManager.itemCount - 1, spanCount)
                        // 判断是否为第一排
                        val isFirstRow = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0
                        // 不是第一行且是第一列，直接一条横线从RecyclerView的左边到右边
                        if (spaceDirection != Direction.VERTICAL && !isFirstRow && spanIndex == 0) {
                            // 绘制横向分割线
                            val left = 0
                            val right = parent.width
                            val top = (child.top - spaceSize / 2) - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一列，在 child 右边绘制一条竖线，高度为 child.height + space
                        if (spaceDirection != Direction.HORIZONTAL && spanIndex + spanSize != spanCount) {
                            // 绘制竖向分割线
                            val left = child.right + spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = if (!isFirstRow) child.top - spaceSize / 2 else child.top
                            val bottom = if (spanGroupIndex == lastGroupIndex) {
                                child.bottom + spaceSize
                            } else {
                                child.bottom + spaceSize / 2
                            }
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
                        val spanGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                        val lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(layoutManager.itemCount - 1, spanCount)
                        // 判断是否为第一列
                        val isFirstColumn = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount) == 0
                        // 不是第一列且是第一行, 直接一条竖线从RecyclerView的上边到下边
                        if (spaceDirection != Direction.HORIZONTAL && !isFirstColumn && spanIndex == 0) {
                            // 绘制竖向分割线
                            val left = child.left - spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = 0
                            val bottom = parent.height
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一行，在 child 顶部绘制一条横线，宽度为 child.width + space
                        if (spaceDirection != Direction.VERTICAL && spanIndex + spanSize != spanCount) {
                            // 绘制横向分割线
                            val left = if (!isFirstColumn) child.left - spaceSize / 2 else child.left
                            val right = if (spanGroupIndex == lastGroupIndex) {
                                child.right + spaceSize
                            } else {
                                child.right + spaceSize / 2
                            }
                            val top = child.bottom + spaceSize / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                }
            }
            is StaggeredGridLayoutManager -> {
                val spanCount = layoutManager.spanCount
                val childCount = parent.childCount
                val half = parent.width / spanCount / 2
                if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        // 如果不是最后一个，则在底部绘制横线
                        if (spaceDirection != Direction.VERTICAL && position != layoutManager.itemCount - 1) {
                            // 绘制横向分割线
                            val left = if (child.left < half) {
                                child.left - params.marginStart - spaceSize
                            } else {
                                child.left - params.marginStart - spaceSize / 2 - divider / 2
                            }
                            val right = if (child.right > half * (spanCount * 2 - 1)) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + divider / 2
                            }
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是第一列
                        if (spaceDirection != Direction.HORIZONTAL && child.left > parent.width / spanCount / 2) {
                            // 绘制竖向分割线
                            val left = child.left - params.marginStart - spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = if (position < spanCount) {
                                child.top - params.topMargin - spaceSize
                            } else {
                                child.top - params.topMargin - spaceSize / 2
                            }
                            val bottom = if (position == layoutManager.itemCount - 1) {
                                child.bottom + params.bottomMargin + spaceSize
                            } else {
                                child.bottom + params.bottomMargin + spaceSize / 2
                            }
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        if (spaceDirection != Direction.HORIZONTAL && (position == (layoutManager.itemCount - 1)
                                        || position == (layoutManager.itemCount - 2)
                                        || position == (layoutManager.itemCount - 3))
                                && child.right < (parent.width - parent.width / spanCount / 2)) {
                            // 绘制竖向分割线
                            val left = child.right + params.marginEnd + spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = child.top - params.topMargin - spaceSize / 2
                            val bottom = if (position == layoutManager.itemCount - 1) {
                                child.bottom + params.bottomMargin + spaceSize
                            } else {
                                child.bottom + params.bottomMargin + spaceSize / 2
                            }
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                } else {
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        // 如果不是最后一个，则在右边绘制分割线
                        if (spaceDirection != Direction.HORIZONTAL && position != layoutManager.itemCount - 1) {
                            // 绘制纵向分割线
                            val left = child.right + params.marginEnd + spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = if (child.top < half) {
                                child.top - params.topMargin - spaceSize
                            } else {
                                child.top - params.topMargin - spaceSize / 2 - divider / 2
                            }
                            val bottom = if (child.bottom > half * (spanCount * 2 - 1)) {
                                child.bottom + params.bottomMargin + spaceSize
                            } else {
                                child.bottom + params.bottomMargin + spaceSize / 2 + divider / 2
                            }
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一行
                        if (spaceDirection != Direction.VERTICAL && child.bottom < parent.height - parent.height / spanCount / 2) {
                            // 绘制横向分割线
                            val left = if (position < spanCount) {
                                child.left - params.marginStart - spaceSize
                            } else {
                                child.left - params.marginStart - spaceSize / 2 - divider / 2
                            }
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + divider / 2
                            }
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        if (spaceDirection != Direction.VERTICAL && (position == (layoutManager.itemCount - 1)
                                        || position == (layoutManager.itemCount - 2)
                                        || position == (layoutManager.itemCount - 3))
                                && child.top > parent.height / spanCount / 2) {
                            // 绘制横向分割线
                            val left = child.left - params.marginStart - spaceSize / 2 - divider / 2
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + divider / 2
                            }
                            val top = child.top - params.topMargin - spaceSize / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val childCount = parent.childCount
                if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    if (spaceDirection != Direction.VERTICAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = 0
                            val right = parent.width
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                } else {
                    if (spaceDirection != Direction.HORIZONTAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = child.right + params.rightMargin + spaceSize / 2 - divider / 2
                            val right = left + divider
                            val top = 0
                            val bottom = parent.height
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                }
            }
        }
    }
}