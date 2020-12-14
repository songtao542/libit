package com.liabit.timerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

internal class HalfTextView : AppCompatTextView {

    companion object {
        const val TOP = 0
    }

    private var showPart = TOP
    private val mPaint = Paint()
    private var mBackgroundColor = Color.WHITE

    constructor(context: Context) : super(context, null) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.HalfTextView, defStyleAttr, 0)
            showPart = typedArray.getInt(R.styleable.HalfTextView_showPart, TOP)
            typedArray.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = paint.measureText("8", 0, 1).toInt()
        setMeasuredDimension(width, measuredHeight)
    }

    override fun setBackgroundColor(color: Int) {
        mBackgroundColor = color
    }

    override fun onDraw(c: Canvas?) {
        val canvas = c ?: return
        canvas.save()
        mPaint.color = mBackgroundColor
        if (showPart == TOP) {
            canvas.clipRect(0f, 0f, width.toFloat(), height / 2f)
            canvas.drawRect(0f, 0f, width.toFloat(), height / 2f, mPaint)
        } else {
            canvas.clipRect(0f, height / 2f, width.toFloat(), height.toFloat())
            canvas.drawRect(0f, height / 2f, width.toFloat(), height.toFloat(), mPaint)
        }
        super.onDraw(canvas)
        canvas.restore()
    }
}
