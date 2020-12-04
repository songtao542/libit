package com.liabit.picker.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.format.DateFormat
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.liabit.picker.NumberPickerView
import com.liabit.picker.R
import java.util.*

@Suppress("unused")
class TimePickerView : LinearLayout, NumberPickerView.OnValueChangeListener {
    companion object {
        private const val TAG = "TimePickerView"
        private const val FORMAT_TWO_NUMBER = "%02d"
    }

    private lateinit var mAmPmPickerView: NumberPickerView
    private lateinit var mHourPickerView: NumberPickerView
    private lateinit var mMinutePickerView: NumberPickerView
    private lateinit var mTimeDividerView: NumberPickerView

    private var mCalendar: Calendar = Calendar.getInstance()
    private var mIs24HourFormat = true
    private var mLastIs24HourFormat: Boolean? = null
    private var mAuto24Hour = true //自动根据系统24小时显示不同view
    private var mDisplayHour: List<Hour> = emptyList()

    private var mDisplayMinute: List<CharSequence> = emptyList()

    private var mDisplayStartMinute: List<CharSequence> = emptyList()
    private var mDisplayStopMinute: List<CharSequence> = emptyList()

    private var mOnTimeChangeListener: OnTimeChangedListener? = null
    private var mOnWindowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null

    private var mHourStart = 0
    private var mHourStop = 23
    private var mMinuteStart = 0
    private var mMinuteStop = 59

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
        val inflate = inflate(context, R.layout.picker_time_layout, this)
        mAmPmPickerView = inflate.findViewById(R.id.picker_amPm)
        mTimeDividerView = inflate.findViewById(R.id.picker_time_divider)
        mHourPickerView = inflate.findViewById(R.id.picker_hour)
        mMinutePickerView = inflate.findViewById(R.id.picker_min)
        mAmPmPickerView.setOnValueChangedListener(this)
        mHourPickerView.setOnValueChangedListener(this)
        mMinutePickerView.setOnValueChangedListener(this)
        mTimeDividerView.setPickedIndexRelativeToMin(1)
        mTimeDividerView.isEnabled = false
        mTimeDividerView.setOffsetY(6)
        mIs24HourFormat = DateFormat.is24HourFormat(context)
        setupWithCalendarInternal(mCalendar)
    }

    fun setItemWrapContent() {
        val params = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mAmPmPickerView.layoutParams = params
        mHourPickerView.layoutParams = params
        mMinutePickerView.layoutParams = params
    }

    fun setItemPadding(paddingPx: Int) {
        mAmPmPickerView.setPadding(paddingPx, 0, paddingPx, 0)
        mHourPickerView.setPadding(paddingPx, 0, paddingPx, 0)
        mMinutePickerView.setPadding(paddingPx, 0, paddingPx, 0)
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (mOnWindowFocusChangeListener == null) {
                mOnWindowFocusChangeListener = ViewTreeObserver.OnWindowFocusChangeListener { hasFocus: Boolean ->
                    if (!hasFocus || !mAuto24Hour) {
                        return@OnWindowFocusChangeListener
                    }
                    val is24Hour = DateFormat.is24HourFormat(context)
                    Log.d(TAG, "onWindowFocusChanged() hasFocus is24Hour:$is24Hour")
                    if (mIs24HourFormat != is24Hour) {
                        mIs24HourFormat = is24Hour
                        setupWithCalendarInternal(mCalendar)
                    }
                }
                viewTreeObserver.addOnWindowFocusChangeListener(mOnWindowFocusChangeListener)
            }
        }
    }

    fun setTime(hour: Int, minute: Int) {
        mCalendar[Calendar.HOUR_OF_DAY] = hour
        mCalendar[Calendar.MINUTE] = minute
        setupWithCalendarInternal(mCalendar)
    }

    /**
     * @param hour 12-hour clock (0 - 11).  0 在12小时制显示为12
     * @param amPm [Calendar.AM] or [Calendar.PM]
     */
    fun setTime(hour: Int, minute: Int, amPm: Int) {
        mCalendar[Calendar.HOUR] = hour
        mCalendar[Calendar.MINUTE] = minute
        if (amPm == Calendar.AM || amPm == Calendar.PM) {
            mCalendar[Calendar.AM_PM] = amPm
        }
        setupWithCalendarInternal(mCalendar)
    }

    /**
     * @param hourOfDay 0-23
     * @param minute 0-59
     */
    fun setMin(hourOfDay: Int, minute: Int) {
        mHourStart = if (hourOfDay in 0..mHourStop) hourOfDay else 0
        mMinuteStart = if (minute in 0..mMinuteStop) minute else 0
    }

    /**
     * @param hourOfDay 0-23
     * @param minute 0-59
     */
    fun setMax(hourOfDay: Int, minute: Int) {
        mHourStop = if (hourOfDay in mHourStart..23) hourOfDay else 23
        mMinuteStop = if (minute in mMinuteStart..59) minute else 59
    }

    fun set24HourFormat(is24HourFormat: Boolean) {
        mAuto24Hour = false
        if (mIs24HourFormat == is24HourFormat) {
            return
        }
        mIs24HourFormat = is24HourFormat
        setupWithCalendarInternal(mCalendar)
    }

    fun is24HourFormat(): Boolean {
        return mIs24HourFormat
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
        if (mHourStop < mHourStart) {
            val start = mHourStart
            mHourStart = mHourStop
            mHourStop = start
        }
        if (mMinuteStop < mMinuteStart) {
            val start = mMinuteStart
            mMinuteStart = mMinuteStop
            mMinuteStop = start
        }
    }

    private fun adjustCalendarByLimit(calendar: Calendar): Calendar {
        fixLimitIfNeeded()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]
        if (hour < mHourStart) {
            calendar[Calendar.HOUR_OF_DAY] = mHourStart
        }
        if (hour > mHourStop) {
            calendar[Calendar.HOUR_OF_DAY] = mHourStop
        }
        if (minute < mMinuteStart) {
            calendar[Calendar.MINUTE] = mMinuteStart
        }
        if (minute > mMinuteStop) {
            calendar[Calendar.MINUTE] = mMinuteStop
        }
        return calendar
    }

    private fun initDisplayData() {
        // 满 小时 数组
        if (mLastIs24HourFormat != mIs24HourFormat || mDisplayHour.size != (mHourStop - mHourStart + 1)) {
            // 24小时制
            mLastIs24HourFormat = mIs24HourFormat
            mDisplayHour = MutableList(mHourStop - mHourStart + 1) {
                Hour(mIs24HourFormat, mHourStart + it)
            }
        }
        // 满 分钟 数组
        if (mDisplayMinute.isEmpty()) {
            mDisplayMinute = MutableList(60) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, it)
            }
        }

        // 开始临界点 分钟 数组
        mDisplayStartMinute = if (mMinuteStart != 0 || mDisplayStartMinute.size != 60 - mMinuteStart) {
            MutableList(60 - mMinuteStart) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, (mMinuteStart + it))
            }
        } else {
            mDisplayMinute
        }
        // 截止临界点 分钟 数组
        mDisplayStopMinute = if (mMinuteStop != 59 || mDisplayStopMinute.size != mMinuteStop + 1) {
            MutableList(mMinuteStop + 1) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, it)
            }
        } else {
            mDisplayMinute
        }
    }

    private fun setDisplayValuesForAll(calendar: Calendar) {
        initDisplayData()
        if (mIs24HourFormat) {
            // 24小时制
            mAmPmPickerView.visibility = GONE
            mTimeDividerView.visibility = VISIBLE
        } else {
            // 12小时制
            mAmPmPickerView.visibility = VISIBLE
            mTimeDividerView.visibility = GONE
            val apm = context.resources.getStringArray(R.array.picker_am_pm_entries)
            mAmPmPickerView.setDisplayedValues(arrayListOf(*apm), false)
            initPickerViewValue(mAmPmPickerView, 0, 1, calendar[Calendar.AM_PM])
        }

        // 小时 分钟 分割符
        val isHourNoLimited = (mHourStart == 0 && mHourStop == 23 && mMinuteStart == 0 && mMinuteStop == 59)
        if (isHourNoLimited) {
            mTimeDividerView.displayedValues = resources.getStringArray(R.array.hour_time_divider).toList()
        } else {
            mTimeDividerView.displayedValues = resources.getStringArray(R.array.hour_time_limit_divider).toList()
        }
        initPickerViewValue(mTimeDividerView, 0, 2, 1)

        // 设置是否可以循环滚动
        mHourPickerView.wrapSelectorWheel = isHourNoLimited
        mMinutePickerView.wrapSelectorWheel = isHourNoLimited
        mAmPmPickerView.isEnabled = isHourNoLimited

        var hour = calendar[Calendar.HOUR_OF_DAY]
        hour = if (hour in mHourStart..mHourStop) hour else mHourStart
        var minute = calendar[Calendar.MINUTE]
        minute = if (minute in mMinuteStart..mMinuteStop) minute else mMinuteStart
        setupDisplayedValues(mHourPickerView, hour, mHourStart, mHourStop, mDisplayHour, anim = false)
        // 更新上午下午
        passiveUpdateAmPm()
        // 更新分钟
        passiveUpdateMinute(hour, minute)
        // 更新日历使其与选择器显示的时间一直
        if (mIs24HourFormat) {
            mCalendar[Calendar.HOUR_OF_DAY] = mHourPickerView.value
        } else {
            mCalendar[Calendar.HOUR_OF_DAY] = Hour.getHour(mAmPmPickerView.value, mHourPickerView.value)
        }

        mOnTimeChangeListener?.onTimeChanged(value)
    }

    private fun passiveUpdateMinute(newHour: Int, newMinute: Int) {
        when (newHour) {
            mHourStart -> {
                val newMinuteValue = if (newMinute < mMinuteStart) mMinuteStart else newMinute
                setupDisplayedValues(mMinutePickerView, newMinuteValue, mMinuteStart, 59, mDisplayStartMinute, anim = false)
            }
            mHourStop -> {
                val newMinuteValue = if (newMinute > mMinuteStop) mMinuteStop else newMinute
                setupDisplayedValues(mMinutePickerView, newMinuteValue, 0, mMinuteStop, mDisplayStopMinute, anim = false)
            }
            else -> {
                setupDisplayedValues(mMinutePickerView, newMinute, 0, 59, mDisplayMinute, anim = false)
            }
        }
        // 更新日历
        mCalendar[Calendar.MINUTE] = mMinutePickerView.value
    }

    private fun passiveUpdateAmPm() {
        if (!mIs24HourFormat && !mAmPmPickerView.isEnabled) {
            val value = mHourPickerView.displayValue
            if (value is Hour) {
                val apm = if (value.hour < 12) 0 else 1
                initPickerViewValue(mAmPmPickerView, 0, 1, apm)
            }
        }
    }

    private fun setupDisplayedValues(pickerView: NumberPickerView,
                                     newValue: Int,
                                     newStart: Int,
                                     newStop: Int,
                                     newDisplayedVales: List<CharSequence>,
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

    private fun initPickerViewValue(picker: NumberPickerView, minValue: Int, maxValue: Int, value: Int) {
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.value = value
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        Log.d(TAG, "onValueChange() ${ViewId.getViewId(picker)}  $oldVal -> $newVal")
        when {
            picker === mAmPmPickerView -> {
                mCalendar[Calendar.HOUR_OF_DAY] = Hour.getHour(newVal, mHourPickerView.value)
            }
            picker === mHourPickerView -> {
                passiveUpdateAmPm()
                passiveUpdateMinute(newVal, mMinutePickerView.value)
                if (mIs24HourFormat) {
                    mCalendar[Calendar.HOUR_OF_DAY] = newVal
                } else {
                    mCalendar[Calendar.HOUR_OF_DAY] = Hour.getHour(mAmPmPickerView.value, newVal)
                }
            }
            picker === mMinutePickerView -> {
                mCalendar[Calendar.MINUTE] = newVal
            }
        }
        mOnTimeChangeListener?.onTimeChanged(value)
    }

    val value: Time get() = Time.from(mCalendar)

    fun setOnTimeChangedListener(onTimeChangeListener: OnTimeChangedListener?) {
        mOnTimeChangeListener = onTimeChangeListener
    }

    interface OnTimeChangedListener {
        fun onTimeChanged(time: Time)
    }

    /**
     * @param is24HourFormat 是否24小时制
     * @param hour 24小时制的小时
     */
    private class Hour(val is24HourFormat: Boolean, val hour: Int) : CharSequence {

        companion object {
            @JvmStatic
            internal fun getHour(apm: Int, hour24: Int): Int {
                // 24小时制转化为12小时制时的字面数值
                val displayHour12 = when {
                    hour24 == 0 -> 12  // 00点是午夜(即上午12点)
                    hour24 <= 12 -> hour24  // 小于12的是上午, 12点是中午(即下午12点)
                    else -> hour24 - 12 // 大于12的是下午
                }
                // 12小时制的字面值配合 AM PM 后对应的 24小时制的数值
                return if (apm == Calendar.AM) {
                    if (displayHour12 == 12) 0 else displayHour12
                } else {
                    if (displayHour12 == 12) 12 else displayHour12 + 12
                }
            }
        }

        private val mHour: String = if (is24HourFormat) {
            String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, hour)
        } else {
            when {
                hour == 0 -> 12  // 00点是午夜(即上午12点)
                hour <= 12 -> hour  // 小于12的是上午, 12点是中午(即下午12点)
                else -> hour - 12 // 大于12的是下午
            }.toString()
        }

        override val length: Int
            get() = mHour.length

        override fun get(index: Int): Char {
            return mHour[index]
        }

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return mHour.subSequence(startIndex, endIndex)
        }

        override fun toString(): String {
            return mHour
        }
    }
}