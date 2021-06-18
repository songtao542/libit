package com.liabit.widget

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.WindowInsets

/**
 * 填充 StatusBar 区域
 */
class PaddingStatusBarView : View {

    private var mStatusBarHeight: Int = 0

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        mStatusBarHeight = getStatusBarHeight(context.resources)
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        mStatusBarHeight = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            insets?.getInsetsIgnoringVisibility(1)?.bottom ?: 0
        } else {
            insets?.stableInsetTop?.let { if (it > 0) it else null } ?: insets?.systemWindowInsetTop ?: 0
        }
        if (mStatusBarHeight == 0) {
            mStatusBarHeight = getStatusBarHeight(resources)
        }
        return super.onApplyWindowInsets(insets)
    }

    private fun getStatusBarHeight(resources: Resources): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return when {
            resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
            else -> TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24f, resources.displayMetrics).toInt()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, mStatusBarHeight)
    }
}