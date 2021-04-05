package com.liabit.recyclerview.nested

import android.database.DataSetObserver
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.liabit.recyclerview.R

internal class TabPageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val mTabLayout: TabLayout = itemView.findViewById(R.id.tab)
    private val mViewPager: ViewPager = itemView.findViewById(R.id.viewPager)

    private var mPagerAdapter: PagerAdapter? = null

    fun setAdapter(pagerAdapter: PagerAdapter?) {
        if (mPagerAdapter != pagerAdapter) {
            mPagerAdapter = pagerAdapter
            mViewPager.offscreenPageLimit = 3
            mViewPager.adapter = if (pagerAdapter != null) ProxyPagerAdapter(pagerAdapter) else null
            mTabLayout.setupWithViewPager(mViewPager)
        }
    }

    fun getCurrentNestedChildRecyclerView(): RecyclerView? {
        val obj = (mViewPager.adapter as? ProxyPagerAdapter)?.getPrimaryItem()
        if (obj is Fragment) {
            return findNestedRecyclerView(obj.view)
        } else if (obj is View) {
            return findNestedRecyclerView(obj)
        }
        return null
    }

    private fun findNestedRecyclerView(view: View?): RecyclerView? {
        if (view is RecyclerView) {
            return view
        } else if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val result = findNestedRecyclerView(view.getChildAt(i))
                if (result is RecyclerView) {
                    return result
                }
            }
        }
        return null
    }

    @Suppress("DEPRECATION")
    private class ProxyPagerAdapter(private val viewPagerAdapter: PagerAdapter) : PagerAdapter() {
        private var mPrimaryItem: Any? = null

        override fun getCount(): Int {
            return viewPagerAdapter.count
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return viewPagerAdapter.isViewFromObject(view, obj)
        }

        override fun startUpdate(container: ViewGroup) {
            viewPagerAdapter.startUpdate(container)
        }

        override fun startUpdate(container: View) {
            viewPagerAdapter.startUpdate(container)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            return viewPagerAdapter.instantiateItem(container, position)
        }

        override fun instantiateItem(container: View, position: Int): Any {
            return viewPagerAdapter.instantiateItem(container, position)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            viewPagerAdapter.destroyItem(container, position, `object`)
        }

        override fun destroyItem(container: View, position: Int, `object`: Any) {
            viewPagerAdapter.destroyItem(container, position, `object`)
        }

        override fun finishUpdate(container: ViewGroup) {
            viewPagerAdapter.finishUpdate(container)
        }

        override fun finishUpdate(container: View) {
            viewPagerAdapter.finishUpdate(container)
        }

        override fun saveState(): Parcelable? {
            return viewPagerAdapter.saveState()
        }

        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
            viewPagerAdapter.restoreState(state, loader)
        }

        override fun getItemPosition(`object`: Any): Int {
            return viewPagerAdapter.getItemPosition(`object`)
        }

        override fun notifyDataSetChanged() {
            viewPagerAdapter.notifyDataSetChanged()
        }

        override fun registerDataSetObserver(observer: DataSetObserver) {
            viewPagerAdapter.registerDataSetObserver(observer)
        }

        override fun unregisterDataSetObserver(observer: DataSetObserver) {
            viewPagerAdapter.unregisterDataSetObserver(observer)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return viewPagerAdapter.getPageTitle(position)
        }

        override fun getPageWidth(position: Int): Float {
            return viewPagerAdapter.getPageWidth(position)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, obj: Any) {
            viewPagerAdapter.setPrimaryItem(container, position, obj)
            mPrimaryItem = obj
        }

        override fun setPrimaryItem(container: View, position: Int, obj: Any) {
            viewPagerAdapter.setPrimaryItem(container, position, obj)
            mPrimaryItem = obj
        }

        fun getPrimaryItem(): Any? {
            return mPrimaryItem
        }
    }
}
