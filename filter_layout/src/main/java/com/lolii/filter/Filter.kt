package com.lolii.filter

import android.text.InputType
import android.util.ArrayMap
import android.view.View
import androidx.annotation.IntDef
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/12 17:58
 */
interface Filter {

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

        val TYPE_MAP: ArrayMap<Int, Int> = ArrayMap<Int, Int>().apply {
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

    @Target(AnnotationTarget.TYPE, AnnotationTarget.VALUE_PARAMETER)
    @IntDef(TYPE_GROUP,
            TYPE_DATE,
            TYPE_DATE_RANGE,
            TYPE_TEXT,
            TYPE_CHECKABLE,
            TYPE_NUMBER,
            TYPE_NUMBER_RANGE,
            TYPE_EDITABLE,
            TYPE_EDITABLE_RANGE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class FilterType

    /**
     * 标签类型
     */
    fun getType(): @FilterType Int
}


interface FilterItem : Filter {
    /**
     * 标签名称
     */
    fun getText(): String

    /**
     * 标签提示语
     */
    fun getHint(): String {
        return getText()
    }
}

interface RangeFilterItem : Filter {
    fun getStartText(): String
    fun getStartHint(): String {
        return getStartText()
    }

    fun getEndText(): String
    fun getEndHint(): String {
        return getEndText()
    }
}

interface FilterGroup : FilterItem {
    fun getChildren(): List<Filter>
    fun isSingleChoice() = false
}

/**
 * 日期范围限定
 */
interface DateBoundary {
    fun getMinDate(): Date
    fun getMaxDate(): Date
}

interface DateFilterItem : FilterItem, DateBoundary {
    fun setDate(date: Date?)
    fun getDate(): Date?
}

interface DateRangeFilterItem : RangeFilterItem, DateBoundary {
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

    fun setText(text: String)

    /**
     * {see android.text.InputType}
     */
    fun getInputType(): Int {
        return InputType.TYPE_CLASS_TEXT
    }
}

interface EditableRangeFilterItem : RangeFilterItem {
    fun setStartText(text: String)

    fun setEndText(text: String)

    /**
     * {see android.text.InputType}
     */
    fun getInputType(): Int {
        return InputType.TYPE_CLASS_TEXT
    }
}

interface FilterAdapter {
    /**
     * 布局文件
     */
    fun getLayoutResource(): Map<Int, Int> {
        return Filter.TYPE_MAP
    }

    /**
     * 配置显示
     */
    fun configure(filterType: Int, itemView: View, filter: Filter) {
    }
}