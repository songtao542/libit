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
class DateTimePickerView : LinearLayout, NumberPickerView.OnValueChangeListener {

    companion object {
        private const val TAG = "DateTimePickerView"
        private const val FORMAT_TWO_NUMBER = "%02d"

        // 此处定义的值都是显示时的值
        private const val MONTH_START = 1
        private const val MONTH_STOP = 12
        private const val MONTH_COUNT = MONTH_STOP - MONTH_START + 1
        private const val DAY_START = 1
        private const val DAY_STOP = 31
        private const val DAY_COUNT = DAY_STOP - DAY_START + 1
        private const val HOUR_START = 0
        private const val HOUR_STOP = 23
        private const val HOUR_COUNT = HOUR_STOP - HOUR_START + 1
        private const val MINUTE_START = 0
        private const val MINUTE_STOP = 59
        private const val MINUTE_COUNT = MINUTE_STOP - MINUTE_START + 1
    }

    private var mYearStart = 1900
    private var mYearStop = 2100
    private var mYearCount = mYearStop - mYearStart + 1
    private var mMonthStart = 0
    private var mMonthStop = 11
    private var mDayStart = 1
    private var mDayStop = 31
    private var mHourStart = 0
    private var mHourStop = 23
    private var mMinuteStart = 0
    private var mMinuteStop = 59

    private lateinit var mYearPickerView: NumberPickerView
    private lateinit var mMonthPickerView: NumberPickerView
    private lateinit var mDayPickerView: NumberPickerView
    private lateinit var mHourPickerView: NumberPickerView
    private lateinit var mMinutePickerView: NumberPickerView
    private lateinit var mTimeDividerView: NumberPickerView

    /**
     * display values
     */
    private var mDisplayYears: List<String> = emptyList()
    private var mDisplayMonths: List<String> = emptyList()
    private var mDisplayDays: List<String> = emptyList()
    private var mDisplayHours: List<String> = emptyList()
    private var mDisplayMinutes: List<String> = emptyList()

    /** 起始年-起始月 */
    private var mDisplayStartMonths: List<String> = emptyList()

    /** 起始年-起始月-起始日 */
    private var mDisplayStartDays: List<String> = emptyList()

    /** 起始年-起始月-起始日-起始小时 */
    private var mDisplayStartHours: List<String> = emptyList()

    /** 起始年-起始月-起始日-起始小时-起始分钟 */
    private var mDisplayStartMinutes: List<String> = emptyList()

    /** 截止年-截止月 */
    private var mDisplayStopMonths: List<String> = emptyList()

    /** 截止年-截止月-截止日 */
    private var mDisplayStopDays: List<String> = emptyList()

    /** 截止年-截止月-截止日-截止小时 */
    private var mDisplayStopHours: List<String> = emptyList()

    /** 截止年-截止月-截止日-截止小时-截止分钟 */
    private var mDisplayStopMinutes: List<String> = emptyList()

    private var mOnDateTimeChangedListener: OnDateTimeChangedListener? = null

    private val mCalendar = Calendar.getInstance(TimeZone.getDefault())

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
        val contentView = inflate(context, R.layout.picker_date_time_layout, this)
        mYearPickerView = contentView.findViewById(R.id.picker_year)
        mMonthPickerView = contentView.findViewById(R.id.picker_month)
        mDayPickerView = contentView.findViewById(R.id.picker_day)

        mHourPickerView = contentView.findViewById(R.id.picker_hour)
        mTimeDividerView = contentView.findViewById(R.id.picker_time_divider)
        mMinutePickerView = contentView.findViewById(R.id.picker_min)

        mYearPickerView.setOnValueChangedListener(this)
        mMonthPickerView.setOnValueChangedListener(this)
        mDayPickerView.setOnValueChangedListener(this)
        mHourPickerView.setOnValueChangedListener(this)
        mMinutePickerView.setOnValueChangedListener(this)

        mTimeDividerView.setPickedIndexRelativeToMin(1)
        mTimeDividerView.isEnabled = false
        mTimeDividerView.setOffsetY(6)

