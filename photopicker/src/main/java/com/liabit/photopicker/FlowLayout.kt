package com.liabit.photopicker

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.google.android.flexbox.*

class FlowLayout : FlexboxLayout {
    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private var mColumn: Int = 0
    private var mSpace: Int = 0

    /**
     * item是否为正方形
     */
    private var mSquare: Boolean = false

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) {
        alignContent = AlignContent.FLEX_START
        alignItems = AlignItems.FLEX_START
        flexWrap = FlexWrap.WRAP
        justifyContent = JustifyContent.FLEX_START

        mColumn = 4
        mSpace = dip(10)

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.FlowLayout, defStyleAttr, R.style.P_FlowLayout_Widget)
            mColumn = a.getInt(R.styleable.FlowLayout_column, 4)
            mSpace = a.getDimensionPixelSize(R.styleable.FlowLayout_space, dip(10))
            mSquare = a.getBoolean(R.styleable.FlowLayout_square, false)
            a.recycle()
        }
    }

    private fun dip(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        resizeChildren(MeasureSpec.getSize(widthMeasureSpec))
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        resizeChildren(width)
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun resizeChildren(width: Int) {
        forEachIndexed { index, child ->
            val childWidth = (width - paddingLeft - paddingRight - (mSpace * (mColumn - 1))) / mColumn
            val childHeight = if (mSquare) childWidth else MarginLayoutParams.WRAP_CONTENT
            val lp = child.layoutParams as? MarginLayoutParams ?: MarginLayoutParams(childWidth, childHeight)
            var shouldResize = false
            if (lp.width != childWidth) {
                shouldResize = true
                lp.width = childWidth
            }
            if (lp.height != childHeight) {
                shouldResize = true
                lp.height = childHeight
            }

            if ((index % mColumn) != mColumn - 1) {
                if (lp.rightMargin != mSpace) {
                    shouldResize = true
                    lp.rightMargin = mSpace
                }
            } else {
                if (lp.rightMargin != 0) {
                    shouldResize = true
                    lp.rightMargin = 0
                }
            }
            if (index > mColumn - 1) {//第一行不要设置topMargin
                if (lp.topMargin != mSpace) {
                    shouldResize = true
                    lp.topMargin = mSpace
                }
            } else {
                if (lp.topMargin != 0) {
                    shouldResize = true
                    lp.topMargin = 0
                }
            }
            if (shouldResize) {
                child.layoutParams = lp
            }
        }
    }

    private var mOnItemClickListener: ((view: View, index: Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (view: View, index: Int) -> Unit) {
        mOnItemClickListener = listener
    }

    private var mAdapter: ViewAdapter? = null

    fun setAdapter(adapter: ViewAdapter?) {
        mAdapter = adapter
        notifyAdapterSizeChanged()
    }

    fun notifyAdapterSizeChanged() {
        mAdapter?.also {
            forEach { view ->
                it.onRecycleView(view)
            }
            removeAllViews()
            for (index in 0 until it.getItemCount()) {
                val child = it.create(index)
                child.setTag(R.id.p_index_tag, index)
                if (!child.hasOnClickListeners()) {
                    child.setOnClickListener { view ->
                        mOnItemClickListener?.invoke(view, view.getTag(R.id.p_index_tag) as Int)
                    }
                }
                addView(child)
            }
        } ?: run {
            removeAllViews()
        }
    }

    interface ViewAdapter {
        fun create(index: Int): View
        fun getItemCount(): Int
        fun onRecycleView(view: View)
    }
}

