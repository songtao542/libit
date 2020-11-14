package com.liabit.filter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
    private var mLeftPageFilterAdapter = FilterViewAdapter()
    private var mRightPageFilterAdapter: FilterViewAdapter? = null
    private var mFooter: View? = null
    private var mReset: View? = null
    private var mConfirm: View? = null
    private var mViewPager: ViewPager? = null
    private var mTabLayout: TabLayout? = null

    private var mPageCount = 1

    private var mLeftPageClickToReturn: Boolean = false
    private var mRightPageClickToReturn: Boolean = false
    private var mLeftPageTitle: String = ""
    private var mRightPageTitle: String = ""
    private var mLeftPageListPadding: Rect? = null
    private var mRightPageListPadding: Rect? = null
    private var mOnResultListener: OnResultListener? = null
    private var mOnCombinationResultListener: OnCombinationResultListener? = null
    private var mOnResetListener: OnResetListener? = null
    private var mOnConfirmListener: OnConfirmListener? = null

    private var mFilterPicker: IPicker? = null

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

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FilterLayout, defStyleAttr, defStyleRes)
            val pickerClass = typedArray.getString(R.styleable.FilterLayout_picker)
            if (pickerClass != null) {
                try {
                    mFilterPicker = Class.forName(pickerClass).newInstance() as? IPicker
                } catch (e: Throwable) {
                    Log.d(TAG, "init picker error: ", e)
                }
            }
            typedArray.recycle()
        }

        setBackgroundColor(ResourcesCompat.getColor(context.resources, android.R.color.white, null))
        LayoutInflater.from(context).inflate(R.layout.filter_layout, this, true)
        mTabLayout = findViewById(R.id.tabLayout)
        mViewPager = findViewById(R.id.viewpager)
        mFooter = findViewById(R.id.footer)
        mReset = findViewById(R.id.reset)
        mConfirm = findViewById(R.id.confirm)

        mPageCount = if (mRightPageFilterAdapter != null) 2 else 1

        if (mPageCount == 1) {
            mTabLayout?.visibility = View.GONE
        } else {
            if (mRightPageFilterAdapter == null) {
                mRightPageFilterAdapter = FilterViewAdapter()
                mRightPageFilterAdapter?.setFilterPicker(mFilterPicker)
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
                return if (position == 0) mLeftPageTitle else mRightPageTitle
            }
        }
        mReset?.setOnClickListener {
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

        mConfirm?.setOnClickListener {
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

        mLeftPageFilterAdapter.setFilterPicker(mFilterPicker)
        mRightPageFilterAdapter?.setFilterPicker(mFilterPicker)

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
        recyclerView.addItemDecoration(FlexboxItemDecoration(context).apply {
            setDrawable(ContextCompat.getDrawable(context, R.drawable.filter_item_divider))
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
        fun onResult(result: List<Filter>)
    }

    interface OnCombinationResultListener {
        fun onResult(pageLeftResult: List<Filter>?, pageRightResult: List<Filter>?)
    }

    interface OnResetListener {
        fun onReset(view: View)
    }

    interface OnConfirmListener {
        fun onConfirm(view: View)
    }

    fun setFilterPicker(filterPicker: IPicker?) {
        mFilterPicker = filterPicker
        mLeftPageFilterAdapter.setFilterPicker(mFilterPicker)
        mRightPageFilterAdapter?.setFilterPicker(mFilterPicker)
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
                mRightPageFilterAdapter = FilterViewAdapter()
                mRightPageFilterAdapter?.setFilterPicker(mFilterPicker)
            }
            mRightPageFilterAdapter?.setClickToReturnListener {
                mOnCombinationResultListener?.onResult(null, arrayListOf(it))
            }
        }
        configFooter()
    }

    fun setTabTitle(leftPageTitle: String?, rightPageTitle: String?) {
        if (leftPageTitle != null) {
            mLeftPageTitle = leftPageTitle
        }
        if (rightPageTitle != null) {
            mRightPageTitle = rightPageTitle
        }
        mTabLayout?.setupWithViewPager(mViewPager)
    }

    fun setLeftPageFilter(items: List<Filter>, adapter: FilterAdapter? = null) {
        mLeftPageFilterAdapter.mFilterAdapter = adapter
        mLeftPageFilterAdapter.setData(items, context != null)
    }

    fun setRightPageFilter(items: List<Filter>, adapter: FilterAdapter? = null) {
        if (mRightPageFilterAdapter == null) {
            mRightPageFilterAdapter = FilterViewAdapter()
            mRightPageFilterAdapter?.setFilterPicker(mFilterPicker)
            context?.let { initRightPage(it) }
        }
        mRightPageFilterAdapter?.mFilterAdapter = adapter
        mRightPageFilterAdapter?.setData(items, context != null)
    }

    class FilterViewAdapter : RecyclerView.Adapter<FilterViewAdapter.ViewHolder>() {

        private val mDefaultLayoutId = R.layout.filter_text

        var mOriginData: List<Filter>? = null
        private val mData = ArrayList<Filter>()

        var mFilterAdapter: FilterAdapter? = null

        var mClickToBackListener: ((item: Filter) -> Unit)? = null

        var mFilterPicker: IPicker? = null

        fun setFilterPicker(filterPicker: IPicker?) {
            mFilterPicker = filterPicker
        }

        fun reset() {
            for (item in mData) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is CheckableFilterItem) {
                    filterItem.setChecked(false)
                }
                if (filterItem is DateFilterItem) {
                    filterItem.setDate(null)
                }
                if (filterItem is DateRangeFilterItem) {
                    filterItem.setStartDate(null)
                    filterItem.setEndDate(null)
                }
                if (filterItem is NumberFilterItem) {
                    filterItem.setNumber(null)
                }
                if (filterItem is NumberRangeFilterItem) {
                    filterItem.setStartNumber(null)
                    filterItem.setEndNumber(null)
                }
                if (filterItem is AddressFilterItem) {
                    filterItem.setAddress(null)
                }
            }
        }

        fun setData(data: List<Filter>, notify: Boolean = true) {
            mOriginData = data
            mData.clear()
            for (item in data) {
                mData.add(item)
                if (item is FilterGroup) {
                    for (childItem in item.getChildren()) {
                        mData.add(WrapperFilterItem(childItem, item))
                    }
                }
            }
            if (notify) {
                notifyDataSetChanged()
            }
        }

        fun setClickToReturnListener(clickToBackListener: ((item: Filter) -> Unit)?) {
            mClickToBackListener = clickToBackListener
        }

        override fun getItemViewType(position: Int): Int {
            return mData[position].getType()
        }

        override fun getItemCount(): Int {
            return mData.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var resId = mFilterAdapter?.getLayoutResource()?.get(viewType)
                    ?: Filter.TYPE_MAP[viewType]
            if (resId == null) {
                Log.d(TAG, "can't find layout resource for view type: $viewType")
                resId = mDefaultLayoutId
            }
            val view = LayoutInflater.from(parent.context).inflate(resId, parent, false)
            val lp = view.layoutParams ?: FlexboxLayoutManager.LayoutParams(
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT,
                    FlexboxLayoutManager.LayoutParams.WRAP_CONTENT)
            (lp as? FlexboxLayoutManager.LayoutParams)?.let {
                it.flexGrow = if (viewType == Filter.TYPE_GROUP) 1f else 0f
                it.alignSelf = AlignItems.FLEX_START
                val margin = parent.context.resources.getDimension(R.dimen.filter_item_vertical_margin).toInt()
                it.topMargin = margin
                it.bottomMargin = margin
            }
            view.layoutParams = lp
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(mData[position])
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun setData(item: Filter) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (itemView is Checkable && filterItem is CheckableFilterItem) {
                    itemView.isChecked = filterItem.isChecked()
                }
                when (item.getType()) {
                    Filter.TYPE_GROUP -> {
                        itemView.findViewById<TextView>(R.id.text)?.text = (item as FilterGroup).getText()
                    }
                    Filter.TYPE_EDITABLE -> {
                        itemView.findViewById<EditText>(R.id.text)?.let {
                            configureEditable(item, it, false)
                        }
                    }
                    Filter.TYPE_EDITABLE_RANGE -> {
                        itemView.findViewById<EditText>(R.id.start)?.let {
                            configureEditable(item, it, false)
                        }
                        itemView.findViewById<EditText>(R.id.end)?.let {
                            configureEditable(item, it, true)
                        }
                    }
                    Filter.TYPE_TEXT -> {
                        itemView.findViewById<TextView>(R.id.text)?.let {
                            configureCheckable(item, it)
                        }
                    }
                    Filter.TYPE_CHECKABLE -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureCheckable(item, it)
                        }
                    }
                    Filter.TYPE_DATE -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureDate(item, it, false)
                        }
                    }
                    Filter.TYPE_DATE_RANGE -> {
                        itemView.findViewById<TextView>(R.id.startDate)?.let {
                            configureDate(item, it, false)
                        }
                        itemView.findViewById<TextView>(R.id.endDate)?.let {
                            configureDate(item, it, true)
                        }
                    }
                    Filter.TYPE_NUMBER -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureNumber(item, it, false)
                        }
                    }
                    Filter.TYPE_NUMBER_RANGE -> {
                        itemView.findViewById<TextView>(R.id.startNumber)?.let {
                            configureNumber(item, it, false)
                        }
                        itemView.findViewById<TextView>(R.id.endNumber)?.let {
                            configureNumber(item, it, true)
                        }
                    }
                    Filter.TYPE_ADDRESS -> {
                        itemView.findViewById<TextView>(R.id.label)?.let {
                            configureAddress(item, it)
                        }
                    }
                }
                if (mClickToBackListener != null) {
                    itemView.setOnClickListener {
                        mClickToBackListener?.invoke(filterItem)
                    }
                }
                mFilterAdapter?.configure(filterItem.getType(), itemView, filterItem)
            }

            private fun configureEditable(item: Filter, editText: EditText, end: Boolean) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is EditableRangeFilterItem) {
                    editText.inputType = filterItem.getInputType()
                    editText.hint = if (!end) filterItem.getStartHint() else filterItem.getEndHint()
                    if (!end) {
                        if (filterItem.getStartText().isNotBlank()) {
                            editText.setText(filterItem.getStartText())
                        }
                    } else {
                        if (filterItem.getEndText().isNotBlank()) {
                            editText.setText(filterItem.getEndText())
                        }
                    }
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        }

                        override fun afterTextChanged(s: Editable?) {
                            val text = s?.toString() ?: ""
                            if (!end) filterItem.setStartText(text) else filterItem.setEndText(text)
                        }
                    })
                } else if (filterItem is EditableFilterItem) {
                    editText.inputType = filterItem.getInputType()
                    editText.hint = filterItem.getHint()
                    if (filterItem.getText().isNotBlank()) {
                        editText.setText(filterItem.getText())
                    }
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

            private fun configureCheckable(item: Filter, textView: TextView) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is CheckableFilterItem) {
                    textView.hint = filterItem.getHint()
                    if (filterItem.getText().isNotBlank()) {
                        textView.text = filterItem.getText()
                    }
                    textView.setOnClickListener {
                        filterItem.setChecked(!filterItem.isChecked())
                        if (item is WrapperFilterItem && item.parent.isSingleChoice()) {
                            for (child in item.parent.getChildren()) {
                                if (child == item || child == item.wrapped) {
                                    continue
                                }
                                (child as? CheckableFilterItem)?.setChecked(false)
                            }
                        }
                        notifyDataSetChanged()
                    }
                }
            }

            private fun configureDate(item: Filter, textView: TextView, end: Boolean) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is DateRangeFilterItem) {
                    textView.hint = if (!end) filterItem.getStartHint() else filterItem.getEndHint()
                    var startText: CharSequence = filterItem.getStartText()
                    if (startText.isBlank()) {
                        startText = filterItem.getStartHint()
                    }
                    var endText: CharSequence = filterItem.getEndText()
                    if (endText.isBlank()) {
                        endText = filterItem.getEndHint()
                    }
                    textView.text = format(
                            if (!end) startText else endText,
                            if (!end) filterItem.getStartDate() else filterItem.getEndDate()
                    )
                    textView.setOnClickListener {
                        val listener = object : IPicker.OnDateSelectListener {
                            override fun onDateSelect(date: Date) {
                                if (!end) {
                                    filterItem.setStartDate(date)
                                } else {
                                    filterItem.setEndDate(date)
                                }
                                notifyDataSetChanged()
                            }
                        }
                        val currentDate = if (end) {
                            filterItem.getEndDate() ?: Date()
                        } else {
                            filterItem.getStartDate() ?: Date()
                        }
                        val minDate = if (end) {
                            filterItem.getStartDate() ?: filterItem.getMin()
                        } else {
                            filterItem.getMin()
                        }
                        val maxData = if (end) {
                            filterItem.getMax()
                        } else {
                            filterItem.getEndDate() ?: filterItem.getMax()
                        }
                        mFilterPicker?.pickDate(textView.context, currentDate, minDate, maxData, listener)
                    }
                } else if (filterItem is DateFilterItem) {
                    textView.hint = filterItem.getHint()
                    var text: CharSequence = filterItem.getText()
                    if (text.isBlank()) {
                        text = filterItem.getHint()
                    }
                    textView.text = format(text, filterItem.getDate())
                    textView.setOnClickListener {
                        val listener = object : IPicker.OnDateSelectListener {
                            override fun onDateSelect(date: Date) {
                                filterItem.setDate(date)
                                notifyDataSetChanged()
                            }
                        }
                        val currentDate = filterItem.getDate() ?: Date()
                        mFilterPicker?.pickDate(textView.context, currentDate, filterItem.getMin(), filterItem.getMax(), listener)
                    }
                }
            }

            @SuppressLint("SimpleDateFormat")
            private fun format(hint: CharSequence, date: Date?): CharSequence {
                if (date == null) {
                    return hint
                }
                val format = SimpleDateFormat("yyyy-MM-dd")
                return format.format(date)
            }


            private fun configureAddress(item: Filter, textView: TextView) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is AddressFilterItem) {
                    textView.hint = filterItem.getHint()
                    val address = filterItem.getAddress()
                    if (address == null) {
                        if (filterItem.getText().isNotBlank()) {
                            textView.text = filterItem.getText()
                        }
                    } else {
                        textView.text = address.formatted
                    }
                    textView.setOnClickListener {
                        mFilterPicker?.pickAddress(textView.context, address, object : IPicker.OnAddressSelectListener {
                            override fun onAddressSelect(address: Address) {
                                filterItem.setAddress(address)
                                notifyDataSetChanged()
                            }
                        })
                    }
                }
            }

            private fun configureNumber(item: Filter, textView: TextView, end: Boolean) {
                val filterItem = if (item is WrapperFilterItem) item.wrapped else item
                if (filterItem is NumberRangeFilterItem) {
                    textView.hint = if (!end) filterItem.getStartHint() else filterItem.getEndHint()
                    var startText: CharSequence? = filterItem.getStartNumber()?.toString()
                    if (startText.isNullOrBlank()) {
                        startText = filterItem.getStartText()
                        if (startText.isBlank()) {
                            startText = filterItem.getStartHint()
                        }
                    }
                    var endText: CharSequence? = filterItem.getEndNumber()?.toString()
                    if (endText.isNullOrBlank()) {
                        endText = filterItem.getEndText()
                        if (endText.isBlank()) {
                            endText = filterItem.getEndHint()
                        }
                    }
                    textView.text = if (!end) startText else endText
                    textView.setOnClickListener {
                        val listener = object : IPicker.OnNumberSelectListener {
                            override fun onNumberSelect(number: Int) {
                                if (!end) {
                                    filterItem.setStartNumber(number)
                                } else {
                                    filterItem.setEndNumber(number)
                                }
                                notifyDataSetChanged()
                            }
                        }
                        val currentNumber = if (end) {
                            filterItem.getEndNumber()
                        } else {
                            filterItem.getStartNumber()
                        }
                        val minNumber = if (end) {
                            filterItem.getStartNumber() ?: filterItem.getMin()
                        } else {
                            filterItem.getMin()
                        }
                        val maxNumber = if (end) {
                            filterItem.getMax()
                        } else {
                            filterItem.getEndNumber() ?: filterItem.getMax()
                        }
                        mFilterPicker?.pickNumber(textView.context, currentNumber, minNumber, maxNumber, listener)
                    }
                } else if (filterItem is NumberFilterItem) {
                    textView.hint = filterItem.getHint()
                    var text: CharSequence? = filterItem.getNumber()?.toString()
                    if (text.isNullOrBlank()) {
                        text = filterItem.getText()
                        if (text.isBlank()) {
                            text = filterItem.getHint()
                        }
                    }
                    textView.text = text
                    textView.setOnClickListener {
                        val listener = object : IPicker.OnNumberSelectListener {
                            override fun onNumberSelect(number: Int) {
                                filterItem.setNumber(number)
                                notifyDataSetChanged()
                            }
                        }
                        val currentNumber = filterItem.getNumber()
                        mFilterPicker?.pickNumber(textView.context, currentNumber, filterItem.getMin(), filterItem.getMax(), listener)
                    }
                }
            }
        }
    }

    private class WrapperFilterItem(val wrapped: Filter, val parent: FilterGroup) :
            Filter by wrapped

}
