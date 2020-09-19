package com.lolii.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.flexbox.*
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Author:         songtao
 * CreateDate:     2020/9/13 15:21
 */
@Suppress("unused")
class FilterLayout : LinearLayout {

    companion object {
        const val TAG = "FilterLayout"
    }

    private var mLeftPage: LinearLayout? = null
    private var mRightPage: LinearLayout? = null
    private var mLeftPageRecycleView: RecyclerView? = null
    private var mRightPageRecycleView: RecyclerView? = null
    private val mLeftPageFilterAdapter by lazy { FilterAdapter() }
    private var mRightPageFilterAdapter: FilterAdapter? = null
    private var mFooter: View? = null
    private var mLeftButton: TextView? = null
    private var mRightButton: TextView? = null
    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null
    private var mRowDividerHeight = 0f
    private var mColumnDividerWidth = 0f
    private var mPageCount = 1

    private var mLeftPageClickToReturn: Boolean = false
    private var mRightPageClickToReturn: Boolean = false
    private var mLeftTabTitle: CharSequence = ""
    private var mRightTabTitle: CharSequence = ""
    private var mLeftPageListPadding: Rect? = null
    private var mRightPageListPadding: Rect? = null
    private var mOnResultListener: OnResultListener? = null
    private var mOnCombinationResultListener: OnCombinationResultListener? = null
    private var mOnResetListener: OnResetListener? = null
    private var mOnConfirmListener: OnConfirmListener? = null

    private var mGroupItemBackground: Int = 0
    private var mCheckableItemBackground: Int = 0
    private var mDateItemBackground: Int = 0
    private var mNumberItemBackground: Int = 0
    private var mEditableItemBackground: Int = 0
    private var mTextItemBackground: Int = 0

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
        orientation = VERTICAL

