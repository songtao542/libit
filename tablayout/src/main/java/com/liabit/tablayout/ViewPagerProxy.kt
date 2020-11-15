package com.liabit.tablayout

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class ViewPagerProxy {

    private var mViewPager: ViewPager? = null
    private var mViewPager2: ViewPager2? = null
    private var mTitles: Array<out CharSequence>? = null

    constructor(viewPager: ViewPager) {
        mViewPager = viewPager
    }

    constructor(viewPager2: ViewPager2, titles: Array<out CharSequence>?) {
        mViewPager2 = viewPager2
        mTitles = titles
    }

    fun isSameWith(viewPager: ViewPager): Boolean {
        if (mViewPager === viewPager) {
            return true
        }
        return false
    }

    fun isSameWith(viewPager2: ViewPager2): Boolean {
        if (mViewPager2 === viewPager2) {
            return true
        }
        return false
    }

    var currentItem: Int
        get() {
            return mViewPager?.currentItem ?: mViewPager2?.currentItem ?: 0
        }
        set(value) {
            mViewPager?.currentItem = value
            mViewPager2?.currentItem = value
        }

    val itemCount: Int
        get() {
            return mViewPager?.adapter?.count ?: mViewPager2?.adapter?.itemCount ?: 0
        }

    fun getPageTitle(position: Int): CharSequence? {
        return mViewPager?.adapter?.getPageTitle(position) ?: mTitles?.get(position)
    }
}