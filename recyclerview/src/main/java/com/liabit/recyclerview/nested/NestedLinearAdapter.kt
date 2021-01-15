package com.liabit.recyclerview.nested

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.liabit.recyclerview.R

internal class NestedLinearAdapter<VH : RecyclerView.ViewHolder>() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mTopAdapter: RecyclerView.Adapter<VH> = EmptyAdapter()
    private var mPageAdapter: PagerAdapter? = null
    private var mPagerTabHolder: TabPageViewHolder? = null
    private var mFixedViewAdapter: FixedViewAdapter? = null

    constructor(adapter: RecyclerView.Adapter<VH>, pagerAdapter: PagerAdapter) : this() {
        setAdapter(adapter, pagerAdapter)
    }

    constructor(adapter: RecyclerView.Adapter<VH>, pagerAdapter: PagerAdapter, fixedViewAdapter: FixedViewAdapter)
            : this(adapter, pagerAdapter) {
        mFixedViewAdapter = fixedViewAdapter
    }

    override fun getItemCount(): Int {
        return mTopAdapter.itemCount + (if (mPageAdapter != null) 1 else 0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < mTopAdapter.itemCount) 0 else 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            mTopAdapter.onCreateViewHolder(parent, viewType)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.nested_tab_pager_layout,
                    parent, false) as FixedLinearLayout
            val inflater = LayoutInflater.from(parent.context)
            mFixedViewAdapter?.getView(inflater, view)?.let {
                view.addView(it, 0)
            }
            (parent as? NestedLinearRecyclerView)?.getFixedHeight()?.let {
                view.setFixedHeight(it)
            }
            TabPageViewHolder(view).also {
                mPagerTabHolder = it
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            @Suppress("UNCHECKED_CAST")
            mTopAdapter.onBindViewHolder(holder as VH, position)
        } else {
            (holder as TabPageViewHolder).setAdapter(mPageAdapter)
        }
    }

    fun getCurrentNestedChildRecyclerView(): RecyclerView? {
        return mPagerTabHolder?.getCurrentNestedChildRecyclerView()
    }

    fun getPagerTabView(): View? {
        return mPagerTabHolder?.itemView
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun setAdapter(adapter: RecyclerView.Adapter<VH>, pagerAdapter: PagerAdapter) {
        mTopAdapter = adapter
        mPageAdapter = pagerAdapter
    }

    private class EmptyAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
        override fun getItemCount(): Int = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            @Suppress("UNCHECKED_CAST")
            return object : RecyclerView.ViewHolder(View(parent.context)) {} as VH
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
        }
    }
}