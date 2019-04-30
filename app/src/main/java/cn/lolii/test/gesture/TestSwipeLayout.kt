package cn.lolii.test.gesture

import android.animation.LayoutTransition
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.animation.Animator
import android.annotation.SuppressLint
import android.util.Log
import cn.lolii.gesture.SwipeHelper


class TestSwipeLayout : LinearLayout, SwipeHelper.Callback {
    override fun shouldRemoveChild(child: View?): Boolean {
        return true
    }

    override fun getChildAtPosition(ev: MotionEvent?): View? {
        if (ev == null) return null
        val touchX = ev.x
        val touchY = ev.y
        val count = childCount
        for (childIdx in 0 until count) {
            val child = getChildAt(childIdx)
            if (child.visibility !== View.VISIBLE) {
                continue
            }

            val left = child.left
            val right = child.right
            val top = child.top
            val bottom = child.bottom

            if (touchY >= top && touchY <= bottom && touchX >= left && touchX <= right) {
                return child
            }
        }
        return null
    }

    override fun canChildBeDismissed(v: View?): Boolean {
        return true
    }

    override fun isAntiFalsingNeeded(): Boolean {
        return true
    }

    override fun onBeginDrag(v: View?) {
    }

    override fun onChildDismissed(v: View?) {
        removeView(v)
    }

    override fun onDragCancelled(v: View?) {
    }

    override fun onChildSnappedBack(animView: View?, targetLeft: Float) {
    }

    override fun updateSwipeProgress(animView: View?, dismissable: Boolean, swipeProgress: Float): Boolean {
        return false
    }

    override fun getFalsingThresholdFactor(): Float {
        return 1f
    }

    override fun canChildBeDragged(animView: View?): Boolean {
        return true
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

    private lateinit var mSwipeHelper: SwipeHelper

    private fun init(context: Context) {
        mSwipeHelper = SwipeHelper(SwipeHelper.X, this, context)

        val transition = LayoutTransition()
        transition.setAnimator(LayoutTransition.DISAPPEARING, getAppearingAnimation())
        layoutTransition = transition
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val handled = super.dispatchTouchEvent(ev)
        Log.d("TTTT", "TestSwipeLayout dispatchTouchEvent===>$handled")
        return handled
    }


    private var mIntercepted = false


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        if (ev.action == MotionEvent.ACTION_DOWN) {
            disableViewPager(true)
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP) {
            disableViewPager(false)
        }
        mIntercepted = mSwipeHelper.onInterceptTouchEvent(ev)
        Log.d("TTTT", "TestSwipeLayout onInterceptTouchEvent===>$mIntercepted")
        return mIntercepted
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        if (ev.action == MotionEvent.ACTION_DOWN) {
            disableViewPager(true)
        } else if (ev.action == MotionEvent.ACTION_CANCEL || ev.action == MotionEvent.ACTION_UP) {
            disableViewPager(false)
        }
        val handled = mSwipeHelper.onTouchEvent(ev)
        Log.d("TTTT", "TestSwipeLayout onTouchEvent===>$handled")
        return handled
    }

}