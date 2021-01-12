package com.liabit.widget.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import java.lang.IllegalStateException

/**
 * 在不改动 RecyclerView 原有 adapter 的情况下，使其拥有加载更多功能和自定义底部视图。
 */
@Suppress("unused")
class LoadMoreAdapter(val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_FOOTER = -2
        private const val TYPE_NO_MORE = -3
        private const val TYPE_LOAD_FAILED = -4

        @JvmStatic
        fun wrap(recyclerView: RecyclerView) {
            val adapter = recyclerView.adapter ?: throw IllegalStateException("RecyclerView has not adapter!")
            recyclerView.adapter = LoadMoreAdapter(adapter)
        }

        @JvmStatic
        fun attach(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
            recyclerView.adapter = LoadMoreAdapter(adapter)
        }

        /**
         * 取到最后的一个节点
         */
        private fun last(lastPositions: IntArray): Int {
            var last = lastPositions[0]
            for (value in lastPositions) {
                if (value > last) {
                    last = value
                }
            }
            return last
        }
    }

    var footerView: View? = null
    var noMoreView: View? = null
    var loadFailedView: View? = null

    private var mFooterResId = View.NO_ID
    private var mNoMoreResId = View.NO_ID
    private var mLoadFailedResId = View.NO_ID
    private var mRecyclerView: RecyclerView? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    private var mIsLoading = false
    private var mShouldRemove = false
    private var mShowNoMoreEnabled = false
    private var mIsLoadFailed = false

    private val mOnEnabledListener = object : OnEnabledListener {
        override fun notifyChanged() {
            mShouldRemove = true
            notifyFooterHolderChanged()
        }

        override fun notifyLoadFailed(isLoadFailed: Boolean) {
            mIsLoadFailed = isLoadFailed
            notifyFooterHolderChanged()
        }
    }

    private val loadMore = LoadMore(mOnEnabledListener)

    private val mObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if (mShouldRemove) {
                mShouldRemove = false
            }
            notifyDataSetChanged()
            mIsLoading = false
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            if (mShouldRemove && positionStart == adapter.itemCount) {
                mShouldRemove = false
            }
            this@LoadMoreAdapter.notifyItemRangeChanged(positionStart, itemCount)
            mIsLoading = false
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            if (mShouldRemove && positionStart == adapter.itemCount) {
                mShouldRemove = false
            }
            this@LoadMoreAdapter.notifyItemRangeChanged(positionStart, itemCount, payload)
            mIsLoading = false
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            // when no data is initialized (has loadMoreView)
            // should remove loadMoreView before notifyItemRangeInserted
            if ((mRecyclerView?.childCount ?: 0) == 1) {
                notifyItemRemoved(0)
            }
            notifyItemRangeInserted(positionStart, itemCount)
            notifyFooterHolderChanged()
            mIsLoading = false
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            if (mShouldRemove && positionStart == adapter.itemCount) {
                mShouldRemove = false
            }
            /*
               use notifyItemRangeRemoved after clear item, can throw IndexOutOfBoundsException
               @link RecyclerView#tryGetViewHolderForPositionByDeadline
               fix java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid item position
             */
            var shouldSync = false
            if (loadMore.isEnable && 0 == adapter.itemCount) {
                isLoadMoreEnabled = false
                shouldSync = true
                // when use onItemRangeInserted(0, count) after clear item
                // recyclerView will auto scroll to bottom, because has one item(loadMoreView)
                // remove loadMoreView
                if (getItemCount() == 1) {
                    notifyItemRemoved(0)
                }
            }
            notifyItemRangeRemoved(positionStart, itemCount)
            if (shouldSync) {
                isLoadMoreEnabled = true
            }
            mIsLoading = false
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            require(!(mShouldRemove && (fromPosition == adapter.itemCount || toPosition == adapter.itemCount))) {
                "can not move last position after setEnable(false)"
            }
            notifyItemMoved(fromPosition, toPosition)
            mIsLoading = false
        }
    }

    init {
        adapter.registerAdapterDataObserver(mObserver)
    }

    constructor(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, footerView: View) : this(adapter) {
        this.footerView = footerView
    }

    constructor(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>, @LayoutRes resId: Int) : this(adapter) {
        mFooterResId = resId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TYPE_FOOTER -> {
                val view = footerView ?: if (mFooterResId != 0) {
                    LayoutInflater.from(parent.context).inflate(mFooterResId, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_footer, parent, false)
                }
                footerView = view
                return FooterHolder(view)
            }
            TYPE_NO_MORE -> {
                val view = noMoreView ?: if (mNoMoreResId != 0) {
                    LayoutInflater.from(parent.context).inflate(mNoMoreResId, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_no_more, parent, false)
                }
                noMoreView = view
                return NoMoreHolder(view)
            }
            TYPE_LOAD_FAILED -> {
                val view = loadFailedView ?: if (mLoadFailedResId != 0) {
                    LayoutInflater.from(parent.context).inflate(mLoadFailedResId, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_load_failed, parent, false)
                }
                loadFailedView = view
                return LoadFailedHolder(view, loadMore, mOnLoadMoreListener)
            }
            else -> return adapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (holder is FooterHolder) {
            // 当 recyclerView 不能滚动的时候(item 不能铺满屏幕的时候也是不能滚动的) call loadMore
            if (!canScroll() && mOnLoadMoreListener != null && !mIsLoading) {
                mIsLoading = true
                // fix Cannot call this method while RecyclerView is computing a layout or scrolling
                mRecyclerView?.post {
                    mOnLoadMoreListener?.onLoadMore(loadMore)
                }
            }
        } else if (holder is NoMoreHolder || holder is LoadFailedHolder) {
            // ignore
        } else {
            adapter.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        val count = adapter.itemCount
        return when {
            isLoadMoreEnabled -> {
                count + 1
            }
            mShowNoMoreEnabled -> {
                count + 1
            }
            else -> {
                count + if (mShouldRemove) 1 else 0
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == adapter.itemCount && mIsLoadFailed) {
            return TYPE_LOAD_FAILED
        }
        if (position == adapter.itemCount && (isLoadMoreEnabled || mShouldRemove)) {
            return TYPE_FOOTER
        } else if (position == adapter.itemCount && mShowNoMoreEnabled && !isLoadMoreEnabled) {
            return TYPE_NO_MORE
        }
        return adapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        val itemViewType = getItemViewType(position)
        return if (adapter.hasStableIds()
                && itemViewType != TYPE_FOOTER
                && itemViewType != TYPE_LOAD_FAILED
                && itemViewType != TYPE_NO_MORE) {
            adapter.getItemId(position)
        } else {
            super.getItemId(position)
        }
    }

    private fun canScroll(): Boolean {
        return mRecyclerView?.canScrollVertically(-1) ?: true
    }

    fun setFooterView(@LayoutRes resId: Int) {
        mFooterResId = resId
    }

    fun setNoMoreView(@LayoutRes resId: Int) {
        mNoMoreResId = resId
    }

    fun setLoadFailedView(@LayoutRes resId: Int) {
        mLoadFailedResId = resId
    }

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    class NoMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    class LoadFailedHolder(itemView: View, enabled: LoadMore, listener: OnLoadMoreListener?)
        : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
            itemView.setOnClickListener {
                enabled.isLoadFailed = false
                listener?.onLoadMore(enabled)
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        recyclerView.addOnScrollListener(mOnScrollListener)

        // 当为 GridLayoutManager 的时候, 设置 footerView 占据整整一行.
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            // 获取原来的 SpanSizeLookup,当不为 null 的时候,除了 footerView 都应该返回原来的 spanSize
            val originalSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val itemViewType = getItemViewType(position)
                    if (itemViewType == TYPE_FOOTER || itemViewType == TYPE_NO_MORE || itemViewType == TYPE_LOAD_FAILED) {
                        return layoutManager.spanCount
                    } else if (originalSizeLookup != null) {
                        return originalSizeLookup.getSpanSize(position)
                    }
                    return 1
                }
            }
        }
    }

    /**
     * Deciding whether to trigger loading
     * 判断是否触发加载更多
     */
    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!isLoadMoreEnabled || mIsLoading) {
                return
            }
            if (newState == RecyclerView.SCROLL_STATE_IDLE && mOnLoadMoreListener != null) {
                val isBottom: Boolean
                val layoutManager = recyclerView.layoutManager
                isBottom = when (layoutManager) {
                    is LinearLayoutManager -> {
                        (layoutManager.findLastVisibleItemPosition() >= layoutManager.getItemCount() - 1)
                    }
                    is StaggeredGridLayoutManager -> {
                        val into = IntArray(layoutManager.spanCount)
                        layoutManager.findLastVisibleItemPositions(into)
                        last(into) >= layoutManager.getItemCount() - 1
                    }
                    is GridLayoutManager -> {
                        (layoutManager.findLastVisibleItemPosition() >= layoutManager.itemCount - 1)
                    }
                    else -> {
                        false
                    }
                }
                if (isBottom) {
                    mIsLoading = true
                    mOnLoadMoreListener?.onLoadMore(loadMore)
                }
            }
        }
    }

    /**
     * clean
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(mOnScrollListener)
        adapter.unregisterAdapterDataObserver(mObserver)
        mRecyclerView = null
    }

    fun setLoadMoreListener(listener: OnLoadMoreListener?) {
        mOnLoadMoreListener = listener
    }

    interface OnLoadMoreListener {
        fun onLoadMore(enabled: LoadMore?)
    }

    var isLoadMoreEnabled: Boolean
        get() = loadMore.isEnable && adapter.itemCount >= 0
        set(enabled) {
            loadMore.isEnable = enabled
        }

    interface OnEnabledListener {
        fun notifyChanged()
        fun notifyLoadFailed(isLoadFailed: Boolean)
    }

    fun setShouldRemove(shouldRemove: Boolean) {
        mShouldRemove = shouldRemove
    }

    fun setShowNoMoreEnabled(showNoMoreEnabled: Boolean) {
        mShowNoMoreEnabled = showNoMoreEnabled
    }

    fun setLoadFailed(isLoadFailed: Boolean) {
        loadMore.isLoadFailed = isLoadFailed
    }

    /**
     * 控制加载更多的开关, 作为 [的参数][OnLoadMoreListener.onLoadMore]
     */
    class LoadMore(private val mListener: OnEnabledListener) {
        private var mLoadMoreEnable = true
        private var mIsLoadFailed = false

        /**
         * 设置是否加载失败
         */
        var isLoadFailed: Boolean
            get() = mIsLoadFailed
            set(isLoadFailed) {
                if (mIsLoadFailed != isLoadFailed) {
                    mIsLoadFailed = isLoadFailed
                    mListener.notifyLoadFailed(isLoadFailed)
                    isEnable = !mIsLoadFailed
                }
            }

        /**
         * 获取是否启用了加载更多,默认是 true
         */
        var isEnable: Boolean
            get() = mLoadMoreEnable
            set(enable) {
                val canNotify = mLoadMoreEnable
                mLoadMoreEnable = enable
                if (canNotify && !mLoadMoreEnable) {
                    mListener.notifyChanged()
                }
            }
    }

    /**
     * update last item
     */
    private fun notifyFooterHolderChanged() {
        if (isLoadMoreEnabled) {
            this@LoadMoreAdapter.notifyItemChanged(adapter.itemCount)
        } else if (mShouldRemove) {
            mShouldRemove = false
            /*
              fix IndexOutOfBoundsException when setEnable(false) and then use onItemRangeInserted
              @see androidx.recyclerview.widget.Recycler#validateViewHolderForOffsetPosition(RecyclerView.ViewHolder)
             */
            val position = adapter.itemCount
            val viewHolder = mRecyclerView?.findViewHolderForAdapterPosition(position)
            if (viewHolder is FooterHolder) {
                notifyItemRemoved(position)
            } else {
                this@LoadMoreAdapter.notifyItemChanged(position)
            }
        }
    }
}