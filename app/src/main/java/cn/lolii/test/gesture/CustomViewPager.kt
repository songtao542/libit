package cn.lolii.test.gesture

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * CustomViewPager
 */
open class CustomViewPager : ViewPager {

    var scrollable = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TTTT", "ViewPager onTouchEvent")
        if (!scrollable) return false
        try {
            val handled = super.onTouchEvent(ev)
            Log.d("TTTT", "ViewPager onTouchEvent == $handled")
            return handled
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TTTT", "ViewPager onInterceptTouchEvent")
        if (!scrollable) return false
        try {
            val handled = super.onInterceptTouchEvent(ev)
            Log.d("TTTT", "ViewPager onInterceptTouchEvent == $handled")
            return handled
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return false
    }
}
