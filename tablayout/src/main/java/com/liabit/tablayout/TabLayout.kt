package com.liabit.tablayout

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import kotlin.math.min

class TabLayout : FrameLayout, OnPageChangeListener, ViewPager.OnAdapterChangeListener {

    companion object {
        const val SCROLLABLE = 0
        const val FIX = 1
    }

    var viewPager: ViewPager? = null

    private var mTabAdapter: TabAdapter = DefaultTabAdapter()
    private var mTabIndicator: TabIndicator? = null
    private var mScrollView: HorizontalScrollView? = null
    private lateinit var mTabContainer: LinearLayout
    private lateinit var mIndicatorContainer: LinearLayout
    private var mTabMode = FIX

    @TabIndicator.Mode
    var indicatorMode = TabIndicator.MATCH_TAB_WIDTH

    private var mStartInterpolator: Interpolator? = null
    private var mEndInterpolator: Interpolator? = null

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
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TabLayout, defStyleAttr, defStyleRes)
            mTabMode = a.getInt(R.styleable.TabLayout_tabMode, FIX)
            indicatorMode = a.getInt(R.styleable.TabLayout_indicatorMode, TabIndicator.MATCH_TAB_WIDTH)
            a.recycle()
        }
        val scrollable = mTabMode == SCROLLABLE
        mTabContainer = LinearLayout(getContext())
        val root: FrameLayout
        if (scrollable) {
            mScrollView = HorizontalScrollView(getContext()).apply {
                layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                isHorizontalScrollBarEnabled = false
                isVerticalScrollBarEnabled = false
            }
            root = FrameLayout(context)
            root.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mScrollView?.addView(root)
            addView(mScrollView)
            mTabContainer.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        } else {
            root = this
            mTabContainer.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        mTabContainer.gravity = Gravity.CENTER
        mTabContainer.orientation = LinearLayout.HORIZONTAL
        root.addView(mTabContainer)
        mIndicatorContainer = LinearLayout(getContext())
        mIndicatorContainer.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mIndicatorContainer.gravity = Gravity.CENTER
        mIndicatorContainer.orientation = LinearLayout.HORIZONTAL
        root.addView(mIndicatorContainer)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        mTabIndicator?.onPageScrolled(this, position, positionOffset)
        val tabCount = mTabContainer.childCount
        if (tabCount > 0) {
            val tabLeft = mTabContainer.getChildAt(position) as TabView
            tabLeft.onPageScrolled(positionOffset)
            if (position + 1 < tabCount) {
                val tabRight = mTabContainer.getChildAt(position + 1) as TabView
                tabRight.onPageScrolled(1f - positionOffset)
            }
            mScrollView?.let {
                if (position in 0 until tabCount) {
                    val currentPosition = min(tabCount - 1, position)
                    val nextPosition = min(tabCount - 1, position + 1)
                    val current = getTabViewAt(currentPosition)
                    val next = getTabViewAt(nextPosition)
                    if (current != null && next != null) {
                        val scrollTo = current.getView().left + current.getView().width / 2f - it.width * 0.5f
                        val nextScrollTo = next.getView().left + next.getView().width / 2f - it.width * 0.5f
                        it.scrollTo((scrollTo + (nextScrollTo - scrollTo) * positionOffset).toInt(), 0)
                    }
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {
        mTabIndicator?.onPageSelected(this, position)
        val count = mTabContainer.childCount
        for (i in 0 until count) {
            val tab = mTabContainer.getChildAt(i)
            tab.isSelected = i == position
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    fun setupWith(viewPager: ViewPager) {
        if (this.viewPager === viewPager) {
            return
        }
        this.viewPager = viewPager
        viewPager.addOnAdapterChangeListener(this)
        viewPager.addOnPageChangeListener(this)
        initTabAndIndicator()
    }

    override fun onAdapterChanged(viewPager: ViewPager, oldAdapter: PagerAdapter?, newAdapter: PagerAdapter?) {
        initTabAndIndicator()
    }

    private fun initTabAndIndicator() {
        val viewPager = viewPager ?: return
        val viewPagerAdapter = viewPager.adapter ?: return
        val count = viewPagerAdapter.count
        for (i in 0 until count) {
            val title = viewPagerAdapter.getPageTitle(i)
            val tab = tabAdapter.onCreateTabView(context, i, title)
            if (tab is View) {
                val view = tab as View
                var lp: LinearLayout.LayoutParams
                if (mTabMode == FIX) {
                    lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                    lp.weight = tabAdapter.getTabWeight(context, i)
                } else {
                    lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    val minWidth = tabAdapter.getTabMinWidth(context, i)
                    if (minWidth > 0) {
                        view.minimumWidth = minWidth
                    }
                }
                (tab as View).setOnClickListener { viewPager.currentItem = i }
                mTabContainer.addView(view, lp)
            }
        }
        mTabIndicator = tabAdapter.onCreateTabIndicator(context, count)
        if (mTabIndicator is View) {
            val lp = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mIndicatorContainer.addView(mTabIndicator as View, lp)
            if (mTabIndicator?.isFront() != true) {
                mTabContainer.bringToFront()
            }
        }
        onPageSelected(viewPager.currentItem)
        mTabIndicator?.onTabCreated(this, viewPager.currentItem)
    }

    var tabAdapter: TabAdapter
        get() {
            return mTabAdapter
        }
        set(adapter) {
            mTabAdapter = adapter
        }

    fun getTabViewAt(position: Int): TabView? {
        return mTabContainer.getChildAt(position) as? TabView
    }

    fun getInterpolator(type: Int): Interpolator {
        return if (type == TabIndicator.INTERPOLATOR_START) {
            if (mStartInterpolator == null) {
                mStartInterpolator = tabAdapter.onCreateInterpolator(type)
            }
            mStartInterpolator ?: LinearInterpolator()
        } else {
            if (mEndInterpolator == null) {
                mEndInterpolator = tabAdapter.onCreateInterpolator(type)
            }
            mEndInterpolator ?: LinearInterpolator()
        }
    }

}