package cn.lolii.test.gesture

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.LayoutTransition
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.core.math.MathUtils
import cn.lolii.gesture.ViewDragHelper


class TestDragLayout : LinearLayout {

    private lateinit var mViewDragHelper: ViewDragHelper

    private var mViewDragHelperCallback = object : ViewDragHelper.Callback() {
        private var mScrollPercent: Float = 0f
        private var mScrollThreshold: Float = 0.5f

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return MathUtils.clamp(left, -child.width, child.width)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return child.top
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width
            var curLeft = releasedChild.left
            var top = releasedChild.top
            var left = if (curLeft > 0f) {
                if (xvel > 4000f || (xvel == 0f && mScrollPercent > mScrollThreshold)) childWidth else 0
            } else {
                if (xvel < -4000f || (xvel == 0f && mScrollPercent > mScrollThreshold)) -childWidth else 0
            }
            mViewDragHelper.settleCapturedViewAt(left, top)
            invalidate()
        }


        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            mScrollPercent = Math.abs(left.toFloat() / changedView.width)
            invalidate()
        }

        override fun onSettlingComplete(child: View, left: Int, top: Int) {
            if (left == -child.width || left == child.width) {
                removeView(child)
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 10
        }

    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
            context, attrs, defStyleAttr, defStyleRes
    ) {
        init(context)
    }

    private fun init(context: Context) {
        orientation = VERTICAL
        mViewDragHelper = ViewDragHelper.create(this, mViewDragHelperCallback)

        val transition = LayoutTransition()
        transition.setAnimator(LayoutTransition.DISAPPEARING, getAppearingAnimation())
        layoutTransition = transition
    }

    override fun computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            invalidate()
        }
    }

    private fun disableViewPager(disable: Boolean) {
        var parent = parent
        while (parent != null) {
            if (parent is CustomViewPager) {
                parent.scrollable = !disable
                break
            }
            parent = parent.parent
            Log.d("TTTT", "disableViewPager = $parent")
        }
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun getAppearingAnimation(): Animator {
        val mSet = AnimatorSet()
        mSet.playTogether(
                ObjectAnimator.ofFloat(null, "scaleX", 1.0f, 0f),
                ObjectAnimator.ofFloat(null, "scaleY", 1.0f, 0f),
                ObjectAnimator.ofFloat(null, "alpha", 1.0f, 0.0f)
        )
        return mSet
    }

    private var mIntercepted = false

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        if (ev.action == MotionEvent.ACTION_DOWN) {
            disableViewPager(true)
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP) {
            disableViewPager(false)
        }
        mIntercepted = mViewDragHelper.shouldInterceptTouchEvent(ev)
        Log.d("TTTT", "TestDragLayout onInterceptTouchEvent==?$mIntercepted  action=${ev.action}")
        return mIntercepted
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        if (event.action == MotionEvent.ACTION_DOWN) {
            disableViewPager(true)
        } else if (event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP) {
            disableViewPager(false)
        }
        val handled = mViewDragHelper.processTouchEvent(event)
        Log.d("TTTT", "TestDragLayout onTouchEvent==$handled")
        return if (mIntercepted) true else super.onTouchEvent(event)
    }


}