        var leftButtonText: CharSequence? = null
        var rightButtonText: CharSequence? = null
        var leftButtonBackground = 0
        var rightButtonBackground = 0
        var pageCount = 1

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FilterLayout, defStyleAttr, defStyleRes)
            leftButtonText = typedArray.getText(R.styleable.FilterLayout_leftBottomButtonText)
            rightButtonText = typedArray.getText(R.styleable.FilterLayout_rightBottomButtonText)
            leftButtonBackground = typedArray.getResourceId(R.styleable.FilterLayout_leftBottomButtonBackground, 0)
            rightButtonBackground = typedArray.getResourceId(R.styleable.FilterLayout_rightBottomButtonBackground, 0)
            pageCount = typedArray.getInt(R.styleable.FilterLayout_columnDividerWidth, 1)
            mGroupItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_groupBackground, 0)
            mCheckableItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_checkableItemBackground, 0)
            mNumberItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_numberItemBackground, 0)
            mEditableItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_editableItemBackground, 0)
            mDateItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_dateItemBackground, 0)
            mTextItemBackground = typedArray.getResourceId(R.styleable.FilterLayout_textItemBackground, 0)
            val dh = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, context.resources.displayMetrics)
            mRowDividerHeight = typedArray.getDimension(R.styleable.FilterLayout_rowDividerHeight, dh)
            mColumnDividerWidth = typedArray.getDimension(R.styleable.FilterLayout_columnDividerWidth, dh)
            mLeftTabTitle = typedArray.getText(R.styleable.FilterLayout_columnDividerWidth) ?: ""
            mRightTabTitle = typedArray.getText(R.styleable.FilterLayout_columnDividerWidth) ?: ""
            typedArray.recycle()
        }

        //setBackgroundColor(ResourcesCompat.getColor(context.resources, android.R.color.white, null))
        LayoutInflater.from(context).inflate(R.layout.filter_layout, this, true)
        mTabLayout = findViewById(R.id.tabLayout)
        mViewPager = findViewById(R.id.viewpager)
        mFooter = findViewById(R.id.footer)
        mLeftButton = findViewById(R.id.leftBottomButton)
        mRightButton = findViewById(R.id.rightBottomButton)

        leftButtonText?.let { mLeftButton?.setText(it) }
        rightButtonText?.let { mRightButton?.setText(it) }
        if (leftButtonBackground != 0) {
            mLeftButton?.setBackgroundResource(leftButtonBackground)
        }
        if (rightButtonBackground != 0) {
            mRightButton?.setBackgroundResource(rightButtonBackground)
        }

        mPageCount = if (mRightPageFilterAdapter != null) 2 else pageCount

        if (mPageCount == 1) {
            mTabLayout?.visibility = View.GONE
        } else {
            if (mRightPageFilterAdapter == null) {
                mRightPageFilterAdapter = FilterAdapter()
            }
            initRightPage(context)
        }
        initLeftPage(context)

        mViewPager?.adapter = object : PagerAdapter() {
            override fun getCount(): Int {
                return mPageCount
            }

            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val view = if (position == 0) mLeftPage!! else mRightPage!!
                container.addView(view)
                return view
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return if (position == 0) mLeftTabTitle else mRightTabTitle
            }
        }
        mLeftButton?.setOnClickListener {
            when (mViewPager?.currentItem) {
                0 -> {
                    mLeftPageFilterAdapter.reset()
                    mLeftPageFilterAdapter.notifyDataSetChanged()
                }
                1 -> {
                    mRightPageFilterAdapter?.reset()
                    mRightPageFilterAdapter?.notifyDataSetChanged()
                }
            }
            mOnResetListener?.onReset(it)
        }

        mRightButton?.setOnClickListener {
            when (mViewPager?.currentItem) {
                0 -> {
                    mLeftPageFilterAdapter.mOriginData?.let { data ->
                        mOnResultListener?.onResult(data)
                    }
                }
                1 -> {
                    mRightPageFilterAdapter?.mOriginData?.let { data ->
                        mOnResultListener?.onResult(data)
                    }
                }
            }
            mOnCombinationResultListener?.onResult(mLeftPageFilterAdapter.mOriginData,
                    mRightPageFilterAdapter?.mOriginData)
            mOnConfirmListener?.onConfirm(it)
        }

        configFooter()
    }

    private fun initLeftPage(context: Context) {
        mLeftPage = LayoutInflater.from(context).inflate(R.layout.filter_list,
                mViewPager, false) as? LinearLayout
        mLeftPageRecycleView = mLeftPage?.findViewById(R.id.recyclerView)
        mLeftPageRecycleView?.let {
            mLeftPageListPadding?.let { rect ->
                it.setPadding(rect.left, rect.top, rect.right, rect.bottom)
            }
            initRecycleView(context, it)
            it.adapter = mLeftPageFilterAdapter
            if (mLeftPageFilterAdapter.itemCount > 0) {
                mLeftPageFilterAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun initRightPage(context: Context) {
        if (mRightPage == null || mRightPageRecycleView == null) {
            mRightPage = LayoutInflater.from(context).inflate(R.layout.filter_list,
                    mViewPager, false) as? LinearLayout
            mRightPageRecycleView = mRightPage?.findViewById(R.id.recyclerView)
            mTabLayout?.setupWithViewPager(mViewPager)
            mTabLayout?.visibility = View.VISIBLE
            mRightPageRecycleView?.let {
                mRightPageListPadding?.let { rect ->
                    it.setPadding(rect.left, rect.top, rect.right, rect.bottom)
                }
                initRecycleView(context, it)
                it.adapter = mRightPageFilterAdapter
                if ((mRightPageFilterAdapter?.itemCount ?: 0) > 0) {
                    mRightPageFilterAdapter?.notifyDataSetChanged()
                }
            }
            mPageCount = 2
            mViewPager?.adapter?.notifyDataSetChanged()
        }
    }

    private fun initRecycleView(context: Context, recyclerView: RecyclerView) {
        recyclerView.layoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }
        val drawable = GradientDrawable()
        mColumnDividerWidth.toInt().let { drawable.setSize(it, it) }
        recyclerView.addItemDecoration(FlexboxItemDecoration(context).apply {
            setDrawable(drawable)
            setOrientation(FlexboxItemDecoration.VERTICAL)
        })
    }

    private fun configFooter() {
        val footer = mFooter ?: findViewById(R.id.footer) ?: return
        if (mLeftPageClickToReturn || mRightPageClickToReturn) {
            (footer.parent as? ViewGroup)?.removeView(footer)
            if (!mLeftPageClickToReturn) {
                mLeftPage?.addView(footer)
            } else if (!mRightPageClickToReturn) {
                mRightPage?.addView(footer)
            }
        }
    }

    interface OnResultListener {
        fun onResult(result: List<FilterItem>)
    }

    interface OnCombinationResultListener {
        fun onResult(pageLeftResult: List<FilterItem>?, pageRightResult: List<FilterItem>?)
    }

    interface OnResetListener {
        fun onReset(view: View)
    }

    interface OnConfirmListener {
        fun onConfirm(view: View)
    }

    fun setOnResultListener(listener: OnResultListener) {
        mOnResultListener = listener
    }

    fun setOnCombinationResultListener(listener: OnCombinationResultListener) {
        mOnCombinationResultListener = listener
    }

    fun setOnResetListener(listener: OnResetListener) {
        mOnResetListener = listener
    }

    fun setOnConfirmListener(listener: OnConfirmListener) {
        mOnConfirmListener = listener
    }

    fun setLeftPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (mLeftPageListPadding == null) {
            mLeftPageListPadding = Rect()
        }
        mLeftPageListPadding?.set(left, top, right, bottom)
    }

    fun setRightPageListPadding(left: Int, top: Int, right: Int, bottom: Int) {
        if (mRightPageListPadding == null) {
            mRightPageListPadding = Rect()
        }
        mRightPageListPadding?.set(left, top, right, bottom)
    }

    fun setClickToReturnMode(leftPageClickToReturn: Boolean, rightPageClickToReturn: Boolean = false) {
        mLeftPageClickToReturn = leftPageClickToReturn
        mRightPageClickToReturn = rightPageClickToReturn
        if (leftPageClickToReturn) {
            mLeftPageFilterAdapter.setClickToReturnListener {
                mOnCombinationResultListener?.onResult(arrayListOf(it), null)
            }
        }
        if (rightPageClickToReturn) {
            if (mRightPageFilterAdapter == null) {
                mRightPageFilterAdapter = FilterAdapter()
            }
            mRightPageFilterAdapter?.setClickToReturnListener {
                mOnCombinationResultListener?.onResult(null, arrayListOf(it))
            }
        }
        configFooter()
    }

    fun setTabTitle(leftPageTitle: String?, rightPageTitle: String?) {
        if (leftPageTitle != null) {
            mLeftTabTitle = leftPageTitle
        }
        if (rightPageTitle != null) {
            mRightTabTitle = rightPageTitle
        }
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    fun setLeftPageFilter(items: List<FilterItem>, configurator: FilterConfigurator? = null) {
        mLeftPageFilterAdapter.mFilterConfigurator = configurator
        mLeftPageFilterAdapter.setData(items, context != null)
    }

    fun setRightPageFilter(items: List<FilterItem>, configurator: FilterConfigurator? = null) {
        if (mRightPageFilterAdapter == null) {
            mRightPageFilterAdapter = FilterAdapter()
            context?.let { initRightPage(it) }
        }
        mRightPageFilterAdapter?.mFilterConfigurator = configurator
        mRightPageFilterAdapter?.setData(items, context != null)
    }

    inner class FilterAdapter : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

        private val mDefaultLayoutId = R.layout.filter_text

        var mOriginData: List<FilterItem>? = null
        private val mData = ArrayList<FilterItem>()

        var mFilterConfigurator: FilterConfigurator? = null

        var mClickToBackListener: ((item: FilterItem) -> Unit)? = null

        fun reset() {
            for (item in mData) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is CheckableFilterItem) {
                    filterItem.setCheck(false)
                }
                if (filterItem is DateFilterItem) {
                    filterItem.setDate(null)
                }
                if (filterItem is DateRangeFilterItem) {
                    filterItem.setDate(null)
                    filterItem.setEndDate(null)
                }
            }
        }

        fun setData(data: List<FilterItem>, notify: Boolean = true) {
            mOriginData = data
            mData.clear()
            for (item in data) {
                mData.add(item)
                if (item is FilterGroup) {
                    for (childItem in item.getItems()) {
                        mData.add(WrapperFilterItem(childItem, item))
                    }
                }
            }
            if (notify) {
                notifyDataSetChanged()
            }
        }

        fun setClickToReturnListener(clickToBackListener: ((item: FilterItem) -> Unit)?) {
            mClickToBackListener = clickToBackListener
        }

        override fun getItemViewType(position: Int): Int {
            return mData[position].getType()
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var resId = mFilterConfigurator?.getLayoutResource()?.get(viewType)
                    ?: FilterItem.TYPE_MAP[viewType]
            if (resId == null) {
                Log.d(TAG, "can't find layout resource for view type: $viewType")
                resId = mDefaultLayoutId
            }
            val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
            val lp = view.layoutParams ?: FlexboxLayoutManager.LayoutParams(
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT,
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT)
            (lp as? FlexboxLayoutManager.LayoutParams)?.let {
                it.flexGrow = if (viewType == FilterItem.TYPE_GROUP) 1f else 0f
                it.alignSelf = AlignItems.FLEX_START
                mRowDividerHeight.toInt().let { height ->
                    it.topMargin = height
                    it.bottomMargin = height
                }
            }
            when (viewType) {
                FilterItem.TYPE_GROUP -> {
                    if (mGroupItemBackground != 0) view.setBackgroundResource(mGroupItemBackground)
                }
                FilterItem.TYPE_CHECKABLE -> {
                    if (mCheckableItemBackground != 0) view.setBackgroundResource(mCheckableItemBackground)
                }
                FilterItem.TYPE_DATE, FilterItem.TYPE_DATE_RANGE -> {
                    if (mDateItemBackground != 0) view.setBackgroundResource(mDateItemBackground)
                }
                FilterItem.TYPE_NUMBER, FilterItem.TYPE_NUMBER_RANGE -> {
                    if (mNumberItemBackground != 0) view.setBackgroundResource(mNumberItemBackground)
                }
                FilterItem.TYPE_EDITABLE, FilterItem.TYPE_EDITABLE_RANGE -> {
                    if (mEditableItemBackground != 0) view.setBackgroundResource(mEditableItemBackground)
                }
                FilterItem.TYPE_TEXT -> {
                    if (mTextItemBackground != 0) view.setBackgroundResource(mTextItemBackground)
                }
            }
            view.layoutParams = lp
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(mData[position])
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(item: FilterItem) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (itemView is Checkable && filterItem is CheckableFilterItem) {
                    itemView.isChecked = filterItem.isChecked()
                }
                when (item.getType()) {
                    FilterItem.TYPE_GROUP -> {
                        itemView.findViewById<TextView>(R.id.text)?.text = item.getName()
                    }
                    FilterItem.TYPE_EDITABLE -> {
                        itemView.findViewById<EditText>(R.id.text)?.let {
                            configureEditable(item, it, false)
                        }
                    }
                    FilterItem.TYPE_EDITABLE_RANGE -> {
                        itemView.findViewById<EditText>(R.id.start)?.let {
                            configureEditable(item, it, false)
                        }
                        itemView.findViewById<EditText>(R.id.end)?.let {
                            configureEditable(item, it, true)
                        }
                    }
                    FilterItem.TYPE_TEXT -> {
                        itemView.findViewById<TextView>(R.id.text)?.let {
                            configureCheckable(item, it)
                        }
                    }
                    FilterItem.TYPE_CHECKABLE -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureCheckable(item, it)
                        }
                    }
                    FilterItem.TYPE_DATE -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureDate(item, it, false)
                        }
                    }
                    FilterItem.TYPE_DATE_RANGE -> {
                        itemView.findViewById<TextView>(R.id.startDate)?.let {
                            configureDate(item, it, false)
                        }
                        itemView.findViewById<TextView>(R.id.endDate)?.let {
                            configureDate(item, it, true)
                        }
                    }
                    FilterItem.TYPE_NUMBER -> {
                    }
                    FilterItem.TYPE_NUMBER_RANGE -> {
                    }
                }
                if (mClickToBackListener != null) {
                    itemView.setOnClickListener {
                        mClickToBackListener?.invoke(filterItem)
                    }
                }
                mFilterConfigurator?.configure(filterItem.getType(), itemView, filterItem)
            }

            private fun configureEditable(item: FilterItem, editText: EditText, end: Boolean) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is EditableRangeFilterItem) {
                    editText.inputType = filterItem.getInputType()
                    editText.hint = if (!end) filterItem.getHint() else filterItem.getEndHint()
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val text = s?.toString() ?: ""
                            if (!end) filterItem.setText(text) else filterItem.setEndText(text)
                        }
                    })
                } else if (filterItem is EditableFilterItem) {
                    editText.inputType = filterItem.getInputType()
                    editText.hint = filterItem.getHint()
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            filterItem.setText(s?.toString() ?: "")
                        }
                    })
                }
            }

            private fun configureCheckable(item: FilterItem, textView: TextView) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is CheckableFilterItem) {
                    textView.hint = filterItem.getHint()
                    textView.text = filterItem.getName()
                    textView.setOnClickListener {
                        filterItem.setCheck(!filterItem.isChecked())
                        if (item is WrapperFilterItem && item.parent.isSingleChoice()) {
                            for (child in item.parent.getItems()) {
                                if (child == item || child == item.wrapped) {
                                    continue
                                }
                                (child as? CheckableFilterItem)?.setCheck(false)
                            }
                        }
                        notifyDataSetChanged()
                    }
                }
            }

            private fun configureDate(item: FilterItem, textView: TextView, end: Boolean) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is DateRangeFilterItem) {
                    textView.hint = if (!end) filterItem.getHint() else filterItem.getEndHint()
                    textView.text = format(
                            if (!end) filterItem.getName() else filterItem.getEndName(),
                            if (!end) filterItem.getDate() else filterItem.getEndDate()
                    )
                    textView.setOnClickListener {
                        Picker.pickDate(textView.context, object : Picker.OnDateSelectListener {
                            override fun onDateSelect(date: Date) {
                                if (!end) {
                                    filterItem.setDate(date)
                                } else {
                                    filterItem.setEndDate(date)
                                }
                                notifyDataSetChanged()
                            }
                        })
                    }
                } else if (filterItem is DateFilterItem) {
                    textView.hint = filterItem.getHint()
                    textView.text = format(filterItem.getName(), filterItem.getDate())
                    textView.setOnClickListener {
                        Picker.pickDate(textView.context, object : Picker.OnDateSelectListener {
                            override fun onDateSelect(date: Date) {
                                filterItem.setDate(date)
                                notifyDataSetChanged()
                            }
                        })
                    }
                }
            }

            @SuppressLint("SimpleDateFormat")
            private fun format(hint: String, date: Date?): String {
                if (date == null) {
                    return hint
                }
                val format = SimpleDateFormat("yyyy-MM-dd")
                return format.format(date)
            }
        }
    }

    private class WrapperFilterItem(val wrapped: FilterItem, val parent: FilterGroup) :
            FilterItem by wrapped

}