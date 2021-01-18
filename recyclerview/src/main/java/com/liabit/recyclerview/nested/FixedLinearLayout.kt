package com.liabit.recyclerview.nested

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

@Suppress("unused")
class FixedLinearLayout : LinearLayout {
    private var mFixedHeight = 0f

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = MeasureSpec.getSize(heightMeasureSpec)
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec((size - mFixedHeight).toInt(), mode)
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    fun setFixedHeight(fixedHeight: Float) {
        mFixedHeight = fixedHeight
    }
}
