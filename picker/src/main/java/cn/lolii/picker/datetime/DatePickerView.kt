package cn.lolii.picker.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import cn.lolii.picker.NumberPickerView
import cn.lolii.picker.R
import java.util.*
import kotlin.math.min

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

    private var mYearStart = 1901
    private var mYearStop = 2036
    private var mYearCount = mYearStop - mYearStart + 1
    private var mMonthStart = 0
    private var mDayStart = 1
    private var mMonthEnd = 11
    private var mDayEnd = 31
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

    private var mOnDateChangedListener: OnDateChangedListener? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
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
        val calendar = Calendar.getInstance()
        mYearStop = calendar.get(Calendar.YEAR) + 30
        mYearCount = mYearStop - mYearStart + 1
        reset(calendar)
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
        mMonthEnd = maxValue[Calendar.MONTH]
        mDayEnd = maxValue[Calendar.DATE]
        mYearPickerView.wrapSelectorWheel = false
        mMonthPickerView.wrapSelectorWheel = false
        mDayPickerView.wrapSelectorWheel = false
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        when {
            picker === mYearPickerView -> {
                passiveUpdateMonthAndDay(oldVal, newVal)
            }
            picker === mMonthPickerView -> {
                val fixYear = mYearPickerView.value
                passiveUpdateDay(fixYear, fixYear, oldVal, newVal)
            }
            picker === mDayPickerView -> {
                mOnDateChangedListener?.onDateChanged(value)
            }
        }
    }

    fun reset(calendar: Calendar) {
        if (!checkCalendarAvailable(calendar)) {
            adjustCalendarByLimit(calendar, mYearStart, mYearStop)
        }
        setDisplayValuesForAll(calendar, false)
    }

    private fun checkCalendarAvailable(calendar: Calendar): Boolean {
        if (mYearStart > mYearStop) {
            val temp = mYearStart
            mYearStart = mYearStop
            mYearStop = temp
            mYearCount = mYearStop - mYearStart + 1
        }
        val year = calendar[Calendar.YEAR]
        return year in mYearStart..mYearStop
    }

    private fun adjustCalendarByLimit(calendar: Calendar, yearStart: Int, yearStop: Int): Calendar {
        val yearSet = calendar[Calendar.YEAR]
        Log.w(TAG, "adjustCalendarByLimit() Calendar year:$yearSet")
        if (yearSet < yearStart) {
            calendar[Calendar.YEAR] = yearStart
            calendar[Calendar.MONTH] = MONTH_START
            calendar[Calendar.DAY_OF_MONTH] = DAY_START
        }
        if (yearSet > yearStop) {
            calendar[Calendar.YEAR] = yearStop
            calendar[Calendar.MONTH] = MONTH_STOP - 1
            calendar[Calendar.DAY_OF_MONTH] = DAY_STOP
        }
        return calendar
    }

    @Suppress("SameParameterValue")
    private fun setDisplayValuesForAll(calendar: Calendar, anim: Boolean) {
        initDisplayData()
        val year = initValuesForY(calendar, anim)
        val month = initValuesForM(calendar, anim)
        initValuesForD(calendar, anim)

        passiveUpdateMonthAndDay(year, year)
        passiveUpdateDay(year, year, month, month)

        mOnDateChangedListener?.onDateChanged(value)
    }

    private fun initDisplayData() {
        if (needResetDisplayYears(mDisplayYears)) {
            mDisplayYears = MutableList(mYearCount) {
                (mYearStart + it).toString()
            }
        }
        if (mDisplayMonths.isEmpty()) {
            mDisplayMonths = MutableList(MONTH_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, MONTH_START + it)
            }
        }
        if (mDisplayDays.isEmpty()) {
            mDisplayDays = MutableList(DAY_COUNT) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, DAY_START + it)
            }
        }

        mDisplayStartMonths = if (mMonthStart != 0 ||
                mDisplayStartMonths.isEmpty() ||
                mDisplayStartMonths.size != MONTH_STOP - mMonthStart) {
            MutableList(MONTH_STOP - mMonthStart) { mDisplayMonths[mMonthStart + it] }
        } else {
            mDisplayMonths
        }

        mDisplayStartDays = if (mDayStart != 1 ||
                mDisplayStartDays.isEmpty() ||
                mDisplayStartDays.size != DAY_STOP - mDayStart + 1) {
            MutableList(DAY_STOP - mDayStart + 1) { mDisplayDays[mDayStart - 1 + it] }
        } else {
            mDisplayDays
        }

        mDisplayEndMonths = if (mMonthEnd != 11 || mDisplayEndMonths.isEmpty() || mDisplayEndMonths.size != mMonthEnd + 1) {
            MutableList(mMonthEnd + 1) { mDisplayMonths[it] }
        } else {
            mDisplayMonths
        }

        mDisplayEndDays = if (mDayEnd != 31 || mDisplayEndDays.isEmpty() || mDisplayEndDays.size != mDayEnd) {
            MutableList(mDayEnd) { mDisplayDays[it] }
        } else {
            mDisplayDays
        }
    }

    private fun needResetDisplayYears(displayYears: List<String>): Boolean {
        if (displayYears.isEmpty() || mYearCount != displayYears.size) {
            return true
        }
        if (displayYears[0] == mYearStart.toString() && displayYears[displayYears.size - 1] == mYearStop.toString()) {
            Log.d(TAG, "needResetDisplayYears() return false")
            return false
        }
        Log.d(TAG, "needResetDisplayYears() mYearStart:" + mYearStart + "," + displayYears[0]
                + " mYearStop:" + mYearStop + "," + displayYears[displayYears.size - 1])
        return true
    }

    @SuppressLint("WrongConstant")
    private fun initValuesForY(calendar: Calendar, anim: Boolean): Int {
        mYearPickerView.wrapSelectorWheel = false
        val yearSway: Int = calendar[Calendar.YEAR]
        setDisplayedValuesForPickerView(mYearPickerView, yearSway, mYearStart, mYearStop, mDisplayYears, false, anim)
        return yearSway
    }

    @SuppressLint("WrongConstant")
    private fun initValuesForM(calendar: Calendar, anim: Boolean): Int {
        val monthStop = MONTH_STOP
        val monthSway: Int = calendar[Calendar.MONTH] + 1
        setDisplayedValuesForPickerView(mMonthPickerView, monthSway, MONTH_START, monthStop, mDisplayMonths, false, anim)
        return monthSway
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return GregorianCalendar(year, month, 0)[Calendar.DATE]
    }

    private fun initValuesForD(calendar: Calendar, anim: Boolean): Int {
        val dayStop: Int = getDaysInMonth(calendar[Calendar.YEAR], calendar[Calendar.MONTH] + 1)
        val daySway: Int = calendar[Calendar.DAY_OF_MONTH]
        setDisplayedValuesForPickerView(mDayPickerView, daySway, DAY_START, dayStop, mDisplayDays, false, anim)
        return daySway
    }

    private fun setDisplayedValuesForPickerView(pickerView: NumberPickerView,
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

    @Suppress("UNUSED_PARAMETER")
    private fun passiveUpdateMonthAndDay(oldYear: Int, newYear: Int) {
        val oldMonthValue = mMonthPickerView.value
        val oldDayValue = mDayPickerView.value
        var newDayValue = oldDayValue
        var newMonthValue = oldMonthValue
        var shouldUpdateDays = false
        if (newYear == mYearStart) {
            if (oldMonthValue < mMonthStart + 1) {
                newMonthValue = mMonthStart + 1
            }
            setDisplayedValuesForPickerView(mMonthPickerView, newMonthValue, mMonthStart + 1, MONTH_STOP, mDisplayStartMonths)
            if (newMonthValue == mMonthStart + 1) {
                if (oldDayValue < mDayStart) {
                    newDayValue = mDayStart
                }
                val newDayStop = getDaysInMonth(newYear, newMonthValue)
                setDisplayedValuesForPickerView(mDayPickerView, newDayValue, mDayStart, newDayStop, mDisplayStartDays)
            } else {
                shouldUpdateDays = true
            }
        } else if (newYear == mYearStop) {
            if (oldMonthValue > mMonthEnd + 1) {
                newMonthValue = mMonthEnd + 1
            }
            setDisplayedValuesForPickerView(mMonthPickerView, newMonthValue, MONTH_START, mMonthEnd + 1, mDisplayEndMonths)
            if (newMonthValue == mMonthEnd + 1) {
                if (oldDayValue > mDayEnd) {
                    newDayValue = mDayEnd
                }
                setDisplayedValuesForPickerView(mDayPickerView, newDayValue, DAY_START, mDayEnd, mDisplayEndDays)
            } else {
                shouldUpdateDays = true
            }
        } else {
            setDisplayedValuesForPickerView(mMonthPickerView, newMonthValue, MONTH_START, MONTH_STOP, mDisplayMonths)
            shouldUpdateDays = true
        }
        if (shouldUpdateDays) {
            val newDayStop = getDaysInMonth(newYear, oldMonthValue)
            newDayValue = min(oldDayValue, newDayStop)
            setDisplayedValuesForPickerView(mDayPickerView, newDayValue, DAY_START, newDayStop, mDisplayDays)
        }
        mOnDateChangedListener?.onDateChanged(Date(newYear, newMonthValue - 1, newDayValue))
    }

    private fun passiveUpdateDay(oldYear: Int, newYear: Int, oldMonthSway: Int, newMonthValue: Int) {
        val oldDayStop = getDaysInMonth(oldYear, oldMonthSway)
        var newDayStop = getDaysInMonth(newYear, newMonthValue)
        if (newDayStop == -1) {
            return
        }
        val oldDayValue = mDayPickerView.value
        var newDayValue = oldDayValue
        if (newYear == mYearStart && newMonthValue == mMonthStart + 1) {
            if (oldDayValue < mDayStart) {
                newDayValue = mDayStart
            }
            newDayStop = getDaysInMonth(newYear, newMonthValue)
            setDisplayedValuesForPickerView(mDayPickerView, newDayValue, mDayStart, newDayStop, mDisplayStartDays)
        } else if (newYear == mYearStop && newMonthValue == mMonthEnd + 1) {
            if (oldDayValue > mDayEnd) {
                newDayValue = mDayEnd
            }
            setDisplayedValuesForPickerView(mDayPickerView, newDayValue, DAY_START, mDayEnd, mDisplayEndDays)
        } else if (oldDayStop != newDayStop || newYear == mYearStart || newYear == mYearStop) {
            newDayValue = min(oldDayValue, newDayStop)
            setDisplayedValuesForPickerView(mDayPickerView, newDayValue, DAY_START, newDayStop, mDisplayDays)
        }
        mOnDateChangedListener?.onDateChanged(Date(newYear, newMonthValue - 1, newDayValue))
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

    val yearPickerView: NumberPickerView
        get() = mYearPickerView

    val monthPickerView: NumberPickerView
        get() = mMonthPickerView

    val dayPickerView: NumberPickerView
        get() = mDayPickerView

    fun setYearPickerViewVisibility(visibility: Int) {
        setPickerViewVisibility(mYearPickerView, visibility)
    }

    fun setMonthPickerViewVisibility(visibility: Int) {
        setPickerViewVisibility(mMonthPickerView, visibility)
    }

    fun setDayPickerViewVisibility(visibility: Int) {
        setPickerViewVisibility(mDayPickerView, visibility)
    }

    private fun setPickerViewVisibility(view: NumberPickerView, visibility: Int) {
        if (view.visibility == visibility) {
            return
        }
        if (visibility == GONE || visibility == VISIBLE || visibility == INVISIBLE) {
            view.visibility = visibility
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val value: Date
        get() = Date(mYearPickerView.value, mMonthPickerView.value - 1, mDayPickerView.value)

    fun setOnDateChangedListener(listener: OnDateChangedListener?) {
        mOnDateChangedListener = listener
    }

    interface OnDateChangedListener {
        fun onDateChanged(date: Date)
    }
}