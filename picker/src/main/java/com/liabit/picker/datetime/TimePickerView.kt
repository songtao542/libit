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
    private lateinit var mTimeDividerView: NumberPickerView
    private lateinit var mHourPickerView: NumberPickerView
    private lateinit var mMinutePickerView: NumberPickerView

    private var mCalendar: Calendar = Calendar.getInstance()
    private var mIs24HourFormat = false
    private var mAuto24Hour = true //自动根据系统24小时显示不同view
    private var mDisplayHour24: List<String> = emptyList()
    private var mDisplayHour12: List<String> = emptyList()
    private var mDisplayMinute: List<String> = emptyList()
    private var mOnTimeChangeListener: OnTimeChangedListener? = null
    private var mOnWindowFocusChangeListener: ViewTreeObserver.OnWindowFocusChangeListener? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
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
        initDisplayTime()
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
                        initDisplayTime()
                    }
                }
                viewTreeObserver.addOnWindowFocusChangeListener(mOnWindowFocusChangeListener)
            }
        }
    }

    fun setTime(hour: Int, minute: Int) {
        mCalendar[Calendar.HOUR_OF_DAY] = hour
        mCalendar[Calendar.MINUTE] = minute
        initDisplayTime()
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
        initDisplayTime()
    }

    fun set24HourFormat(is24HourFormat: Boolean) {
        mAuto24Hour = false
        if (mIs24HourFormat == is24HourFormat) {
            return
        }
        mIs24HourFormat = is24HourFormat
        initDisplayTime()
    }

    fun is24HourFormat(): Boolean {
        return mIs24HourFormat
    }

    private fun initDisplayTime() {
        initDisplayTime(mCalendar)
    }

    fun initDisplayTime(calendar: Calendar) {
        if (mIs24HourFormat) {
            mAmPmPickerView.visibility = GONE
            mTimeDividerView.visibility = VISIBLE
        } else {
            mTimeDividerView.visibility = GONE
            mAmPmPickerView.visibility = VISIBLE
            val apm = context.resources.getStringArray(R.array.picker_am_pm_entries)
            mAmPmPickerView.setDisplayedValues(arrayListOf(*apm), false)
            initPickerViewData(mAmPmPickerView, 0, 1, calendar[Calendar.AM_PM])
        }
        // hour
        if (mIs24HourFormat && mDisplayHour24.isEmpty()) {
            mDisplayHour24 = MutableList(24) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, it)
            }
        }
        if (!mIs24HourFormat && mDisplayHour12.isEmpty()) {
            mDisplayHour12 = MutableList(12) {
                if (it == 0) 12.toString() else it.toString()
            }
        }
        val displayHour = if (mIs24HourFormat) mDisplayHour24 else mDisplayHour12
        mHourPickerView.setDisplayedValues(displayHour, false)
        val hour = if (mIs24HourFormat) calendar[Calendar.HOUR_OF_DAY] else calendar[Calendar.HOUR]
        initPickerViewData(mHourPickerView, 0, displayHour.size - 1, hour)

        // minute
        if (mDisplayMinute.isEmpty()) {
            mDisplayMinute = MutableList(60) {
                String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, it)
            }
        }
        mMinutePickerView.setDisplayedValues(mDisplayMinute, false)
        initPickerViewData(mMinutePickerView, 0, 59, calendar[Calendar.MINUTE])
        mOnTimeChangeListener?.onTimeChanged(value)
    }

    private fun initPickerViewData(picker: NumberPickerView, minValue: Int, maxValue: Int, value: Int) {
        picker.minValue = minValue
        picker.maxValue = maxValue
        picker.value = value
    }

    override fun onValueChange(picker: NumberPickerView, oldVal: Int, newVal: Int) {
        Log.d(TAG, "onValueChange() picker new:$newVal")
        if (picker === mAmPmPickerView) {
            mCalendar[Calendar.AM_PM] = newVal
        } else if (picker === mHourPickerView) {
            if (mIs24HourFormat) {
                mCalendar[Calendar.HOUR_OF_DAY] = newVal
            } else {
                mCalendar[Calendar.HOUR] = newVal
            }
        } else if (picker === mMinutePickerView) {
            mCalendar[Calendar.MINUTE] = newVal
        }
        mOnTimeChangeListener?.onTimeChanged(value)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val value: Time
        get() {
            var hour = mHourPickerView.value
            hour = if (!mIs24HourFormat && hour == 0) 12 else hour
            return Time(hour, mMinutePickerView.value, mAmPmPickerView.value, mIs24HourFormat)
        }

    fun setOnTimeChangedListener(onTimeChangeListener: OnTimeChangedListener?) {
        mOnTimeChangeListener = onTimeChangeListener
    }

    interface OnTimeChangedListener {
        fun onTimeChanged(time: Time)
    }
}