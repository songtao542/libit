package com.liabit.picker.datetime

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.liabit.picker.NumberPickerView
import com.liabit.picker.R
import java.util.*

@Suppress("unused")
class DatePickerView : LinearLayout, NumberPickerView.OnValueChangeListener {

    companion object {
        private const val TAG = "DatePickerView"
        private const val FORMAT_TWO_NUMBER = "%02d"
        private const val MONTH_START = 1
        private const val MONTH_STOP = 12
        private const val MONTH_COUNT = MONTH_STOP - MONTH_START + 1
        private const val DAY_START = 1
        private const val DAY_STOP = 31
        private const val DAY_COUNT = DAY_STOP - DAY_START + 1
    }

    private var mYearStart = 1900
    private var mYearStop = 2100
    private var mYearCount = mYearStop - mYearStart + 1
    private var mMonthStart = 0
    private var mMonthStop = 11
    private var mDayStart = 1
    private var mDayStop = 31
    private lateinit var mYearPickerView: NumberPickerView
    private lateinit var mMonthPickerView: NumberPickerView
    private lateinit var mDayPickerView: NumberPickerView

    /**
     * display values
     */
    private var mDisplayYears: List<String> = emptyList()
    private var mDisplayMonths: List<String> = emptyList()
    private var mDisplayDays: List<String> = emptyList()

    /**
     * 起始年-起始月
     */
    private var mDisplayStartMonths: List<String> = emptyList()

    /**
     * 起始年-起始月-起始日
     */
    private var mDisplayStartDays: List<String> = emptyList()

    /**
     * 截止年-截止月
     */
    private var mDisplayEndMonths: List<String> = emptyList()

    /**
     * 截止年-截止月-截止日
     */
    private var mDisplayEndDays: List<String> = emptyList()

    private var mCalendar = Calendar.getInstance(TimeZone.getDefault())

    private var mOnDateChangedListener: OnDateChangedListener? = null

