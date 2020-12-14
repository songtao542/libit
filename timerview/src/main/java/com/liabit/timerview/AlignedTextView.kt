package com.liabit.timerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatTextView

internal class AlignedTextView : AppCompatTextView {
    private var alignment = TOP
    private val textRect = Rect()

    companion object {
        const val TOP = 0
        const val BOTTOM = 1
    }

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
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AlignedTextView, defStyleAttr, 0)
            val alignment = typedArray.getInt(R.styleable.AlignedTextView_alignment, TOP)
            if (alignment != 0) {
                setAlignment(alignment)
            } else {
                Log.e("AlignedTextView", "You did not set an alignment for an AlignedTextView. Default is top alignment.")
            }
            invalidate()
            typedArray.recycle()
        }
    }

    override fun setTextSize(unit: Int, size: Float) {
        super.setTextSize(unit, size)
        val width = paint.measureText("8", 0, 1).toInt()
        minimumWidth = width
        minimumHeight = width * 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight / 2)
    }

    private fun setAlignment(alignment: Int) {
        if (alignment == 0) {
            this.alignment = TOP
        } else {
            this.alignment = BOTTOM
        }
    }

    private var pp = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    override fun onDraw(c: Canvas?) {
        val canvas = c ?: return
        canvas.save()
        /*canvas.clipRect(0, 0, width, height)
        val text = text.toString()
        paint.getTextBounds(text, 0, text.length, textRect)
        val bottom = textRect.bottom
        textRect.offset(-textRect.left, -textRect.top)
        paint.textAlign = Paint.Align.CENTER
        val drawY = if (alignment == TOP) {
            (textRect.bottom - bottom).toFloat() - ((textRect.bottom - textRect.top) / 2)
        } else {
            top + height.toFloat() + ((textRect.bottom - textRect.top) / 2)
        }
        paint.color = this.currentTextColor
        canvas.drawText(text, width / 2f, drawY, paint)*/

        val text = text.toString()
        paint.getTextBounds(text, 0, text.length, textRect)
//        Log.d("TTTT", "textRect===>" + textRect + "  " + textRect.width() + "  " + textRect.height())
//        val y = if (alignment == TOP) {
//            (height - textRect.height()) / 4f
//        } else {
//            -(height - textRect.height()) / 4f
//        }
//        paint.textAlign = Paint.Align.CENTER
//        paint.color = this.currentTextColor
//        canvas.drawText(text, width / 2f, y, paint)
        Log.d("TTTT", "width: $width   height: $height   $text : $textRect")

        //canvas.drawLine(0f, 0f, width.toFloat(), 0f, pp)
        canvas.drawRect(textRect, pp)
        super.onDraw(canvas)

        canvas.restore()
    }
}
