package com.liabit.recyclerview.nested

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout

class FixedLinearLayout : LinearLayout {
    private var mFixedHeight = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((size - mFixedHeight).toInt(), mode)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    fun setFixedHeight(fixedHeight: Float) {
        mFixedHeight = fixedHeight
    }
}