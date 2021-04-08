package com.liabit.listpicker.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.citypicker.R
import com.liabit.listpicker.model.HotItem
import com.liabit.listpicker.model.Item

class SectionItemDecoration<I : Item>(val context: Context) : RecyclerView.ItemDecoration() {
    private val mBgPaint: Paint
    private val mTextPaint: TextPaint
    private val mBounds: Rect
    private val mSectionHeight: Int
    private val mBgColor: Int
    private val mTextColor: Int
    private val mSectionPaddingStart: Float

    private var mVariableItem: I? = null
    private var mHotItem: HotItem<I>? = null
    private var mItems: List<I>? = null

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.cpSectionBackground, typedValue, true)
        mBgColor = ResourcesCompat.getColor(context.resources, typedValue.resourceId, context.theme)
        context.theme.resolveAttribute(R.attr.cpSectionHeight, typedValue, true)
        mSectionHeight = context.resources.getDimensionPixelSize(typedValue.resourceId)
        context.theme.resolveAttribute(R.attr.cpSectionTextSize, typedValue, true)
        val textSize = context.resources.getDimension(typedValue.resourceId)
        context.theme.resolveAttribute(R.attr.cpSectionTextColor, typedValue, true)
        mTextColor = ResourcesCompat.getColor(context.resources, typedValue.resourceId, context.theme)
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint.color = mBgColor
        mTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textSize = textSize
        mTextPaint.color = mTextColor
        mSectionPaddingStart = context.resources.getDimension(R.dimen.cp_content_padding_start)
        mBounds = Rect()
    }

    fun setItem(variableItem: I?, hotItem: HotItem<I>?, items: List<I>?) {
        mVariableItem = variableItem
        mHotItem = hotItem
        mItems = items
    }

    private fun getItem(position: Int): Item? {
        val variableItem = mVariableItem
        var offset = 0
        if (variableItem != null) {
            if (position == 0) {
                return variableItem
            }
            offset += 1
        }
        val hotItem = mHotItem
        if (hotItem != null) {
            if (variableItem == null && position == 0) {
                return hotItem
            } else if (variableItem != null && position == 1) {
                return hotItem
            }
            offset += 1
        }
        val items = mItems
        if (items != null) {
            if (position < offset + items.size) {
                return items[position - offset]
            }
        }
        return null
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(canvas, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewLayoutPosition
            val data = getItem(position)
            if (data != null && position > -1) {
                if (position == 0) {
                    drawSection(canvas, left, right, child, params, data)
                } else {
                    val prevData = getItem(position - 1)
                    if (data.getItemSection() != prevData?.getItemSection()) {
                        drawSection(canvas, left, right, child, params, data)
                    }
                }
            }
        }
    }

    private fun drawSection(c: Canvas, left: Int, right: Int, child: View, params: RecyclerView.LayoutParams, data: Item) {
        c.drawRect(left.toFloat(), child.top.toFloat() - params.topMargin - mSectionHeight,
                right.toFloat(), child.top.toFloat() - params.topMargin, mBgPaint)
        mTextPaint.getTextBounds(data.getItemSection(), 0, data.getItemSection().length, mBounds)
        c.drawText(data.getItemSection(), mSectionPaddingStart, child.top.toFloat() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2), mTextPaint)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val position = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (position < 0) return
        val data = getItem(position) ?: return
        val section = data.getItemSection()
        val child = parent.findViewHolderForLayoutPosition(position)?.itemView
        var flag = false
        val nextData = getItem(position + 1)
        if (child != null && section != nextData?.getItemSection() && child.height + child.top < mSectionHeight) {
            c.save()
            flag = true
            c.translate(0f, child.height.toFloat() + child.top - mSectionHeight)
        }
        c.drawRect(parent.paddingLeft.toFloat(),
                parent.paddingTop.toFloat(), parent.right.toFloat() - parent.paddingRight.toFloat(),
                parent.paddingTop.toFloat() + mSectionHeight, mBgPaint)
        mTextPaint.getTextBounds(section, 0, section.length, mBounds)
        c.drawText(section, mSectionPaddingStart, parent.paddingTop.toFloat() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2), mTextPaint)
        if (flag) c.restore()
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val data = getItem(position) ?: return
        if (position > -1) {
            if (position == 0) {
                outRect.set(0, mSectionHeight, 0, 0)
            } else {
                val prevData = getItem(position - 1)
                if (data.getItemSection() != prevData?.getItemSection()) {
                    outRect.set(0, mSectionHeight, 0, 0)
                }
            }
        }
    }
}
