package com.liabit.recyclerview.nested

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.liabit.recyclerview.R

class NestedLinearRecyclerView : RecyclerView {

    private var mScrollDY = 0

    private var mFixedHeight = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        resolveAttr(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        resolveAttr(context, attrs, defStyleAttr)
    }

    init {
        layoutManager = LinearLayoutManager(context)
    }

    private fun resolveAttr(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NestedLinearRecyclerView, defStyleAttr, 0)
            mFixedHeight = typedArray.getDimension(R.styleable.NestedLinearRecyclerView_fixedHeight, 0f)
            typedArray.recycle()
        }
    }

    private val mParentScrollConsumed = IntArray(2)

    private var mCurrentFling = 0

    private val mOverScroller = OverScroller(context) {
        val t = it - 1.0f
        t * t * t * t * t + 1.0f
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.actionMasked == MotionEvent.ACTION_DOWN) {
            if (!mOverScroller.isFinished) {
                mOverScroller.abortAnimation()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        mScrollDY += (t - oldt)
        super.onScrollChanged(l, t, oldl, oldt)
    }

    /**
     * NestedScrollingChild2 start
     */
    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        var consumedSelf = false
        if (type == ViewCompat.TYPE_TOUCH) {
            // up
            if (dy > 0 && !canScrollVertically(1)) {
                val target = fetchNestedChild()
                target?.apply {
                    this.scrollBy(0, dy)
                    consumed?.let {
                        it[1] = dy
                    }
                    consumedSelf = true
                }
            }
            // down
            if (dy < 0) {
                val target = fetchNestedChild()
                target?.apply {
                    if (this.canScrollVertically(-1)) {
                        this.scrollBy(0, dy)
                        consumed?.let {
                            it[1] = dy
                        }
                        consumedSelf = true
                    }
                }
            }
        }
        // Now let our nested parent consume the leftovers
        val parentScrollConsumed = mParentScrollConsumed
        val superConsumed = super.dispatchNestedPreScroll(dx, dy - (consumed?.get(1) ?: 0), parentScrollConsumed, offsetInWindow, type)
        consumed?.let {
            it[1] += parentScrollConsumed[1]
        }
        return consumedSelf || superConsumed
        //return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    /**
     * fling 回调是一次性的, 无法同时分发到两个View, 只能自己托管fling
     */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        fling(velocityY)
        return true
    }

    /**
     * NestedScrollingChild2 end
     * 利用 OverScroller 托管 fling
     */
    private fun fling(velocityY: Float) {
        mCurrentFling = 0
        mOverScroller.fling(0, 0, 0, velocityY.toInt(), 0, 0, Int.MIN_VALUE, Int.MAX_VALUE)
        invalidate()
    }

    private fun fetchNestedChild(): View? {
        return (adapter as? NestedLinearAdapter<*>)?.getCurrentNestedChildRecyclerView()
    }

    override fun computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            val current = mOverScroller.currY
            val dy = current - mCurrentFling
            mCurrentFling = current
            val target = fetchNestedChild()
            if (dy > 0) {
                if (canScrollVertically(1)) {
                    scrollBy(0, dy)
                } else {
                    if (target?.canScrollVertically(1) == true) {
                        target.scrollBy(0, dy)
                    } else {
                        if (!mOverScroller.isFinished) {
                            mOverScroller.abortAnimation()
                        }
                    }
                }
            }
            if (dy < 0) {
                if (target?.canScrollVertically(-1) == true) {
                    target.scrollBy(0, dy)
                } else {
                    if (canScrollVertically(-1)) {
                        scrollBy(0, dy)
                    } else {
                        if (!mOverScroller.isFinished) {
                            mOverScroller.abortAnimation()
                        }
                    }
                }
            }
            invalidate()
        }
        super.computeScroll()
    }

    /**
     * The two parameters <var>unit</var> are as in [TypedValue.TYPE_DIMENSION]
     *
     * @param unit The unit to convert from.
     */
    fun setFixedHeight(unit: Int, fixedHeight: Float) {
        mFixedHeight = TypedValue.applyDimension(unit, fixedHeight, resources.displayMetrics)
    }

    fun setFixedHeight(fixedHeight: Float) {
        mFixedHeight = fixedHeight
    }

    internal fun getFixedHeight(): Float {
        return mFixedHeight
    }

    fun <VH : ViewHolder> setAdapter(adapter: Adapter<VH>, pagerAdapter: PagerAdapter) {
        setAdapter(NestedLinearAdapter(adapter, pagerAdapter))
    }

    fun <VH : ViewHolder> setAdapter(adapter: Adapter<VH>, pagerAdapter: PagerAdapter, fixedViewAdapter: FixedViewAdapter) {
        setAdapter(NestedLinearAdapter(adapter, pagerAdapter, fixedViewAdapter))
    }
}