package com.lolii.filter

import android.text.InputType
import android.util.ArrayMap
import android.view.View
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author:         songtao
 * CreateDate:     2020/9/12 17:58
 */
interface Filter {
    @Suppress("MayBeConstant")
    companion object {
        const val TYPE_GROUP = 0
        const val TYPE_DATE = 1
        const val TYPE_DATE_RANGE = 2
        const val TYPE_TEXT = 3
        const val TYPE_CHECKABLE = 4
        const val TYPE_NUMBER = 5
        const val TYPE_NUMBER_RANGE = 6
        const val TYPE_EDITABLE = 7
        const val TYPE_EDITABLE_RANGE = 8
        const val TYPE_OTHER = 9

        val TYPE_MAP = ArrayMap<Int, Int>().apply {
            put(TYPE_GROUP, R.layout.filter_text)
            put(TYPE_DATE, R.layout.filter_label)
            put(TYPE_DATE_RANGE, R.layout.filter_date_range)
            put(TYPE_TEXT, R.layout.filter_text)
            put(TYPE_CHECKABLE, R.layout.filter_checkable)
            put(TYPE_NUMBER, R.layout.filter_label)
            put(TYPE_EDITABLE, R.layout.filter_editable)
            put(TYPE_EDITABLE_RANGE, R.layout.filter_editable_range)
            put(TYPE_NUMBER_RANGE, R.layout.filter_number_range)
        }
    }

    /**
     * 标签类型
     */
    fun getType(): Int
}


interface FilterItem : Filter {
    /**
     * 标签名称
     */
    fun getName(): String

    /**
     * 标签提示语
     */
    fun getHint(): String {
        return getName()
    }
}

interface FilterGroup : FilterItem {
    /**
     * 类别标签列表
     */
    fun getItems(): List<Filter>
    fun isSingleChoice() = false
}

interface RangeFilterItem : Filter {
    fun getStartName(): String
    fun getStartHint(): String {
        return getStartName()
    }

    fun getEndName(): String
    fun getEndHint(): String {
        return getEndName()
    }
}

interface DateRange {
    fun getMinDate(): Date?
    fun getMaxDate(): Date?
}

interface DateFilterItem : FilterItem, DateRange {
    fun setDate(date: Date?)
    fun getDate(): Date?
}

interface DateRangeFilterItem : RangeFilterItem, DateRange {
    fun setStartDate(date: Date?)
    fun getStartDate(): Date?

    fun setEndDate(date: Date?)
    fun getEndDate(): Date?
}

interface CheckableFilterItem : FilterItem {
    fun setChecked(checked: Boolean)

    /**
     * 是否被选中
     */
    fun isChecked(): Boolean = false
}

interface EditableFilterItem : FilterItem {
    /**
     *  获取输入文本
     */
    fun getText(): String

    fun setText(text: String)

    override fun getName(): String {
        return getHint()
    }

    /**
     * {see android.text.InputType}
     */
    fun getInputType(): Int {
        return InputType.TYPE_CLASS_TEXT
    }
}

interface EditableRangeFilterItem : RangeFilterItem {
    /**
     *  获取输入文本
     */
    fun getStartText(): String
    fun setStartText(text: String)

    /**
     *  获取输入文本
     */
    fun getEndText(): String
    fun setEndText(text: String)

    /**
     * {see android.text.InputType}
     */
    fun getInputType(): Int {
        return InputType.TYPE_CLASS_TEXT
    }
}

interface FilterConfigurator {
    /**
     * 布局文件
     */
    fun getLayoutResource(): Map<Int, Int> {
        return Filter.TYPE_MAP
    }

    /**
     * 配置显示
     */
    fun configure(viewType: Int, itemView: View, filterItem: Filter) {
    }
}

class SimpleFilterConfigurator : FilterConfigurator

open class SimpleFilterGroup(private val name: String) : FilterGroup {

    var tag: Any? = null

    constructor(name: String, tag: Any) : this(name) {
        this.tag = tag
    }

    private val mItems = ArrayList<FilterItem>()
    private var mSingleChoice = false

    constructor(name: String, singleChoice: Boolean) : this(name) {
        this.mSingleChoice = singleChoice
    }

    constructor(name: String, items: List<FilterItem>) : this(name) {
        this.mItems.addAll(items)
    }

    constructor(name: String, singleChoice: Boolean, items: List<FilterItem>) : this(name) {
        this.mSingleChoice = singleChoice
        this.mItems.addAll(items)
    }

    fun add(item: FilterItem) {
        mItems.add(item)
    }

    fun addAll(items: List<FilterItem>) {
        mItems.addAll(items)
    }

    override fun getName(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return Filter.TYPE_GROUP
    }

    override fun getItems(): List<FilterItem> {
        return mItems
    }

    fun setSingleChoice(singleChoice: Boolean) {
        mSingleChoice = singleChoice
    }