        mYearStop = mCalendar.get(Calendar.YEAR) + 100
        mYearCount = mYearStop - mYearStart + 1
        setupWithCalendarInternal(mCalendar)
    }

    fun setMinValue(minValue: Calendar) {
        mYearStart = minValue[Calendar.YEAR]
        mMonthStart = minValue[Calendar.MONTH]
        mDayStart = minValue[Calendar.DATE]
        mHourStart = minValue[Calendar.HOUR_OF_DAY]
        mMinuteStart = minValue[Calendar.MINUTE]
        mYearPickerView.wrapSelectorWheel = false
        mMonthPickerView.wrapSelectorWheel = false
        mDayPickerView.wrapSelectorWheel = false
        mHourPickerView.wrapSelectorWheel = false
        mMinutePickerView.wrapSelectorWheel = false

        configTimeDividerView()
    }

    fun setMaxValue(maxValue: Calendar) {
        mYearStop = maxValue[Calendar.YEAR]
        mMonthStop = maxValue[Calendar.MONTH]
        mDayStop = maxValue[Calendar.DATE]
        mHourStop = maxValue[Calendar.HOUR_OF_DAY]
        mMinuteStop = maxValue[Calendar.MINUTE]
        mYearPickerView.wrapSelectorWheel = false
        mMonthPickerView.wrapSelectorWheel = false
        mDayPickerView.wrapSelectorWheel = false
        mHourPickerView.wrapSelectorWheel = false
        mMinutePickerView.wrapSelectorWheel = false

        configTimeDividerView()
    }

    private fun configTimeDividerView() {
        mTimeDividerView.displayedValues = resources.getStringArray(R.array.hour_time_limit_divider).toList()
        mTimeDividerView.minValue = 0
        mTimeDividerView.maxValue = 2
        mTimeDividerView.value = 1
    }

    private fun setupWithCalendarInternal(calendar: Calendar) {
        adjustCalendarByLimit(calendar)
        setDisplayValuesForAll(calendar)
    }

    fun setupWithCalendar(calendar: Calendar) {
        mCalendar.time = calendar.time
        setupWithCalendarInternal(mCalendar)
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
        if (mHourStart > mHourStop) {
            val temp = mHourStart
            mHourStart = mHourStop
            mHourStop = temp
        }
        if (mMinuteStart > mMinuteStop) {
            val temp = mMinuteStart
            mMinuteStart = mMinuteStop
            mMinuteStop = temp
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

        var dayOfMonth: Int = calendar[Calendar.DAY_OF_MONTH]
        dayOfMonth = if (dayOfMonth in mDayStart..mDayStop) dayOfMonth else mDayStart

        var hourOfDay: Int = calendar[Calendar.HOUR_OF_DAY]
        hourOfDay = if (hourOfDay in mHourStart..mHourStop) hourOfDay else mHourStart

        var minute: Int = calendar[Calendar.MINUTE]
        minute = if (minute in mMinuteStart..mMinuteStop) minute else mMinuteStart

        // 设置 年 选择器数据
        setupDisplayedValues(mYearPickerView, year, mYearStart, mYearStop, mDisplayYears, false, anim = false)

        passiveUpdateMonth(year)
        passiveUpdateDay(year, month + 1)
        passiveUpdateHour(year, month + 1, dayOfMonth)
        passiveUpdateMinute(year, month + 1, dayOfMonth, hourOfDay)
        mMinutePickerView.value = minute

        mOnDateTimeChangedListener?.onDateTimeChanged(value)
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
        // 满 小时 数组
        if (mDisplayHours.isEmpty()) {
            mDisplayHours = MutableList(HOUR_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, HOUR_START + it)
            }
        }
        // 满 分钟 数组
        if (mDisplayMinutes.isEmpty()) {
            mDisplayMinutes = MutableList(MINUTE_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, MINUTE_START + it)
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
        // 开始临界点 小时 数组
        mDisplayStartHours = if (mHourStart != 0 || mDisplayStartHours.size != HOUR_STOP - mHourStart + 1) {
            MutableList(HOUR_STOP - mHourStart + 1) { mDisplayHours[mHourStart + it] }
        } else {
            mDisplayHours
        }
        // 开始临界点 分钟 数组
        mDisplayStartMinutes = if (mMinuteStart != 0 || mDisplayStartMinutes.size != HOUR_STOP - mMinuteStart + 1) {
            MutableList(MINUTE_STOP - mMinuteStart + 1) { mDisplayMinutes[mMinuteStart + it] }
        } else {
            mDisplayMinutes
        }

        // 截止临界点 月份 数组
        mDisplayStopMonths = if (mMonthStop != 11 || mDisplayStopMonths.size != mMonthStop + 1) {
            MutableList(mMonthStop + 1) { mDisplayMonths[it] }
        } else {
            mDisplayMonths
        }
        // 截止临界点 天 数组
        mDisplayStopDays = if (mDayStop != 31 || mDisplayStopDays.size != mDayStop) {
            MutableList(mDayStop) { mDisplayDays[it] }
        } else {
            mDisplayDays
        }
        // 截止临界点 小时 数组
        mDisplayStopHours = if (mHourStop != 23 || mDisplayStopHours.size != mHourStop + 1) {
            MutableList(mHourStop + 1) { mDisplayHours[it] }
        } else {
            mDisplayHours
        }
        // 截止临界点 分钟 数组
        mDisplayStopMinutes = if (mMinuteStop != 31 || mDisplayStopMinutes.size != mMinuteStop + 1) {
            MutableList(mMinuteStop + 1) { mDisplayMinutes[it] }
        } else {
            mDisplayMinutes
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

    //private val mFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm") }

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
                passiveUpdateHour(mYearPickerView.value, mMonthPickerView.value, newVal)
            }
            picker === mHourPickerView -> {
                mCalendar[Calendar.HOUR_OF_DAY] = newVal
                passiveUpdateMinute(mYearPickerView.value, mMonthPickerView.value, mDayPickerView.value, newVal)
            }
            picker === mMinutePickerView -> {
                mCalendar[Calendar.MINUTE] = newVal
            }
        }
        //Log.d(TAG, "datetime: ${mFormat.format(mCalendar.time)}    ${mFormat.format(value.calendar.time)}")
        mOnDateTimeChangedListener?.onDateTimeChanged(value)
    }

    private fun passiveUpdateMinute(year: Int, month: Int, day: Int, hour: Int) {
        val minute = mMinutePickerView.value
        var updatedMinute = minute
        if (year == mYearStart && month == mMonthStart + 1 && day == mDayStart && hour == mHourStart) {
            if (minute < mMinuteStart) {
                updatedMinute = mMinuteStart
            }
            setupDisplayedValues(mMinutePickerView, updatedMinute, mMinuteStart, MINUTE_STOP, mDisplayStartMinutes)
        } else if (year == mYearStop && month == mMonthStop + 1 && day == mDayStop && hour == mHourStop) {
            if (minute > mMinuteStop) {
                updatedMinute = mMinuteStop
            }
            setupDisplayedValues(mMinutePickerView, updatedMinute, MINUTE_START, mMinuteStop, mDisplayStopMinutes)
        } else {
            setupDisplayedValues(mMinutePickerView, updatedMinute, MINUTE_START, MINUTE_STOP, mDisplayMinutes)
        }
        mCalendar[Calendar.MINUTE] = updatedMinute
    }

    private fun passiveUpdateHour(year: Int, month: Int, day: Int) {
        val hour = mHourPickerView.value
        var updatedHour = hour
        if (year == mYearStart && month == mMonthStart + 1 && day == mDayStart) {
            if (hour < mHourStart) {
                updatedHour = mHourStart
            }
            setupDisplayedValues(mHourPickerView, updatedHour, mHourStart, HOUR_STOP, mDisplayStartHours)
        } else if (year == mYearStop && month == mMonthStop + 1 && day == mDayStop) {
            if (hour > mHourStop) {
                updatedHour = mHourStop
            }
            setupDisplayedValues(mHourPickerView, updatedHour, HOUR_START, mHourStop, mDisplayStopHours)
        } else {
            setupDisplayedValues(mHourPickerView, updatedHour, HOUR_START, HOUR_STOP, mDisplayHours)
        }
        mCalendar[Calendar.HOUR_OF_DAY] = updatedHour
        passiveUpdateMinute(year, month, day, updatedHour)
    }

    private fun passiveUpdateDay(year: Int, month: Int) {
        val day = mDayPickerView.value
        var updatedDay = day
        if (year == mYearStart && month == mMonthStart + 1) {
            if (day < mDayStart) {
                updatedDay = mDayStart
            }
            val newDayStop = getDaysInMonth(year, month)
            setupDisplayedValues(mDayPickerView, updatedDay, mDayStart, newDayStop, mDisplayStartDays)
        } else if (year == mYearStop && month == mMonthStop + 1) {
            if (day > mDayStop) {
                updatedDay = mDayStop
            }
            setupDisplayedValues(mDayPickerView, updatedDay, DAY_START, mDayStop, mDisplayStopDays)
        } else {
            setupDisplayedValues(mDayPickerView, updatedDay, DAY_START, DAY_STOP, mDisplayDays)
        }
        mCalendar[Calendar.DAY_OF_MONTH] = updatedDay
        passiveUpdateHour(year, month, updatedDay)
    }

    private fun passiveUpdateMonth(year: Int) {
        val month = mMonthPickerView.value
        var updatedMonth = month
        when (year) {
            mYearStart -> {
                if (month < mMonthStart + 1) {
                    updatedMonth = mMonthStart + 1
                }
                setupDisplayedValues(mMonthPickerView, updatedMonth, mMonthStart + 1, MONTH_STOP, mDisplayStartMonths, anim = false)
            }
            mYearStop -> {
                if (month > mMonthStop + 1) {
                    updatedMonth = mMonthStop + 1
                }
                setupDisplayedValues(mMonthPickerView, updatedMonth, MONTH_START, mMonthStop + 1, mDisplayStopMonths, anim = false)
            }
            else -> {
                setupDisplayedValues(mMonthPickerView, updatedMonth, MONTH_START, MONTH_STOP, mDisplayMonths, anim = false)
            }
        }
        mCalendar[Calendar.MONTH] = updatedMonth - 1
        passiveUpdateDay(year, updatedMonth)
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
        mHourPickerView.setSelectedTextColor(selectedColor)
        mHourPickerView.setHintTextColor(selectedColor)
        mMinutePickerView.setSelectedTextColor(selectedColor)
        mMinutePickerView.setHintTextColor(selectedColor)
    }

    private fun setNormalColor(normalColor: Int) {
        mYearPickerView.setNormalTextColor(normalColor)
        mMonthPickerView.setNormalTextColor(normalColor)
        mDayPickerView.setNormalTextColor(normalColor)
        mHourPickerView.setNormalTextColor(normalColor)
        mMinutePickerView.setNormalTextColor(normalColor)
    }

    fun setDividerColor(dividerColor: Int) {
        mYearPickerView.setDividerColor(dividerColor)
        mMonthPickerView.setDividerColor(dividerColor)
        mDayPickerView.setDividerColor(dividerColor)
        mHourPickerView.setDividerColor(dividerColor)
        mMinutePickerView.setDividerColor(dividerColor)
    }

    val yearPickerView: NumberPickerView get() = mYearPickerView

    val monthPickerView: NumberPickerView get() = mMonthPickerView

    val dayPickerView: NumberPickerView get() = mDayPickerView

    val hourPickerView: NumberPickerView get() = mHourPickerView

    val minutePickerView: NumberPickerView get() = mMinutePickerView

    val value: DateTime get() = DateTime.from(mCalendar)

    fun setOnDateTimeChangedListener(listener: OnDateTimeChangedListener?) {
        mOnDateTimeChangedListener = listener
    }

    interface OnDateTimeChangedListener {
        fun onDateTimeChanged(dateTime: DateTime)
    }
}