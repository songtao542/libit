package com.liabit.decoration

import android.content.Intent
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
 * @param spaceDirection 在哪个方向上添加间距
 * @param divider 分割线宽度, 单位 px
 * @param dividerColor 分割线颜色
 */
@Suppress("unused")
class SpaceDecoration(
        private val space: Float,
        private val spaceDirection: Int,
        private val divider: Float,
        @ColorInt dividerColor: Int
) : RecyclerView.ItemDecoration() {

    private var mDrawable: Drawable? = null

    constructor(space: Float) : this(space, ALL, 0f, Color.TRANSPARENT)

    constructor(space: Float, spaceDirection: Int) : this(space, spaceDirection, 0f, Color.TRANSPARENT)

    constructor(space: Float, divider: Float) : this(space, ALL, divider, Color.GRAY)

    constructor(space: Float, spaceDirection: Int, divider: Float)
            : this(space, spaceDirection, divider, Color.GRAY)

    init {
        if (dividerColor != Color.TRANSPARENT) {
            mDrawable = ColorDrawable(dividerColor)
        }
    }

    companion object {
        /**
         * 只考虑横向分割线
         */
        const val ONLY_HORIZONTAL = 0x000001
        val i: Intent = Intent().apply {

        }

        /**
         * 只考虑纵向分割线
         */
        const val ONLY_VERTICAL = 0x000010

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

    fun setDividerColor(@ColorInt color: Int) {
        mDrawable = ColorDrawable(color)
    }

    fun setDrawable(drawable: Drawable) {
        mDrawable = drawable
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val dividerSize = divider.toInt()
        // 间距大小必须大于等于 分割线宽度
        val spaceSize = if (space < divider) dividerSize else space.toInt()
        when (val layoutManager = parent.layoutManager) {
            is GridLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val spanCount = layoutManager.spanCount
                val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, spanCount)
                val spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
                val spanGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(position, spanCount)
                val lastGroupIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(layoutManager.itemCount - 1, spanCount)
                if (layoutManager.orientation == RecyclerView.VERTICAL) {
                    if ((spaceDirection and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val ew = (spanCount - 1) * spaceSize / spanCount
                        val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = if (spanIndex + spanSize == spanCount) 0 else ew - left
                        val top = if (spanGroupIndex == 0) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (spanGroupIndex == lastGroupIndex) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        val top = if (spanGroupIndex == 0) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (spanGroupIndex == lastGroupIndex) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                        direction = direction and IGNORE_CROSS_AXIS_END.inv()
                        when (direction) {
                            ONLY_VERTICAL -> outRect.set(left, 0, right, 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                } else {
                    if ((spaceDirection and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val eh = (spanCount - 1) * spaceSize / spanCount
                        val left = if (spanGroupIndex == 0) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (spanGroupIndex == lastGroupIndex) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = if ((spanIndex + spanSize) == spanCount) 0 else eh - top
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanGroupIndex == 0) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (spanGroupIndex == lastGroupIndex) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if ((spanIndex + spanSize) == spanCount) spaceSize else spaceSize / 2
                        var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                        direction = direction and IGNORE_CROSS_AXIS_END.inv()
                        when (direction) {
                            ONLY_VERTICAL -> outRect.set(left, 0, right, 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top, 0, bottom)
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
                    if ((spaceDirection and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val ew = (spanCount - 1) * spaceSize / spanCount
                        val left = spanIndex % spanCount * (spaceSize - ew)
                        val right = ew - left
                        val top = if (position < spanCount) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (position == layoutManager.itemCount - 1) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val right = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        val top = if (position < spanCount) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val bottom = if (position == layoutManager.itemCount - 1) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                        direction = direction and IGNORE_CROSS_AXIS_END.inv()
                        when (direction) {
                            ONLY_VERTICAL -> outRect.set(left, 0, right, 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                } else {
                    if ((spaceDirection and IGNORE_MAIN_AXIS_EDGE) == IGNORE_MAIN_AXIS_EDGE) {
                        val eh = (spanCount - 1) * spaceSize / spanCount
                        val left = if (position < spanCount) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (position == layoutManager.itemCount - 1) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = spanIndex % spanCount * (spaceSize - eh)
                        val bottom = eh - top
                        outRect.set(left, top, right, bottom)
                    } else {
                        val left = if (position < spanCount) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val right = if (position == layoutManager.itemCount - 1) {
                            if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                        } else {
                            spaceSize / 2
                        }
                        val top = if (spanIndex == 0) spaceSize else spaceSize / 2
                        val bottom = if (spanIndex == spanCount - 1) spaceSize else spaceSize / 2
                        var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                        direction = direction and IGNORE_CROSS_AXIS_END.inv()
                        when (direction) {
                            ONLY_VERTICAL -> outRect.set(left, 0, right, 0)
                            ONLY_HORIZONTAL -> outRect.set(0, top, 0, bottom)
                            else -> outRect.set(left, top, right, bottom)
                        }
                    }
                }
            }
            is LinearLayoutManager -> {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
                    val top = if (position == 0) {
                        if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    val bottom = if (position == layoutManager.itemCount - 1) {
                        if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                    direction = direction and IGNORE_CROSS_AXIS_END.inv()
                    when (direction) {
                        ONLY_VERTICAL -> outRect.set(spaceSize, 0, spaceSize, 0)
                        ONLY_HORIZONTAL, IGNORE_MAIN_AXIS_EDGE -> outRect.set(0, top, 0, bottom)
                        else -> outRect.set(spaceSize, top, spaceSize, bottom)
                    }
                } else {
                    val left = if (position == 0) {
                        if ((spaceDirection and IGNORE_CROSS_AXIS_START) == IGNORE_CROSS_AXIS_START) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    val right = if (position == layoutManager.itemCount - 1) {
                        if ((spaceDirection and IGNORE_CROSS_AXIS_END) == IGNORE_CROSS_AXIS_END) 0 else spaceSize
                    } else {
                        spaceSize / 2
                    }
                    var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
                    direction = direction and IGNORE_CROSS_AXIS_END.inv()
                    when (direction) {
                        ONLY_VERTICAL, IGNORE_MAIN_AXIS_EDGE -> outRect.set(left, 0, right, 0)
                        ONLY_HORIZONTAL -> outRect.set(0, spaceSize, 0, spaceSize)
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
        val spaceSize = space.toInt()
        val dividerSize = divider.toInt()
        var direction = spaceDirection and IGNORE_CROSS_AXIS_START.inv()
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
                            val top = (child.top - spaceSize / 2) - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一列，在 child 右边绘制一条竖线，高度为 child.height + space
                        if (direction != ONLY_HORIZONTAL && spanIndex + spanSize != spanCount) {
                            // 绘制竖向分割线
                            val left = child.right + spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
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
                        if (direction != ONLY_HORIZONTAL && !isFirstColumn && spanIndex == 0) {
                            // 绘制竖向分割线
                            val left = child.left - spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
                            val top = 0
                            val bottom = parent.height
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一行，在 child 顶部绘制一条横线，宽度为 child.width + space
                        if (direction != ONLY_VERTICAL && spanIndex + spanSize != spanCount) {
                            // 绘制横向分割线
                            val left = if (!isFirstColumn) child.left - spaceSize / 2 else child.left
                            val right = if (spanGroupIndex == lastGroupIndex) {
                                child.right + spaceSize
                            } else {
                                child.right + spaceSize / 2
                            }
                            val top = child.bottom + spaceSize / 2 - dividerSize / 2
                            val bottom = top + dividerSize
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
                        if (direction != ONLY_VERTICAL && position != layoutManager.itemCount - 1) {
                            // 绘制横向分割线
                            val left = if (child.left < half) {
                                child.left - params.marginStart - spaceSize
                            } else {
                                child.left - params.marginStart - spaceSize / 2 - dividerSize / 2
                            }
                            val right = if (child.right > half * (spanCount * 2 - 1)) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + dividerSize / 2
                            }
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是第一列
                        if (direction != ONLY_HORIZONTAL && child.left > parent.width / spanCount / 2) {
                            // 绘制竖向分割线
                            val left = child.left - params.marginStart - spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
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
                        if (direction != ONLY_HORIZONTAL && (position == (layoutManager.itemCount - 1)
                                        || position == (layoutManager.itemCount - 2)
                                        || position == (layoutManager.itemCount - 3))
                                && child.right < (parent.width - parent.width / spanCount / 2)) {
                            // 绘制竖向分割线
                            val left = child.right + params.marginEnd + spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
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
                        if (direction != ONLY_HORIZONTAL && position != layoutManager.itemCount - 1) {
                            // 绘制纵向分割线
                            val left = child.right + params.marginEnd + spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
                            val top = if (child.top < half) {
                                child.top - params.topMargin - spaceSize
                            } else {
                                child.top - params.topMargin - spaceSize / 2 - dividerSize / 2
                            }
                            val bottom = if (child.bottom > half * (spanCount * 2 - 1)) {
                                child.bottom + params.bottomMargin + spaceSize
                            } else {
                                child.bottom + params.bottomMargin + spaceSize / 2 + dividerSize / 2
                            }
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        // 不是最后一行
                        if (direction != ONLY_VERTICAL && child.bottom < parent.height - parent.height / spanCount / 2) {
                            // 绘制横向分割线
                            val left = if (position < spanCount) {
                                child.left - params.marginStart - spaceSize
                            } else {
                                child.left - params.marginStart - spaceSize / 2 - dividerSize / 2
                            }
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + dividerSize / 2
                            }
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                        if (direction != ONLY_VERTICAL && (position == (layoutManager.itemCount - 1)
                                        || position == (layoutManager.itemCount - 2)
                                        || position == (layoutManager.itemCount - 3))
                                && child.top > parent.height / spanCount / 2) {
                            // 绘制横向分割线
                            val left = child.left - params.marginStart - spaceSize / 2 - dividerSize / 2
                            val right = if (position == layoutManager.itemCount - 1) {
                                child.right + params.marginEnd + spaceSize
                            } else {
                                child.right + params.marginEnd + spaceSize / 2 + dividerSize / 2
                            }
                            val top = child.top - params.topMargin - spaceSize / 2 - dividerSize / 2
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
                    if (direction != ONLY_VERTICAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = 0
                            val right = parent.width
                            val top = child.bottom + params.bottomMargin + spaceSize / 2 - dividerSize / 2
                            val bottom = top + dividerSize
                            drawable.setBounds(left, top, right, bottom)
                            drawable.draw(c)
                        }
                    }
                } else {
                    if (direction != ONLY_HORIZONTAL) {
                        for (i in 0 until childCount - 1) {
                            val child = parent.getChildAt(i)
                            val params = child.layoutParams as RecyclerView.LayoutParams
                            val left = child.right + params.rightMargin + spaceSize / 2 - dividerSize / 2
                            val right = left + dividerSize
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
