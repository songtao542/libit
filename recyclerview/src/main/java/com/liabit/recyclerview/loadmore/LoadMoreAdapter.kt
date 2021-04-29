package com.liabit.recyclerview.loadmore

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liabit.recyclerview.R

/**
 * 在不改动 RecyclerView 原有 adapter 的情况下，使其拥有加载更多功能和自定义底部视图。
 */
@Suppress("unused")
class LoadMoreAdapter<VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>>(val adapter: A) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_LOADING = -234623
        private const val TYPE_NO_MORE = -345674
        private const val TYPE_LOAD_FAILED = -445678

        @JvmStatic
        fun <VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>> wrap(adapter: A): LoadMoreAdapter<VH, A> {
            return LoadMoreAdapter(adapter)
        }

        @JvmStatic
        fun <VH : RecyclerView.ViewHolder, A : RecyclerView.Adapter<VH>> attach(recyclerView: RecyclerView, adapter: A) {
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

    var loadingView: View? = null
    var noMoreView: View? = null
    var loadFailedView: View? = null

    private var mLoadingResId: Int? = null
    private var mNoMoreResId: Int? = null
    private var mLoadFailedResId: Int? = null

    private var mStyleResId: Int = R.style.DefaultLoadMoreStyle

    private var mLoadingText: String? = null
    private var mLoadFailedText: String? = null
    private var mLoadNoMoreText: String? = null

    private var mLoadingTextColor: ColorStateList? = null
    private var mLoadFailedTextColor: ColorStateList? = null
    private var mLoadNoMoreTextColor: ColorStateList? = null

    private var mLoadFailedIcon: Drawable? = null
    private var mLoadNoMoreIcon: Drawable? = null

    private var mLoadMoreProgressColor: ColorStateList? = null
    private var mLoadFailedIconColor: ColorStateList? = null
    private var mLoadNoMoreIconColor: ColorStateList? = null

    private var mLoadMoreTextSize: Float? = null
    private var mLoadFailedTextSize: Float? = null
    private var mLoadNoMoreTextSize: Float? = null

    private var mRecyclerView: RecyclerView? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    private var mIsLoading = false

    /**
     * 是否加载失败，用于显示加载失败视图
     */
    private var mLoadFailed = false

    private val mLoadMore = LoadMoreImpl(object : LoadMoreImpl.OnEnabledListener {
        override fun notifyChanged() {
            notifyFooterHolderChanged()
        }

        override fun notifyLoadFailed(isLoadFailed: Boolean) {
            mLoadFailed = isLoadFailed
            notifyFooterHolderChanged()
        }
    })

    private val mObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mIsLoading = false
            if (adapter.itemCount == 0) {
                notifyItemRemoved(0)
            }
            notifyItemMoved(fromPosition, toPosition)
        }
    }

    init {
        adapter.registerAdapterDataObserver(mObserver)
    }

    constructor(adapter: A, footerView: View) : this(adapter) {
        this.loadingView = footerView
    }

    constructor(adapter: A, @LayoutRes resId: Int) : this(adapter) {
        mLoadingResId = resId
    }

    /**
     *  <style name="DefaultLoadMoreStyle">
     *      <item name="loadMoreTextColor">@color/load_more_text_color</item>
     *      <item name="loadMoreText">@string/load_more_loading</item>
     *      <item name="loadMoreFailedTextColor">@color/load_more_text_color</item>
     *      <item name="loadMoreFailedText">@string/load_more_retry</item>
     *      <item name="loadMoreFailedIcon">@drawable/load_more_ic_reload</item>
     *      <item name="loadMoreFailedIconColor">@color/load_more_text_color</item>
     *      <item name="loadNoMoreTextColor">@color/load_more_text_color</item>
     *      <item name="loadNoMoreText">@string/load_more_no_more</item>
     *      <item name="loadNoMoreIcon">@null</item>
     *  </style>
     */
    fun setStyle(context: Context, styleResId: Int) {
        mStyleResId = styleResId

        val typedArray = context.obtainStyledAttributes(styleResId, R.styleable.LoadMoreStyle)
        mLoadingText = typedArray.getString(R.styleable.LoadMoreStyle_loadMoreText)
        mLoadFailedText = typedArray.getString(R.styleable.LoadMoreStyle_loadMoreFailedText)
        mLoadNoMoreText = typedArray.getString(R.styleable.LoadMoreStyle_loadNoMoreText)

        val defaultColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.load_more_text_color))
        mLoadingTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreTextColor) ?: defaultColor
        mLoadFailedTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreFailedTextColor) ?: defaultColor
        mLoadNoMoreTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadNoMoreTextColor) ?: defaultColor

        val defaultFailedIcon = ContextCompat.getDrawable(context, R.drawable.load_more_ic_reload)
        mLoadFailedIcon = typedArray.getDrawable(R.styleable.LoadMoreStyle_loadMoreFailedIcon) ?: defaultFailedIcon
        mLoadNoMoreIcon = typedArray.getDrawable(R.styleable.LoadMoreStyle_loadNoMoreIcon)

        mLoadMoreProgressColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreProgressColor) ?: defaultColor
        mLoadFailedIconColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreFailedIconColor) ?: defaultColor
        mLoadNoMoreIconColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadNoMoreIconColor) ?: defaultColor

        val defaultTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context.resources.displayMetrics)
        mLoadMoreTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadMoreTextSize, defaultTextSize)
        mLoadFailedTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadMoreFailedTextSize, defaultTextSize)
        mLoadNoMoreTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadNoMoreTextSize, defaultTextSize)

        loadingView?.findViewById<TextView>(R.id.loadMoreTextView)?.let {
            mLoadingText?.let { text ->
                it.text = text
            }
            mLoadingTextColor?.let { textColor ->
                it.setTextColor(textColor)
            }
            mLoadMoreTextSize?.let { textSize ->
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }
        loadFailedView?.findViewById<TextView>(R.id.loadMoreFailedTextView)?.let {
            mLoadFailedText?.let { text ->
                it.text = text
            }
            mLoadFailedTextColor?.let { textColor ->
                it.setTextColor(textColor)
            }
            mLoadFailedTextSize?.let { textSize ->
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }
        noMoreView?.findViewById<TextView>(R.id.loadMoreNoMoreTextView)?.let {
            mLoadNoMoreText?.let { text ->
                it.text = text
            }
            mLoadNoMoreTextColor?.let { textColor ->
                it.setTextColor(textColor)
            }
            mLoadNoMoreTextSize?.let { textSize ->
                it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }
        loadingView?.findViewById<ProgressBar>(R.id.loadMoreProgressBar)?.let {
            mLoadMoreProgressColor?.let { color ->
                it.indeterminateTintList = color
            }
        }
        loadFailedView?.findViewById<ImageView>(R.id.loadMoreFailedIcon)?.let {
            mLoadFailedIcon?.let { icon ->
                it.setImageDrawable(icon)
            }
            mLoadFailedIconColor?.let { iconColor ->
                it.imageTintList = iconColor
            }
        }
        noMoreView?.findViewById<ImageView>(R.id.loadMoreNoMoreIcon)?.let {
            mLoadNoMoreIcon?.let { icon ->
                it.setImageDrawable(icon)
            }
            mLoadNoMoreIconColor?.let { iconColor ->
                it.imageTintList = iconColor
            }
        }
        typedArray.recycle()
    }

    fun setLoadingText(loadingText: String) {
        mLoadingText = loadingText
        loadingView?.findViewById<TextView>(R.id.loadMoreTextView)?.text = mLoadingText
    }

    fun setLoadFailedText(loadFailedText: String) {
        mLoadFailedText = loadFailedText
        loadFailedView?.findViewById<TextView>(R.id.loadMoreFailedTextView)?.text = mLoadFailedText
    }

    fun setLoadNoMoreText(loadNoMoreText: String) {
        mLoadNoMoreText = loadNoMoreText
        noMoreView?.findViewById<TextView>(R.id.loadMoreNoMoreTextView)?.text = mLoadNoMoreText
    }

    private fun removeFromParent(view: View) {
        (view.parent as? ViewGroup)?.removeView(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        setStyle(parent.context, mStyleResId)
        val inflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_LOADING -> {
                val view = loadingView?.also { removeFromParent(it) } ?: mLoadingResId?.let {
                    inflater.inflate(it, parent, false)
                } ?: kotlin.run {
                    inflater.inflate(R.layout.load_more_base_footer, parent, false)
                }
                view?.findViewById<TextView>(R.id.loadMoreTextView)?.let {
                    mLoadingText?.let { text ->
                        it.text = text
                    }
                    mLoadingTextColor?.let { textColor ->
                        it.setTextColor(textColor)
                    }
                    mLoadMoreTextSize?.let { textSize ->
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    }
                }
                view?.findViewById<ProgressBar>(R.id.loadMoreProgressBar)?.let {
                    mLoadMoreProgressColor?.let { color ->
                        it.indeterminateTintList = color
                    }
                }
                loadingView = view
                return LoadingHolder(view)
            }
            TYPE_NO_MORE -> {
                val view = noMoreView?.also { removeFromParent(it) } ?: mNoMoreResId?.let {
                    inflater.inflate(it, parent, false)
                } ?: kotlin.run {
                    inflater.inflate(R.layout.load_more_base_no_more, parent, false)
                }
                view?.findViewById<TextView>(R.id.loadMoreNoMoreTextView)?.let {
                    mLoadNoMoreText?.let { text ->
                        it.text = text
                    }
                    mLoadNoMoreTextColor?.let { textColor ->
                        it.setTextColor(textColor)
                    }
                    mLoadNoMoreTextSize?.let { textSize ->
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    }
                }
                view?.findViewById<ImageView>(R.id.loadMoreNoMoreIcon)?.let {
                    mLoadNoMoreIcon?.let { icon ->
                        it.setImageDrawable(icon)
                    }
                    mLoadNoMoreIconColor?.let { iconColor ->
                        it.imageTintList = iconColor
                    }
                }
                noMoreView = view
                return NoMoreHolder(view)
            }
            TYPE_LOAD_FAILED -> {
                val view = loadFailedView?.also { removeFromParent(it) } ?: mLoadFailedResId?.let {
                    inflater.inflate(it, parent, false)
                } ?: kotlin.run {
                    inflater.inflate(R.layout.load_more_base_load_failed, parent, false)
                }
                view?.findViewById<TextView>(R.id.loadMoreFailedTextView)?.let {
                    mLoadFailedText?.let { text ->
                        it.text = text
                    }
                    mLoadFailedTextColor?.let { textColor ->
                        it.setTextColor(textColor)
                    }
                    mLoadNoMoreTextSize?.let { textSize ->
                        it.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
                    }
                }
                view?.findViewById<ImageView>(R.id.loadMoreFailedIcon)?.let {
                    mLoadFailedIcon?.let { icon ->
                        it.setImageDrawable(icon)
                    }
                    mLoadFailedIconColor?.let { iconColor ->
                        it.imageTintList = iconColor
                    }
                }
                loadFailedView = view
                return LoadFailedHolder(view, this, mOnLoadMoreListener)
            }
            else -> return adapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (holder is LoadingHolder) {
            // 当 recyclerView 不能滚动的时候(item 不能铺满屏幕的时候也是不能滚动的) call loadMore
            if (!canScroll() && mOnLoadMoreListener != null && !mIsLoading) {
                mIsLoading = true
                // fix Cannot call this method while RecyclerView is computing a layout or scrolling
                mRecyclerView?.post {
                    mOnLoadMoreListener?.onLoadMore(mLoadMore)
                }
            }
        } else if (holder is NoMoreHolder || holder is LoadFailedHolder) {
            // ignore
        } else {
            @Suppress("UNCHECKED_CAST")
            adapter.onBindViewHolder(holder as VH, position, payloads)
        }
    }

    override fun getItemCount(): Int {
        val count = adapter.itemCount
        return if (count > 0) {
            when {
                // 如果当前状态是 isLoadMoreEnabled == true, 则说明需要显示加载更多视图
                // 加载失败是 isLoadMoreEnabled == true 的一种情况
                isLoadMoreEnabled -> count + 1
                // 如果 isLoadMoreEnabled == false 且 showNoMoreEnabled == true
                // 则说明需要显示 加载完毕（没有更多） 视图
                showNoMoreEnabled -> count + 1
                else -> count
            }
        } else {
            count
        }
    }

    /**
     * count = [adapter].itemCount
     *
     *                     itemCount: count + 1      ┌┈┈ true ┈┈┈> TYPE_LOAD_FAILED
     *                     ┌┈┈ true ┈┈> failed ┈┈┈┈┈>|
     *                     |                         └┈┈ false ┈┈> TYPE_LOADING
     *                     |
     * isLoadMoreEnabled --|
     *                     |                         itemCount: count + 1
     *                     |                         ┌┈┈ true ┈┈┈> TYPE_NO_MORE
     *                     └┈┈ false┈┈> no more ┈┈┈┈>|
     *                                               └┈┈ false ┈┈>
     *                                               itemCount: count
     */
    override fun getItemViewType(position: Int): Int {
        val count = adapter.itemCount
        if (count > 0 && position == count) {
            // 判断是否启用（isLoadMoreEnabled == true）
            if (isLoadMoreEnabled) {
                // 优先判断是否加载失败
                return if (mLoadFailed) {
                    TYPE_LOAD_FAILED
                } else {
                    TYPE_LOADING
                }
            } else if (showNoMoreEnabled) {
                // 在 isLoadMoreEnabled == false 的情况下再判断是否显示 加载完毕 视图
                return TYPE_NO_MORE
            }
        }
        return adapter.getItemViewType(position)
    }

    override fun getItemId(position: Int): Long {
        val itemViewType = getItemViewType(position)
        return if (itemViewType != TYPE_LOADING
            && itemViewType != TYPE_LOAD_FAILED
            && itemViewType != TYPE_NO_MORE
        ) {
            adapter.getItemId(position)
        } else {
            super.getItemId(position)
        }
    }

    private fun canScroll(): Boolean {
        return mRecyclerView?.canScrollVertically(-1) ?: true
    }

    fun setLoadingView(@LayoutRes resId: Int) {
        mLoadingResId = resId
    }

    fun setNoMoreView(@LayoutRes resId: Int) {
        mNoMoreResId = resId
    }

    fun setLoadFailedView(@LayoutRes resId: Int) {
        mLoadFailedResId = resId
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRecyclerView = recyclerView
        recyclerView.addOnScrollListener(mOnScrollListener)
        try {
            adapter.registerAdapterDataObserver(mObserver)
        } catch (e: Throwable) {
        }
        // 当为 GridLayoutManager 的时候, 设置 footerView 占据整整一行.
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            // 获取原来的 SpanSizeLookup,当不为 null 的时候,除了 footerView 都应该返回原来的 spanSize
            val originalSizeLookup = layoutManager.spanSizeLookup
            layoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val itemViewType = getItemViewType(position)
                    if (itemViewType == TYPE_LOADING || itemViewType == TYPE_NO_MORE || itemViewType == TYPE_LOAD_FAILED) {
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
     * 判断是否触发加载更多
     */
    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (!isLoadMoreEnabled || mIsLoading || mLoadFailed) {
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
                    mLoadFailed = false
                    mOnLoadMoreListener?.onLoadMore(mLoadMore)
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

    fun setLoadMoreListener(listener: ((loadMore: LoadMore) -> Unit)? = null) {
        mOnLoadMoreListener = if (listener != null) {
            object : OnLoadMoreListener {
                override fun onLoadMore(loadMore: LoadMore) {
                    listener.invoke(loadMore)
                }
            }
        } else {
            null
        }
    }

    interface OnLoadMoreListener {
        fun onLoadMore(loadMore: LoadMore)
    }

    /**
     *  当 [isLoadMoreEnabled] 等于 false 时，是否显示已加载完毕视图
     */
    var showNoMoreEnabled = false

    /**
     *  是否启用加载更多
     */
    var isLoadMoreEnabled: Boolean
        get() = mLoadMore.isEnabled && adapter.itemCount >= 0
        set(enabled) {
            mLoadMore.isEnabled = enabled
        }

    /**
     *  是否加载失败
     */
    var isLoadFailed: Boolean
        get() = mLoadMore.isLoadFailed
        set(value) {
            mLoadMore.isLoadFailed = value
        }

    /**
     * 控制加载更多的开关, 作为 [OnLoadMoreListener.onLoadMore] 方法的参数
     */
    private class LoadMoreImpl(private val mListener: OnEnabledListener) : LoadMore {
        private var mLoadMoreEnable = true
        private var mIsLoadFailed = false

        /**
         * 设置是否加载失败
         */
        override var isLoadFailed: Boolean
            get() = mIsLoadFailed
            set(value) {
                if (mIsLoadFailed != value) {
                    mIsLoadFailed = value
                    mListener.notifyLoadFailed(value)
                }
            }

        /**
         * 获取是否启用了加载更多,默认是 true
         */
        override var isEnabled: Boolean
            get() = mLoadMoreEnable
            set(enable) {
                val old = mLoadMoreEnable
                mLoadMoreEnable = enable
                if (old && !mLoadMoreEnable) {
                    mListener.notifyChanged()
                }
            }

        interface OnEnabledListener {
            fun notifyChanged()
            fun notifyLoadFailed(isLoadFailed: Boolean)
        }
    }

    /**
     * 可以对加载状态进行操作
     */
    interface LoadMore {
        /**
         * 设置是否加载失败
         */
        var isLoadFailed: Boolean

        /**
         * 获取是否启用了加载更多,默认是 true
         */
        var isEnabled: Boolean
    }

    /**
     * update last item
     */
    private fun notifyFooterHolderChanged() {
        if (isLoadMoreEnabled || showNoMoreEnabled) {
            notifyItemChanged(adapter.itemCount)
        }
    }

    private class LoadingHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    private class NoMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    private class LoadFailedHolder(itemView: View, adapter: LoadMoreAdapter<*, *>, listener: OnLoadMoreListener?) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
            itemView.setOnClickListener {
                adapter.mLoadMore.isLoadFailed = false
                adapter.notifyFooterHolderChanged()
                listener?.onLoadMore(adapter.mLoadMore)
            }
        }
    }
}