    constructor(context: Context) : super(context) {
        initInternal(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInternal(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initInternal(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initInternal(context)
    }

    private fun initInternal(context: Context) {
        val contentView = inflate(context, R.layout.picker_date_layout, this)
        mYearPickerView = contentView.findViewById(R.id.picker_year)
        mMonthPickerView = contentView.findViewById(R.id.picker_month)
        mDayPickerView = contentView.findViewById(R.id.picker_day)
        mYearPickerView.setOnValueChangedListener(this)
        mMonthPickerView.setOnValueChangedListener(this)
        mDayPickerView.setOnValueChangedListener(this)
        mYearStop = mCalendar.get(Calendar.YEAR) + 100
        mYearCount = mYearStop - mYearStart + 1
        setupWithCalendarInternal(mCalendar)
    }

    fun setMinValue(minValue: Calendar) {
        val year = minValue[Calendar.YEAR]
        if (mYearStart == year) {
            return
        }
        mYearStart = year
        mYearCount = mYearStop - mYearStart + 1
        mMonthStart = minValue[Calendar.MONTH]
        mDayStart = minValue[Calendar.DATE]
        mYearPickerView.wrapSelectorWheel = false
        mMonthPickerView.wrapSelectorWheel = false
        mDayPickerView.wrapSelectorWheel = false
    }

    fun setMaxValue(maxValue: Calendar) {
        val year = maxValue[Calendar.YEAR]
        if (mYearStop == year) {
            return
        }
        mYearStop = year
        mYearCount = mYearStop - mYearStart + 1
        mMonthStop = maxValue[Calendar.MONTH]
        mDayStop = maxValue[Calendar.DATE]
        mYearPickerView.wrapSelectorWheel = false
        mMonthPickerView.wrapSelectorWheel = false
        mDayPickerView.wrapSelectorWheel = false
    }

    fun setupWithCalendar(calendar: Calendar) {
        mCalendar.time = calendar.time
        setupWithCalendarInternal(mCalendar)
    }

    private fun setupWithCalendarInternal(calendar: Calendar) {
        adjustCalendarByLimit(calendar)
        setDisplayValuesForAll(calendar)
    }

    private fun fixLimitIfNeeded() {
        if (mYearStart > mYearStop) {
            val temp = mYearStart
            mYearStart = mYearStop
            mYearStop = temp
            mYearCount = mYearStop - mYearStart + 1
        }
        if (mMonthStart > mMonthStop) {
            val temp = mMonthStart
            mMonthStart = mMonthStop
            mMonthStop = temp
        }
        if (mDayStart > mDayStop) {
            val temp = mDayStart
            mDayStart = mDayStop
            mDayStop = temp
        }
    }

    private fun adjustCalendarByLimit(calendar: Calendar): Calendar {
        fixLimitIfNeeded()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        if (year < mYearStart) {
            calendar[Calendar.YEAR] = mYearStart
        }
        if (year > mYearStop) {
            calendar[Calendar.YEAR] = mYearStop
        }
        if (month < mMonthStart) {
            calendar[Calendar.MONTH] = mMonthStart
        }
        if (month > mMonthStop) {
            calendar[Calendar.MONTH] = mMonthStop
        }
        if (day < mDayStart) {
            calendar[Calendar.DAY_OF_MONTH] = mDayStart
        }
        if (day > mMonthStop) {
            calendar[Calendar.DAY_OF_MONTH] = mDayStop
        }
        return calendar
    }

    private fun setDisplayValuesForAll(calendar: Calendar) {
        initDisplayData()

        val year: Int = calendar[Calendar.YEAR]
        var month: Int = calendar[Calendar.MONTH]
        month = if (month in mMonthStart..mMonthStop) month else mMonthStart
        var day: Int = calendar[Calendar.DAY_OF_MONTH]
        day = if (day in mDayStart..mDayStop) day else mDayStart

        setupDisplayedValues(mYearPickerView, year, mYearStart, mYearStop, mDisplayYears, false, anim = false)

        passiveUpdateMonth(year)
        passiveUpdateDay(year, month + 1)
        mDayPickerView.value = day

        mOnDateChangedListener?.onDateChanged(value)
    }

    private fun initDisplayData() {
        if (needResetDisplayYears(mDisplayYears)) {
            mDisplayYears = MutableList(mYearCount) {
                (mYearStart + it).toString()
            }
        }
        // 满 月 数组
        if (mDisplayMonths.isEmpty()) {
            mDisplayMonths = MutableList(MONTH_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, MONTH_START + it)
            }
        }
        // 满 天 数组
        if (mDisplayDays.isEmpty()) {
            mDisplayDays = MutableList(DAY_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, DAY_START + it)
            }
        }

        // 开始临界点 月份 数组
        mDisplayStartMonths = if (mMonthStart != 0 || mDisplayStartMonths.size != MONTH_STOP - mMonthStart) {
            MutableList(MONTH_STOP - mMonthStart) { mDisplayMonths[mMonthStart + it] }
        } else {
            mDisplayMonths
        }

        // 开始临界点 天 数组
        mDisplayStartDays = if (mDayStart != 1 || mDisplayStartDays.size != DAY_STOP - mDayStart + 1) {
            MutableList(DAY_STOP - mDayStart + 1) { mDisplayDays[mDayStart - 1 + it] }
        } else {
            mDisplayDays
        }

        // 截止临界点 月份 数组
        mDisplayEndMonths = if (mMonthStop != 11 || mDisplayEndMonths.size != mMonthStop + 1) {
            MutableList(mMonthStop + 1) { mDisplayMonths[it] }
        } else {
            mDisplayMonths
        }

        // 截止临界点 天 数组
        mDisplayEndDays = if (mDayStop != 31 || mDisplayEndDays.size != mDayStop) {
            MutableList(mDayStop) { mDisplayDays[it] }
        } else {
            mDisplayDays
        }
    }

    private fun needResetDisplayYears(displayYears: List<String>): Boolean {
        if (displayYears.isEmpty() || mYearCount != displayYears.size) {
            return true
        }
        if (displayYears[0] == mYearStart.toString() && displayYears[displayYears.size - 1] == mYearStop.toString()) {
            Log.d(TAG, "needResetDisplayYears: false")
            return false
        }
        Log.d(TAG, "needResetDisplayYears mYearStart:$mYearStart ${displayYears[0]} mYearStop:$mYearStop ${displayYears[displayYears.size - 1]}")
        return true
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return GregorianCalendar(year, month, 0)[Calendar.DATE]
    }

    private fun setupDisplayedValues(pickerView: NumberPickerView,
                                     newValue: Int,
                                     newStart: Int,
                                     newStop: Int,
                                     newDisplayedVales: List<String>,
                                     needRespond: Boolean = true,
                                     anim: Boolean = true) {
        if (newStart > newStop) { //规避一些错误
            Log.w(TAG, "setValuesForPickerView() newStart > newStop")
            return
        }
        require(newDisplayedVales.isNotEmpty()) { "newDisplayedVales's length should not be 0." }
        val newCount = newStop - newStart + 1
        require(newDisplayedVales.size >= newCount) { "newDisplayedVales's length should not be less than newCount." }
        val oldStart = pickerView.minValue
        val oldStop = pickerView.maxValue
        val oldCount = oldStop - oldStart + 1
        var fromValue = pickerView.value
        if (newCount > oldCount) {
            pickerView.displayedValues = newDisplayedVales
            pickerView.minValue = newStart
            pickerView.maxValue = newStop
        } else {
            pickerView.minValue = newStart
            pickerView.maxValue = newStop
            pickerView.displayedValues = newDisplayedVales
        }
        if (anim) {
            if (fromValue < newStart) {
                fromValue = newStart
            }
            pickerView.smoothScrollToValue(fromValue, newValue, needRespond)
        } else {
            pickerView.value = newValue
        }
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        Log.d(TAG, "onValueChange() ${ViewId.getViewId(picker)}  $oldVal -> $newVal")
        when {
            picker === mYearPickerView -> {
                mCalendar[Calendar.YEAR] = newVal
                passiveUpdateMonth(newVal)
            }
            picker === mMonthPickerView -> {
                mCalendar[Calendar.MONTH] = newVal - 1
                passiveUpdateDay(mYearPickerView.value, newVal)
            }
            picker === mDayPickerView -> {
                mCalendar[Calendar.DAY_OF_MONTH] = newVal
            }
        }
        mOnDateChangedListener?.onDateChanged(value)
    }

    private fun passiveUpdateMonth(year: Int) {
        val month = mMonthPickerView.value
        var updatedMonth = month
        when (year) {
            mYearStart -> {
                if (month < mMonthStart + 1) {
                    updatedMonth = mMonthStart + 1
                }
                setupDisplayedValues(mMonthPickerView, updatedMonth, mMonthStart + 1, MONTH_STOP, mDisplayStartMonths)
            }
            mYearStop -> {
                if (month > mMonthStop + 1) {
                    updatedMonth = mMonthStop + 1
                }
                setupDisplayedValues(mMonthPickerView, updatedMonth, MONTH_START, mMonthStop + 1, mDisplayEndMonths)
            }
            else -> {
                setupDisplayedValues(mMonthPickerView, updatedMonth, MONTH_START, MONTH_STOP, mDisplayMonths)
            }
        }
        mCalendar[Calendar.MONTH] = updatedMonth - 1
        passiveUpdateDay(year, updatedMonth)
    }

    private fun passiveUpdateDay(year: Int, month: Int) {
        val day = mDayPickerView.value
        var updatedDay = day
        if (year == mYearStart && month == mMonthStart + 1) {
            if (day < mDayStart) {
                updatedDay = mDayStart
            }
            val dayStop = getDaysInMonth(year, month)
            setupDisplayedValues(mDayPickerView, updatedDay, mDayStart, dayStop, mDisplayStartDays, anim = false)
        } else if (year == mYearStop && month == mMonthStop + 1) {
            if (day > mDayStop) {
                updatedDay = mDayStop
            }
            setupDisplayedValues(mDayPickerView, updatedDay, DAY_START, mDayStop, mDisplayEndDays, anim = false)
        } else {
            val dayStop = getDaysInMonth(year, month)
            if (updatedDay > dayStop) {
                updatedDay = dayStop
            }
            setupDisplayedValues(mDayPickerView, updatedDay, DAY_START, dayStop, mDisplayDays, anim = false)
        }
        mCalendar[Calendar.DAY_OF_MONTH] = updatedDay
    }

    fun setTextColor(selectedColor: Int, normalColor: Int) {
        setSelectedColor(selectedColor)
        setNormalColor(normalColor)
    }

    private fun setSelectedColor(selectedColor: Int) {
        mYearPickerView.setSelectedTextColor(selectedColor)
        mYearPickerView.setHintTextColor(selectedColor)
        mMonthPickerView.setSelectedTextColor(selectedColor)
        mMonthPickerView.setHintTextColor(selectedColor)
        mDayPickerView.setSelectedTextColor(selectedColor)
        mDayPickerView.setHintTextColor(selectedColor)
    }

    private fun setNormalColor(normalColor: Int) {
        mYearPickerView.setNormalTextColor(normalColor)
        mMonthPickerView.setNormalTextColor(normalColor)
        mDayPickerView.setNormalTextColor(normalColor)
    }

    fun setDividerColor(dividerColor: Int) {
        mYearPickerView.setDividerColor(dividerColor)
        mMonthPickerView.setDividerColor(dividerColor)
        mDayPickerView.setDividerColor(dividerColor)
    }

    val yearPickerView: NumberPickerView get() = mYearPickerView

    val monthPickerView: NumberPickerView get() = mMonthPickerView

    val dayPickerView: NumberPickerView get() = mDayPickerView

    val value: Date get() = Date.from(mCalendar)

    fun setOnDateChangedListener(listener: OnDateChangedListener?) {
        mOnDateChangedListener = listener
    }

    interface OnDateChangedListener {
        fun onDateChanged(date: Date)
    }
}