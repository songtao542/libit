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
interface FilterItem {
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

    /**
     * 标签类型
     */
    fun getType(): Int

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
            put(TYPE_CHECKABLE, R.layout.filter_label)
            put(TYPE_NUMBER, R.layout.filter_label)
            put(TYPE_NUMBER_RANGE, R.layout.filter_number_range)
            put(TYPE_EDITABLE, R.layout.filter_editable)
            put(TYPE_EDITABLE_RANGE, R.layout.filter_editable_range)
        }
    }
}

interface FilterGroup : FilterItem {
    /**
     * 类别标签列表
     */
    fun getItems(): List<FilterItem>
    fun isSingleChoice() = false
}

interface RangeFilterItem : FilterItem {
    fun getEndName(): String
    fun getEndHint(): String {
        return getEndName()
    }
}

interface DateFilterItem : FilterItem {
    fun setDate(date: Date?)
    fun getDate(): Date?
    fun getMinDate(): Date?
    fun getMaxDate(): Date?
}

interface DateRangeFilterItem : DateFilterItem, RangeFilterItem {
    fun setEndDate(date: Date?)
    fun getEndDate(): Date?
}

interface CheckableFilterItem : FilterItem {
    fun setCheck(check: Boolean)

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

interface EditableRangeFilterItem : EditableFilterItem, RangeFilterItem {
    /**
     *  获取输入文本
     */
    fun getEndText(): String
    fun setEndText(text: String)
}

interface FilterConfigurator {
    /**
     * 布局文件
     */
    fun getLayoutResource(): Map<Int, Int> {
        return FilterItem.TYPE_MAP
    }

    /**
     * 配置显示
     */
    fun configure(viewType: Int, itemView: View, filterItem: FilterItem) {
    }
}

class SimpleFilterConfigurator : FilterConfigurator

open class SimpleFilterGroup(private val name: String) : FilterGroup {

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
        return FilterItem.TYPE_GROUP
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
                return Pair(item.getDate(), item.getEndDate())
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
                return Pair(item.getText(), item.getText())
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
        return FilterItem.TYPE_EDITABLE
    }
}

open class SimpleEditableRangeFilterItem(private var mHint: String = "",
                                         private var mEndHint: String = "")
    : SimpleEditableFilterItem(mHint), EditableRangeFilterItem {

    private var mEndText: String = ""

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

    override fun getType(): Int {
        return FilterItem.TYPE_EDITABLE_RANGE
    }
}

open class SimpleDateFilterItem(private val name: String) : DateFilterItem {
    private var mDate: Date? = null

    override fun getDate(): Date? {
        return mDate
    }

    override fun getMinDate(): Date? {
        return null
    }

    override fun getMaxDate(): Date? {
        return null
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
        return FilterItem.TYPE_DATE
    }
}

open class SimpleDateRangeFilterItem(name: String,
                                     private val mEndHint: String) :
        SimpleDateFilterItem(name), DateRangeFilterItem {
    private var mEndDate: Date? = null

    override fun setEndDate(date: Date?) {
        mEndDate = date
    }

    override fun getEndDate(): Date? {
        return mEndDate
    }

    override fun getType(): Int {
        return FilterItem.TYPE_DATE_RANGE
    }

    override fun getEndName(): String {
        return mEndHint
    }

    override fun getEndHint(): String {
        return mEndHint
    }
}

open class DefaultCheckableFilterItem(private val name: String) : CheckableFilterItem {

    private var mChecked = false

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setCheck(check: Boolean) {
        mChecked = check
    }

    override fun getName(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return FilterItem.TYPE_CHECKABLE
    }
}