@file:Suppress("unused")

package com.lolii.filter

import android.text.InputType
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author:         songtao
 * CreateDate:     2020/9/12 17:58
 */
class SimpleFilterAdapter : FilterAdapter

open class SimpleFilterGroup(private val name: String) : FilterGroup {

    var tag: Any? = null

    constructor(name: String, tag: Any) : this(name) {
        this.tag = tag
    }

    private val mItems = ArrayList<Filter>()
    private var mSingleChoice = false

    constructor(name: String, singleChoice: Boolean) : this(name) {
        this.mSingleChoice = singleChoice
    }

    constructor(name: String, items: List<Filter>) : this(name) {
        this.mItems.addAll(items)
    }

    constructor(name: String, singleChoice: Boolean, items: List<Filter>) : this(name) {
        this.mSingleChoice = singleChoice
        this.mItems.addAll(items)
    }

    fun add(item: Filter) {
        mItems.add(item)
    }

    fun addAll(items: List<Filter>) {
        mItems.addAll(items)
    }

    override fun getText(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return Filter.TYPE_GROUP
    }

    override fun getChildren(): List<Filter> {
        return mItems
    }

    fun setSingleChoice(singleChoice: Boolean) {
        mSingleChoice = singleChoice
    }

    override fun isSingleChoice(): Boolean {
        return mSingleChoice
    }

    fun getCheckedValues(): List<CheckableFilterItem> {
        val values = ArrayList<CheckableFilterItem>()
        for (item in mItems) {
            if (item is CheckableFilterItem && item.isChecked()) {
                values.add(item)
            }
        }
        return values
    }


    fun getCheckedValue(): CheckableFilterItem? {
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

    fun getEditText(): String? {
        for (item in mItems) {
            if (item is EditableFilterItem) {
                return item.getText()
            }
        }
        return null
    }

    fun getEditTextRange(): Pair<String, String>? {
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

    override fun getStartHint(): String {
        return mStartHint
    }

    override fun getEndText(): String {
        return mEndText
    }

    override fun setEndText(text: String) {
        mEndText = text
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

open class SimpleDateBoundary : DateBoundary {
    private val mCurrent: Calendar = Calendar.getInstance(TimeZone.getDefault())

    private val mMin: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(mCurrent.get(Calendar.YEAR) - 10, 1, 1)
    }

    private val mMax: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(mCurrent.get(Calendar.YEAR) + 10, 1, 1)
    }

    private var mYearOffset: Int = 0

    override fun getMinDate(): Date {
        if (mYearOffset > 0) {
            mMin.set(mCurrent.get(Calendar.YEAR) - mYearOffset, 1, 1)
            return mMin.time
        }
        return mMin.time
    }

    override fun getMaxDate(): Date {
        if (mYearOffset > 0) {
            mMax.set(mCurrent.get(Calendar.YEAR) + mYearOffset, 1, 1)
            return mMax.time
        }
        return mMax.time
    }

    fun setYearOffset(yearOffset: Int) {
        mYearOffset = yearOffset
    }
}

open class SimpleDateFilterItem(private val hint: String) : SimpleDateBoundary(), DateFilterItem {
    private var mDate: Date? = null

    override fun getDate(): Date? {
        return mDate
    }

    override fun setDate(date: Date?) {
        mDate = date
    }

    override fun getText(): String {
        return hint
    }

    override fun getHint(): String {
        return hint
    }

    override fun getType(): Int {
        return Filter.TYPE_DATE
    }
}

open class SimpleDateRangeFilterItem(private val startHint: String,
                                     private val endHint: String) : SimpleDateBoundary(), DateRangeFilterItem {
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

    override fun getStartText(): String {
        return startHint
    }

    override fun getEndText(): String {
        return endHint
    }

    override fun getStartHint(): String {
        return startHint
    }

    override fun getEndHint(): String {
        return endHint
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

    override fun getText(): String {
        return name
    }

    override fun getHint(): String {
        return name
    }

    override fun getType(): Int {
        return Filter.TYPE_CHECKABLE
    }
}