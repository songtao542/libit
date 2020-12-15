@file:Suppress("unused")

package com.liabit.filter

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

    fun getEditText(): CharSequence? {
        for (item in mItems) {
            if (item is EditableFilterItem) {
                return item.getText()
            }
        }
        return null
    }

    fun getEditTextRange(): Pair<CharSequence, CharSequence>? {
        for (item in mItems) {
            if (item is EditableRangeFilterItem) {
                return Pair(item.getStartText(), item.getEndText())
            }
        }
        return null
    }
}

open class SimpleEditableFilterItem(private var mHint: String) : EditableFilterItem {
    private var mText: CharSequence = ""

    override fun getText(): CharSequence {
        return mText
    }

    override fun setText(text: CharSequence) {
        mText = text
    }

    override fun getHint(): CharSequence {
        return mHint
    }
}

open class SimpleEditableRangeFilterItem(
        private var mStartHint: String = "",
        private var mEndHint: String = ""
) : EditableRangeFilterItem {

    private var mInputType: Int = InputType.TYPE_CLASS_TEXT
    private var mStartText: CharSequence = ""
    private var mEndText: CharSequence = ""

    override fun getStartText(): CharSequence {
        return mStartText
    }

    override fun setStartText(text: CharSequence) {
        mStartText = text
    }

    override fun getStartHint(): CharSequence {
        return mStartHint
    }

    override fun getEndText(): CharSequence {
        return mEndText
    }

    override fun setEndText(text: CharSequence) {
        mEndText = text
    }

    override fun getEndHint(): CharSequence {
        return mEndHint
    }

    fun setInputType(inputType: Int) {
        mInputType = inputType
    }

    override fun getInputType(): Int {
        return mInputType
    }
}

open class SimpleDateBoundary : Boundary<Date> {
    private val mCurrent: Calendar = Calendar.getInstance(TimeZone.getDefault())

    private val mMin: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(mCurrent.get(Calendar.YEAR) - 10, 1, 1)
    }

    private val mMax: Calendar = Calendar.getInstance(TimeZone.getDefault()).apply {
        set(mCurrent.get(Calendar.YEAR) + 10, 1, 1)
    }

    private var mYearOffset: Int = 0

    override fun getMin(): Date {
        if (mYearOffset > 0) {
            mMin.set(mCurrent.get(Calendar.YEAR) - mYearOffset, 1, 1)
            return mMin.time
        }
        return mMin.time
    }

    override fun getMax(): Date {
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

    override fun getText(): CharSequence {
        return hint
    }

    override fun getHint(): CharSequence {
        return hint
    }
}

open class SimpleDateRangeFilterItem(
        private val startHint: String,
        private val endHint: String
) : SimpleDateBoundary(), DateRangeFilterItem {
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

    override fun getStartText(): CharSequence {
        return startHint
    }

    override fun getEndText(): CharSequence {
        return endHint
    }

    override fun getStartHint(): CharSequence {
        return startHint
    }

    override fun getEndHint(): CharSequence {
        return endHint
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

    override fun getText(): CharSequence {
        return name
    }

    override fun getHint(): CharSequence {
        return name
    }
}

open class SimpleNumberBoundary(private val min: Int, private val max: Int) : Boundary<Int> {
    override fun getMin(): Int {
        return min
    }

    override fun getMax(): Int {
        return max
    }
}

open class SimpleNumberFilterItem(private val hint: String, min: Int, max: Int)
    : SimpleNumberBoundary(min, max), NumberFilterItem {
    private var mNumber: Int? = null

    override fun setNumber(number: Int?) {
        mNumber = number
    }

    override fun getNumber(): Int? {
        return mNumber
    }

    override fun getText(): CharSequence {
        return hint
    }
}

open class SimpleNumberRangeFilterItem(private val startHint: String, private val endHint: String, min: Int, max: Int)
    : SimpleNumberBoundary(min, max), NumberRangeFilterItem {
    private var mStartNumber: Int? = null
    private var mEndNumber: Int? = null

    override fun setStartNumber(number: Int?) {
        mStartNumber = number
    }

    override fun getStartNumber(): Int? {
        return mStartNumber
    }

    override fun setEndNumber(number: Int?) {
        mEndNumber = number
    }

    override fun getEndNumber(): Int? {
        return mEndNumber
    }

    override fun getStartText(): CharSequence {
        return startHint
    }

    override fun getEndText(): CharSequence {
        return endHint
    }
}

open class SimpleAddressFilterItem(private val hint: String) : AddressFilterItem {

    private var mAddress: Address? = null

    override fun setAddress(address: Address?) {
        mAddress = address
    }

    override fun getAddress(): Address? {
        return mAddress
    }

    override fun getText(): CharSequence {
        return hint
    }

    override fun getHint(): CharSequence {
        return hint
    }
}