    override fun isSingleChoice(): Boolean {
        return mSingleChoice
    }

    fun getCheckedValues(): List<FilterItem> {
        var values = ArrayList<FilterItem>();
        for (item in mItems) {
            if (item is CheckableFilterItem && item.isChecked()) {
                values.add(item)
            }
        }
        return values
    }


    fun getCheckedValue(): FilterItem? {
        for (item in mItems) {
            if (item is CheckableFilterItem && item.isChecked()) {
                return item
            }
        }
        return null
    }

    fun getDate(): Date? {
        for (item in mItems) {
            if (item is DateFilterItem) {
                return item.getDate()
            }
        }
        return null
    }

    fun getDateRange(): Pair<Date?, Date?>? {
        for (item in mItems) {
            if (item is DateRangeFilterItem) {
                return Pair(item.getStartDate(), item.getEndDate())
            }
        }
        return null
    }

    fun getText(): String? {
        for (item in mItems) {
            if (item is EditableFilterItem) {
                return item.getText()
            }
        }
        return null
    }

    fun getTextRange(): Pair<String, String>? {
        for (item in mItems) {
            if (item is EditableRangeFilterItem) {
                return Pair(item.getStartText(), item.getEndText())
            }
        }
        return null
    }
}

open class SimpleEditableFilterItem(private var mHint: String) : EditableFilterItem {
    private var mText: String = ""
    override fun getText(): String {
        return mText
    }

    override fun setText(text: String) {
        mText = text
    }

    override fun getHint(): String {
        return mHint
    }

    override fun getType(): Int {
        return Filter.TYPE_EDITABLE
    }
}

open class SimpleEditableRangeFilterItem(private var mStartHint: String = "",
                                         private var mEndHint: String = "")
    : EditableRangeFilterItem {

    private var mInputType: Int = InputType.TYPE_CLASS_TEXT
    private var mStartText: String = ""
    private var mEndText: String = ""

    override fun getStartText(): String {
        return mStartText
    }

    override fun setStartText(text: String) {
        mStartText = text
    }

    override fun getStartName(): String {
        return getStartHint()
    }

    override fun getStartHint(): String {
        return mStartHint
    }

    override fun getEndText(): String {
        return mEndText
    }

    override fun setEndText(text: String) {
        mEndText = text
    }

    override fun getEndName(): String {
        return getEndHint()
    }

    override fun getEndHint(): String {
        return mEndHint
    }

    fun setInputType(inputType: Int) {
        mInputType = inputType
    }

    override fun getInputType(): Int {
        return mInputType
    }

    override fun getType(): Int {
        return Filter.TYPE_EDITABLE_RANGE
    }
}

open class SimpleDateRange : DateRange {
    private val current: Calendar = Calendar.getInstance(TimeZone.getDefault())
    private val min: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(current.get(Calendar.YEAR) - 10, 1, 1)
    }
    private val max: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(current.get(Calendar.YEAR) + 10, 1, 1)
    }

    private var mYearOffset: Int = 0

    override fun getMinDate(): Date? {
        if (mYearOffset > 0) {
            min.set(current.get(Calendar.YEAR) - mYearOffset, 1, 1)
            return min.time
        }
        return min.time
    }

    override fun getMaxDate(): Date? {
        if (mYearOffset > 0) {
            max.set(current.get(Calendar.YEAR) + mYearOffset, 1, 1)
            return max.time
        }
        return max.time
    }

    fun setYearOffset(yearOffset: Int) {
        mYearOffset = yearOffset
    }
}

open class SimpleDateFilterItem(private val name: String) : SimpleDateRange(), DateFilterItem {
    private var mDate: Date? = null

    override fun getDate(): Date? {
        return mDate
    }

    override fun setDate(date: Date?) {
        mDate = date
    }

    override fun getName(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return Filter.TYPE_DATE
    }
}

open class SimpleDateRangeFilterItem(private val mStartHint: String,
                                     private val mEndHint: String) : SimpleDateRange(), DateRangeFilterItem {
    private var mEndDate: Date? = null
    private var mStartDate: Date? = null

    override fun getStartDate(): Date? {
        return mStartDate
    }

    override fun setStartDate(date: Date?) {
        mStartDate = date
    }

    override fun getEndDate(): Date? {
        return mEndDate
    }

    override fun setEndDate(date: Date?) {
        mEndDate = date
    }

    override fun getStartName(): String {
        return mStartHint
    }

    override fun getEndName(): String {
        return mEndHint
    }

    override fun getStartHint(): String {
        return mStartHint
    }

    override fun getEndHint(): String {
        return mEndHint
    }

    override fun getType(): Int {
        return Filter.TYPE_DATE_RANGE
    }
}

open class SimpleCheckableFilterItem(private val name: String) : CheckableFilterItem {

    private var mChecked = false

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(checked: Boolean) {
        mChecked = checked
    }

    override fun getName(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return Filter.TYPE_CHECKABLE
    }
}