package com.liabit.listpicker.decoration

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {
    private val mDividerHeight: Float
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(com.liabit.citypicker.R.attr.cpSectionBackground, typedValue, true)
        mPaint.color = ResourcesCompat.getColor(context.resources, typedValue.resourceId, context.theme)
        mDividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f, context.resources.displayMetrics)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = mDividerHeight.toInt()
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount: Int = parent.childCount
        val left: Int = parent.paddingLeft
        val right: Int = parent.width - parent.paddingRight
        for (i in 0 until childCount - 1) {
            val view: View = parent.getChildAt(i)
            val top = view.bottom
            val bottom = view.bottom + mDividerHeight
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, mPaint)
        }
    }


}
