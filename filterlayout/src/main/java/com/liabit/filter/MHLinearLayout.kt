package com.liabit.filter

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

/**
 * Author:         songtao
 * CreateDate:     2020/12/22 19:06
 */
internal class MHLinearLayout : LinearLayout {

    private var mMaxHeight = 0

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    fun setMaxHeight(maxHeight: Int) {
        mMaxHeight = maxHeight
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val hMeasureSpec = if (mMaxHeight in 1 until height) {
            MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.getMode(heightMeasureSpec))
        } else {
            heightMeasureSpec
        }
        super.onMeasure(widthMeasureSpec, hMeasureSpec)
    }

}
