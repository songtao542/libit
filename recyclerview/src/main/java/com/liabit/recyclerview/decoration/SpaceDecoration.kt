package com.liabit.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 间距装饰器，使每一个 Item 的四边保持相等的边距;
 *
 * @param space 间距宽度, 单位 px
 * @param flags 在哪个方向上添加间距
 * @param divider 分割线宽度, 单位 px
 * @param dividerColor 分割线颜色
 */
@Suppress("unused")
class SpaceDecoration(
    private val space: Float,
    private val flags: Int,
    private val divider: Float,
    @ColorInt dividerColor: Int
) : RecyclerView.ItemDecoration() {

    companion object {
        /**
         * 只显示横向分割线
         */
        const val ONLY_HORIZONTAL = 0x000001

        /**
         * 只显示纵向分割线
         */
        const val ONLY_VERTICAL = 0x000010

        private const val DIRECTION_MASK = 0x000011

        /**
         * 忽略与滚动同向的边界的Space
         */
        const val IGNORE_MAIN_AXIS_EDGE = 0x000100

        /**
         * 忽略与滚动垂直方法的起始边界的Space
         */
        const val IGNORE_CROSS_AXIS_START = 0x001000

        /**
         * 忽略与滚动垂直方法的末尾边界的Space
         */
        const val IGNORE_CROSS_AXIS_END = 0x010000

        /**
         * 横纵向皆有分割线
         */
        const val ALL = ONLY_HORIZONTAL or ONLY_VERTICAL
    }

    private var mDrawable: Drawable? = null

    private var mDividerPadding: Float = 0f

    constructor(space: Float) : this(space, ALL, 0f, Color.TRANSPARENT)

    constructor(space: Float, flags: Int) : this(space, flags, 0f, Color.TRANSPARENT)

    constructor(space: Float, divider: Float) : this(space, ALL, divider, Color.GRAY)

    constructor(space: Float, flags: Int, divider: Float) : this(space, flags, divider, Color.GRAY)

    init {
        if (dividerColor != Color.TRANSPARENT) {
            mDrawable = ColorDrawable(dividerColor)
        }
    }

    /**
     * 只对 LinearLayoutManager 起作用
     */
    fun setDividerPadding(padding: Float) {
        mDividerPadding = padding
    }

    fun setDividerColor(@ColorInt color: Int) {
        mDrawable = ColorDrawable(color)
    }

    fun setDividerDrawable(drawable: Drawable) {
        mDrawable = drawable
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        // 间距大小必须大于等于 分割线宽度
        val spaceSize = if (space < divider) divider else space
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val spanCount = layoutManager.spanCount
                val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, spanCount)
                val spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
                val spanGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                val lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(layoutManager.itemCount - 1, spanCount)
                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    if ((flags and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val realSpanCount = spanCount / spanSize
                        val ew = (realSpanCount - 1) * spaceSize / realSpanCount
                        val left = (spanIndex / spanSize) % realSpanCount * (spaceSize - ew)
                        //val ew = (spanCount - 1) * spaceSize / spanCount
                        //val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = if (spanIndex + spanSize == spanCount) 0 else ew - left
                        val top = if (spanGroupIndex == 0) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (spanGroupIndex == lastGroupIndex) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        val top = if (spanGroupIndex == 0) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (spanGroupIndex == lastGroupIndex) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    }
                } else {
                    if ((flags and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val left = if (spanGroupIndex == 0) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (spanGroupIndex == lastGroupIndex) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val realSpanCount = spanCount / spanSize
                        val eh = (realSpanCount - 1) * spaceSize / realSpanCount
                        val top = (spanIndex / spanSize) % realSpanCount * (spaceSize - eh)
                        //val eh = (spanCount - 1) * spaceSize / spanCount
                        //val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = if ((spanIndex + spanSize) == spanCount) 0 else eh - top
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    } else {
                        val left = if (spanGroupIndex == 0) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (spanGroupIndex == lastGroupIndex) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
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
                    if ((flags and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val ew = (spanCount - 1) * spaceSize / spanCount
                        val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = ew - left
                        val top = if (position < spanCount) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (position == layoutManager.itemCount - 1) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        val top = if (position < spanCount) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (position == layoutManager.itemCount - 1) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    }
                } else {
                    if ((flags and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val eh = (spanCount - 1) * spaceSize / spanCount
                        val left = if (position < spanCount) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (position == layoutManager.itemCount - 1) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = eh - top
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    } else {
                        val left = if (position < spanCount) {
                            if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (position == layoutManager.itemCount - 1) {
                            if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        when (flags and DIRECTION_MASK) {
                            ONLY_VERTICAL -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                            else -> outRect.set(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val top = if (position == 0) {
                        if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    val bottom = if (position == layoutManager.itemCount - 1) {
                        if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    when (flags and DIRECTION_MASK) {
                        ONLY_VERTICAL -> outRect.set(spaceSize.toInt(), 0, spaceSize.toInt(), 0)
                        ONLY_HORIZONTAL, IGNORE_MAIN_AXIS_EDGE -> outRect.set(0, top.toInt(), 0, bottom.toInt())
                        else -> outRect.set(spaceSize.toInt(), top.toInt(), spaceSize.toInt(), bottom.toInt())
                    }
                } else {
                    val left = if (position == 0) {
                        if ((flags and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    val right = if (position == layoutManager.itemCount - 1) {
                        if ((flags and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    when (flags and DIRECTION_MASK) {
                        ONLY_VERTICAL, IGNORE_MAIN_AXIS_EDGE -> outRect.set(left.toInt(), 0, right.toInt(), 0)
                        ONLY_HORIZONTAL -> outRect.set(0, spaceSize.toInt(), 0, spaceSize.toInt())
                        else -> outRect.set(left.toInt(), spaceSize.toInt(), right.toInt(), spaceSize.toInt())
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
        var direction = flags and IGNORE_CROSS_AXIS_START.inv()
        direction = direction and IGNORE_CROSS_AXIS_END.inv()
        direction = direction and IGNORE_MAIN_AXIS_EDGE.inv()
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
                        if (direction != ONLY_VERTICAL && !isFirstRow && spanIndex == 0) {
                            // 绘制横向分割线
                            val left = 0
                            val right = parent.width
                            val top = (child.top - space / 2) - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left, top.toInt(), right, bottom.toInt())
                            drawable.draw(c)
                        }
                        // 不是最后一列，在 child 右边绘制一条竖线，高度为 child.height + space
                        if (direction != ONLY_HORIZONTAL && spanIndex + spanSize != spanCount) {
                            // 绘制竖向分割线
                            val left = child.right + space / 2 - divider / 2
                            val right = left + divider
                            val top = if (!isFirstRow) child.top - space / 2 else child.top
                            val bottom = if (spanGroupIndex == lastGroupIndex) {
                                child.bottom + space
                            } else {
                                child.bottom + space / 2
                            }
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
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
                        if (direction != ONLY_HORIZONTAL && !isFirstColumn && spanIndex == 0) {
                            // 绘制竖向分割线
                            val left = child.left - space / 2 - divider / 2
                            val right = left + divider
                            val top = 0
                            val bottom = parent.height
                            drawable.setBounds(left.toInt(), top, right.toInt(), bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一行，在 child 顶部绘制一条横线，宽度为 child.width + space
                        if (direction != ONLY_VERTICAL && spanIndex + spanSize != spanCount) {
                            // 绘制横向分割线
                            val left = if (!isFirstColumn) child.left - space / 2 else child.left
                            val right = if (spanGroupIndex == lastGroupIndex) {
                                child.right + space
                            } else {
                                child.right + space / 2
                            }
                            val top = child.bottom + space / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
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
                        if (direction != ONLY_VERTICAL && position != layoutManager.itemCount - 1) {
                            // 绘制横向分割线
                            val left = if (child.left < half) {
                                child.left - params.marginStart - space
                            } else {
                                child.left - params.marginStart - space / 2 - divider / 2
                            }
                            val right = if (child.right > half * (spanCount * 2 - 1)) {
                                child.right + params.marginEnd + space
                            } else {
                                child.right + params.marginEnd + space / 2 + divider / 2
                            }
                            val top = child.bottom + params.bottomMargin + space / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                        // 不是第一列
                        if (direction != ONLY_HORIZONTAL && child.left > parent.width / spanCount / 2) {
                            // 绘制竖向分割线
                            val left = child.left - params.marginStart - space / 2 - divider / 2
                            val right = left + divider
                            val top = if (position < spanCount) {
                                child.top - params.topMargin - space
                            } else {
                                child.top - params.topMargin - space / 2
                            }
                            val bottom = if (position == layoutManager.itemCount - 1) {
                                child.bottom + params.bottomMargin + space
                            } else {
                                child.bottom + params.bottomMargin + space / 2
                            }
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                        if (direction != ONLY_HORIZONTAL && (position == (layoutManager.itemCount - 1)
                                    || position == (layoutManager.itemCount - 2)
                                    || position == (layoutManager.itemCount - 3))
                            && child.right < (parent.width - parent.width / spanCount / 2)
                        ) {
                            // 绘制竖向分割线
                            val left = child.right + params.marginEnd + space / 2 - divider / 2
                            val right = left + divider
                            val top = child.top - params.topMargin - space / 2
                            val bottom = if (position == layoutManager.itemCount - 1) {
                                child.bottom + params.bottomMargin + space
                            } else {
                                child.bottom + params.bottomMargin + space / 2
                            }
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                    }
                } else {
                    for (i in 0 until childCount) {
                        val child = parent.getChildAt(i)
                        val position = parent.getChildAdapterPosition(child)
                        val params = child.layoutParams as RecyclerView.LayoutParams
                        // 如果不是最后一个，则在右边绘制分割线
                        if (direction != ONLY_HORIZONTAL && position != layoutManager.itemCount - 1) {
                            // 绘制纵向分割线
                            val left = child.right + params.marginEnd + space / 2 - divider / 2
                            val right = left + divider
                            val top = if (child.top < half) {
                                child.top - params.topMargin - space
                            } else {
                                child.top - params.topMargin - space / 2 - divider / 2
                            }
                            val bottom = if (child.bottom > half * (spanCount * 2 - 1)) {
                                child.bottom + params.bottomMargin + space
                            } else {
                                child.bottom + params.bottomMargin + space / 2 + divider / 2
                            }
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                        // 不是最后一行
                        if (direction != ONLY_VERTICAL && child.bottom < parent.height - parent.height / spanCount / 2) {
                            // 绘制横向分割线
                            val left = if (position < spanCount) {
                                child.left - params.marginStart - space
                            } else {
                                child.left - params.marginStart - space / 2 - divider / 2
                            }
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + space
                            } else {
                                child.right + params.marginEnd + space / 2 + divider / 2
                            }
                            val top = child.bottom + params.bottomMargin + space / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                        if (direction != ONLY_VERTICAL && (position == (layoutManager.itemCount - 1)
                                    || position == (layoutManager.itemCount - 2)
                                    || position == (layoutManager.itemCount - 3))
                            && child.top > parent.height / spanCount / 2
                        ) {
                            // 绘制横向分割线
                            val left = child.left - params.marginStart - space / 2 - divider / 2
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + space
                            } else {
                                child.right + params.marginEnd + space / 2 + divider / 2
                            }
                            val top = child.top - params.topMargin - space / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val childCount = parent.childCount
                if (layoutManager.orientation == GridLayoutManager.VERTICAL) {
                    if (direction != ONLY_VERTICAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = 0 + mDividerPadding
                            val right = parent.width - mDividerPadding
                            val top = child.bottom + params.bottomMargin + space / 2 - divider / 2
                            val bottom = top + divider
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                    }
                } else {
                    if (direction != ONLY_HORIZONTAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = child.right + params.rightMargin + space / 2 - divider / 2
                            val right = left + divider
                            val top = 0 + mDividerPadding
                            val bottom = parent.height - mDividerPadding
                            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                            drawable.draw(c)
                        }
                    }
                }
            }
        }
    }
}
