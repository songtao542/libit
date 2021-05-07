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
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.liabit.recyclerview.R

abstract class AbstractLoadMoreAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), LoadMore {

    companion object {
        private const val TYPE_LOADING = -234623
        private const val TYPE_NO_MORE = -345674
        private const val TYPE_LOAD_FAILED = -445678
        private const val TYPE_IDLE = -675454

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

    private var mLoadingText: String? = null
    private var mLoadingTextSize: Float? = null
    private var mLoadingTextColor: ColorStateList? = null
    private var mLoadingProgressColor: ColorStateList? = null

    private var mLoadFailedText: String? = null
    private var mLoadFailedTextSize: Float? = null
    private var mLoadFailedTextColor: ColorStateList? = null
    private var mLoadFailedIcon: Drawable? = null
    private var mLoadFailedIconColor: ColorStateList? = null

    private var mLoadNoMoreText: String? = null
    private var mLoadNoMoreTextColor: ColorStateList? = null
    private var mLoadNoMoreTextSize: Float? = null
    private var mLoadNoMoreIcon: Drawable? = null
    private var mLoadNoMoreIconColor: ColorStateList? = null

    private var mRecyclerView: RecyclerView? = null
    private var mOnLoadMoreListener: OnLoadMoreListener? = null

    private var mUpdateLoadingWhenNotify = false

    private val mLoadMore = LoadMoreImpl(object : LoadMoreImpl.OnEnabledListener {
        override fun notifyChanged(enabled: Boolean, oldState: Int, state: Int) {
            // 当 recyclerView 不能滚动的时候(item 不能铺满屏幕的时候也是不能滚动的)触发 loadMore
            /*if (enabled && getCount() > 0 && state == LoadMoreImpl.IDLE && !canScroll()) {
                invokeLoadMore()
            }*/
            notifyFooterHolderChanged()
        }
    })

    private fun createView(inflater: LayoutInflater, parent: ViewGroup, @LayoutRes resId: Int): View {
        return inflater.inflate(resId, parent, false)
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LOADING -> {
                LoadingHolder(createView(inflater, parent, getLoadingLayoutResourceId()))
            }
            TYPE_LOAD_FAILED -> {
                LoadFailedHolder(createView(inflater, parent, getLoadFailedLayoutResourceId()), this)
            }
            TYPE_NO_MORE -> {
                NoMoreHolder(createView(inflater, parent, getNoMoreLayoutResourceId()))
            }
            TYPE_IDLE -> {
                IdleHolder(createView(inflater, parent, getLoadingLayoutResourceId()))
            }
            else -> {
                onCreateHolder(parent, viewType)
            }
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, @NonNull payloads: List<Any>) {
        when (holder) {
            is LoadingHolder -> {
                holder.setup(mLoadingText, mLoadingTextColor, mLoadingTextSize, mLoadingProgressColor)
            }
            is LoadFailedHolder -> {
                holder.setup(mLoadFailedText, mLoadFailedTextColor, mLoadFailedTextSize, mLoadFailedIcon, mLoadFailedIconColor)
            }
            is NoMoreHolder -> {
                holder.setup(mLoadNoMoreText, mLoadNoMoreIconColor, mLoadNoMoreTextSize, mLoadNoMoreIcon, mLoadNoMoreIconColor)
            }
            is IdleHolder -> {
                // do nothing
            }
            else -> {
                @Suppress("UNCHECKED_CAST")
                onBindHolder(holder as VH, position)
            }
        }
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    }

    private fun invokeLoadMore() {
        if (!isEnabled) return
        val recyclerView = mRecyclerView ?: return
        mLoadMore.state = LoadMoreImpl.LOADING
        mOnLoadMoreListener?.also {
            recyclerView.post {
                it.onLoadMore(mLoadMore)
            }
        } ?: kotlin.run {
            recyclerView.post {
                onLoadMore(mLoadMore)
            }
        }
    }

    final override fun getItemCount(): Int {
        val count = getCount()
        return if (count > 0 && mLoadMore.isEnabled) count + 1 else count
    }

    /**
     * count = [getCount]
     *
     *                     itemCount: count + 1      ┌┈┈ true ┈┈┈> TYPE_LOAD_FAILED
     *                     ┌┈┈ true ┈┈> failed ┈┈┈┈┈>|
     *                     |                         └┈┈ false ┈┈> TYPE_LOADING
     *                     |
     * isLoadMoreEnabled --|
     *                     |                         itemCount: count + 1
     *                     |                         ┌┈┈ true ┈┈┈> TYPE_NO_MORE
     *                     └┈┈ false┈┈> no more ┈┈┈┈>|
     *                                               └┈┈ false ┈┈> TYPE_IDLE
     *                                               itemCount: count + 1
     */
    final override fun getItemViewType(position: Int): Int {
        val count = getCount()
        if (count > 0 && position == count) {
            return when (state) {
                LoadMoreImpl.LOADING -> {
                    TYPE_LOADING
                }
                LoadMoreImpl.FAILED -> {
                    TYPE_LOAD_FAILED
                }
                LoadMoreImpl.NO_MORE -> {
                    TYPE_NO_MORE
                }
                else -> {
                    TYPE_IDLE
                }
            }
        }
        return getViewType(position)
    }

    abstract fun onCreateHolder(parent: ViewGroup, viewType: Int): VH
    abstract fun onBindHolder(holder: VH, position: Int)
    abstract fun getCount(): Int
    protected open fun onLoadMore(loadMore: LoadMore) {
    }

    protected open fun getViewType(position: Int): Int {
        return 0
    }

    /**
     * 控制加载更多的开关, 作为 [OnLoadMoreListener.onLoadMore] 方法的参数
     */
    private class LoadMoreImpl(private val mListener: OnEnabledListener) : LoadMore {
        private var mState = IDLE
        private var mEnabled = true

        var state: Int
            get() = mState
            set(value) {
                val old = mState
                mState = value
                if (old != value) {
                    mListener.notifyChanged(mEnabled, old, value)
                }
            }

        override fun finishLoad() {
            state = IDLE
        }

        override fun failedLoad() {
            state = FAILED
        }

        override fun allLoad() {
            state = NO_MORE
        }

        override var isEnabled: Boolean
            get() = mEnabled
            set(value) {
                val old = mEnabled
                mEnabled = value
                if (old != value) {
                    mListener.notifyChanged(value, mState, mState)
                }
            }

        interface OnEnabledListener {
            fun notifyChanged(enabled: Boolean, oldState: Int, state: Int)
        }

        companion object {
            const val FAILED = 1
            const val NO_MORE = 2
            const val LOADING = 4
            const val IDLE = 8
        }
    }

    /**
     * update last item
     */
    private fun notifyFooterHolderChanged() {
        if (mLoadMore.isEnabled) {
            notifyItemChanged(getCount())
        } else {
            notifyItemRemoved(getCount())
        }
    }


    private val state: Int get() = mLoadMore.state

    /**
     *  是否启用加载更多
     */
    override var isEnabled: Boolean
        get() = mLoadMore.isEnabled
        set(value) {
            mLoadMore.isEnabled = value
        }

    var autoLoadWhenPageNotFull = false

    override fun finishLoad() {
        mLoadMore.finishLoad()
    }

    override fun failedLoad() {
        mLoadMore.failedLoad()
    }

    override fun allLoad() {
        mLoadMore.allLoad()
    }

    @LayoutRes
    protected open fun getLoadingLayoutResourceId(): Int {
        return R.layout.load_more_base_footer
    }

    @LayoutRes
    protected open fun getLoadFailedLayoutResourceId(): Int {
        return R.layout.load_more_base_load_failed
    }

    @LayoutRes
    protected open fun getNoMoreLayoutResourceId(): Int {
        return R.layout.load_more_base_no_more
    }

    private fun canScroll(): Boolean {
        return mRecyclerView?.canScrollVertically(-1) ?: true
    }

    /**
     * 判断是否触发加载更多
     */
    private val mOnScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (getCount() == 0 || !mLoadMore.isEnabled || state != LoadMoreImpl.IDLE) {
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
                    invokeLoadMore()
                }
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
            layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
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
     * clean
     */
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(mOnScrollListener)
        mRecyclerView = null
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
        val typedArray = context.obtainStyledAttributes(styleResId, R.styleable.LoadMoreStyle)
        mLoadingText = typedArray.getString(R.styleable.LoadMoreStyle_loadingText)
        mLoadFailedText = typedArray.getString(R.styleable.LoadMoreStyle_loadMoreFailedText)
        mLoadNoMoreText = typedArray.getString(R.styleable.LoadMoreStyle_loadNoMoreText)

        val defaultColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.load_more_text_color))
        mLoadingTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadingTextColor) ?: defaultColor
        mLoadFailedTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreFailedTextColor) ?: defaultColor
        mLoadNoMoreTextColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadNoMoreTextColor) ?: defaultColor

        val defaultFailedIcon = ContextCompat.getDrawable(context, R.drawable.load_more_ic_reload)
        mLoadFailedIcon = typedArray.getDrawable(R.styleable.LoadMoreStyle_loadMoreFailedIcon) ?: defaultFailedIcon
        mLoadNoMoreIcon = typedArray.getDrawable(R.styleable.LoadMoreStyle_loadNoMoreIcon)

        mLoadingProgressColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadingProgressColor) ?: defaultColor
        mLoadFailedIconColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadMoreFailedIconColor) ?: defaultColor
        mLoadNoMoreIconColor = typedArray.getColorStateList(R.styleable.LoadMoreStyle_loadNoMoreIconColor) ?: defaultColor

        val defaultTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context.resources.displayMetrics)
        mLoadingTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadingTextSize, defaultTextSize)
        mLoadFailedTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadMoreFailedTextSize, defaultTextSize)
        mLoadNoMoreTextSize = typedArray.getDimension(R.styleable.LoadMoreStyle_loadNoMoreTextSize, defaultTextSize)
        typedArray.recycle()
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

    private class LoadingHolder(itemView: View) : BaseHolder(itemView) {
        fun setup(text: String?, textColor: ColorStateList?, textSize: Float?, progressColor: ColorStateList?) {
            itemView.findViewById<TextView>(R.id.loadMoreTextView)?.let { textView ->
                text?.let { textView.text = it }
                textColor?.let { textView.setTextColor(it) }
                textSize?.let { textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
            }
            itemView.findViewById<ProgressBar>(R.id.loadMoreProgressBar)?.let { progressBar ->
                progressColor?.let { progressBar.indeterminateTintList = it }
            }
        }
    }

    private class NoMoreHolder(itemView: View) : BaseHolder(itemView) {
        fun setup(text: String?, textColor: ColorStateList?, textSize: Float?, icon: Drawable?, iconColor: ColorStateList?) {
            itemView.findViewById<TextView>(R.id.loadMoreNoMoreTextView)?.let { textView ->
                text?.let { textView.text = it }
                textColor?.let { textView.setTextColor(it) }
                textSize?.let { textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
            }
            itemView.findViewById<ImageView>(R.id.loadMoreNoMoreIcon)?.let { imageView ->
                icon?.let { imageView.setImageDrawable(it) }
                iconColor?.let { imageView.imageTintList = it }
            }
        }
    }

    private class LoadFailedHolder(itemView: View, adapter: AbstractLoadMoreAdapter<*>) : BaseHolder(itemView) {
        init {
            itemView.setOnClickListener {
                adapter.invokeLoadMore()
            }
        }

        fun setup(text: String?, textColor: ColorStateList?, textSize: Float?, icon: Drawable?, iconColor: ColorStateList?) {
            itemView.findViewById<TextView>(R.id.loadMoreFailedTextView)?.let { textView ->
                text?.let { textView.text = it }
                textColor?.let { textView.setTextColor(it) }
                textSize?.let { textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, it) }
            }
            itemView.findViewById<ImageView>(R.id.loadMoreFailedIcon)?.let { imageView ->
                icon?.let { imageView.setImageDrawable(it) }
                iconColor?.let { imageView.imageTintList = it }
            }
        }
    }

    private open class IdleHolder(itemView: View) : BaseHolder(itemView) {
        init {
            if (itemView is ViewGroup) {
                for (i in 0 until itemView.childCount) {
                    itemView.getChildAt(i).visibility = View.INVISIBLE
                }
            }
        }
    }

    private open class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            //当为StaggeredGridLayoutManager的时候,设置footerView占据整整一行
            val layoutParams = itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        }
    }

}

/**
 * 可以对加载状态进行操作
 */
interface LoadMore {

    var isEnabled: Boolean

    /**
     * 完成一次加载
     */
    fun finishLoad()

    /**
     * 加载失败
     */
    fun failedLoad()

    /**
     * 已加载全部数据
     */
    fun allLoad()
}