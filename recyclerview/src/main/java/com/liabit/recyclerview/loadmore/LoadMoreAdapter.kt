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
class LoadMoreAdapter<VH : RecyclerView.ViewHolder>(val adapter: RecyclerView.Adapter<VH>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_FOOTER = -2
        private const val TYPE_NO_MORE = -3
        private const val TYPE_LOAD_FAILED = -4

        @JvmStatic
        fun <VH : RecyclerView.ViewHolder> wrap(adapter: RecyclerView.Adapter<VH>): LoadMoreAdapter<VH> {
            return LoadMoreAdapter(adapter)
        }

        @JvmStatic
        fun <VH : RecyclerView.ViewHolder> attach(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<VH>) {
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

    private var mFooterResId: Int? = null
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

    private val loadMore = LoadMoreImpl(mOnEnabledListener)

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

    constructor(adapter: RecyclerView.Adapter<VH>, footerView: View) : this(adapter) {
        this.footerView = footerView
    }

    constructor(adapter: RecyclerView.Adapter<VH>, @LayoutRes resId: Int) : this(adapter) {
        mFooterResId = resId
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

        footerView?.findViewById<TextView>(R.id.loadMoreTextView)?.let {
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
        footerView?.findViewById<ProgressBar>(R.id.loadMoreProgressBar)?.let {
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
        footerView?.findViewById<TextView>(R.id.loadMoreTextView)?.text = mLoadingText
    }

    fun setLoadFailedText(loadFailedText: String) {
        mLoadFailedText = loadFailedText
        loadFailedView?.findViewById<TextView>(R.id.loadMoreFailedTextView)?.text = mLoadFailedText
    }

    fun setLoadNoMoreText(loadNoMoreText: String) {
        mLoadNoMoreText = loadNoMoreText
        noMoreView?.findViewById<TextView>(R.id.loadMoreNoMoreTextView)?.text = mLoadNoMoreText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        setStyle(parent.context, mStyleResId)
        when (viewType) {
            TYPE_FOOTER -> {
                val view = footerView ?: mFooterResId?.let {
                    LayoutInflater.from(parent.context).inflate(it, parent, false)
                } ?: kotlin.run {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_footer, parent, false)
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
                footerView = view
                return FooterHolder(view)
            }
            TYPE_NO_MORE -> {
                val view = noMoreView ?: mNoMoreResId?.let {
                    LayoutInflater.from(parent.context).inflate(it, parent, false)
                } ?: kotlin.run {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_no_more, parent, false)
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
                val view = loadFailedView ?: mLoadFailedResId?.let {
                    LayoutInflater.from(parent.context).inflate(it, parent, false)
                } ?: kotlin.run {
                    LayoutInflater.from(parent.context).inflate(R.layout.load_more_base_load_failed, parent, false)
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
                return LoadFailedHolder(view, loadMore, mOnLoadMoreListener)
            }
            else -> return adapter.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (holder is LoadMoreAdapter<*>.FooterHolder) {
            // 当 recyclerView 不能滚动的时候(item 不能铺满屏幕的时候也是不能滚动的) call loadMore
            if (!canScroll() && mOnLoadMoreListener != null && !mIsLoading) {
                mIsLoading = true
                // fix Cannot call this method while RecyclerView is computing a layout or scrolling
                mRecyclerView?.post {
                    mOnLoadMoreListener?.onLoadMore(loadMore)
                }
            }
        } else if (holder is LoadMoreAdapter<*>.NoMoreHolder || holder is LoadMoreAdapter<*>.LoadFailedHolder) {
            // ignore
        } else {
            @Suppress("UNCHECKED_CAST")
            adapter.onBindViewHolder(holder as VH, position, payloads)
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

    private inner class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    private inner class NoMoreHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

    private inner class LoadFailedHolder(itemView: View, enabled: LoadMore, listener: OnLoadMoreListener?)
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
    private class LoadMoreImpl(private val mListener: OnEnabledListener) : LoadMore {
        private var mLoadMoreEnable = true
        private var mIsLoadFailed = false

        /**
         * 设置是否加载失败
         */
        override var isLoadFailed: Boolean
            get() = mIsLoadFailed
            set(isLoadFailed) {
                if (mIsLoadFailed != isLoadFailed) {
                    mIsLoadFailed = isLoadFailed
                    mListener.notifyLoadFailed(isLoadFailed)
                    //isEnable = !mIsLoadFailed
                }
            }

        /**
         * 获取是否启用了加载更多,默认是 true
         */
        override var isEnable: Boolean
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
        var isEnable: Boolean
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
            if (viewHolder is LoadMoreAdapter<*>.FooterHolder) {
                notifyItemRemoved(position)
            } else {
                this@LoadMoreAdapter.notifyItemChanged(position)
            }
        }
    }
}
