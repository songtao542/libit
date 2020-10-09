package cn.lolii.picker.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import cn.lolii.picker.R
import cn.lolii.picker.datepicker.ChineseCalendar
import java.util.*

@Suppress("unused")
class DateTimePickerDialog private constructor(private val mContext: Context,
                                               private val dialog: AlertDialog,
                                               private val datePickerView: DatePickerView?,
                                               private val timePickerView: TimePickerView?) {

    companion object {
        private const val TAG = "DateTimePickerDialog"
        private const val FORMAT_TWO_NUMBER = "%02d"

        private val WEEK_ENTRIES: Array<String> = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")
        private const val NON_VALUE = "NON"

        /**
         * 获取星期几的显示字符
         * @param dayOfWeek 范围 [1, 7]
         */
        fun getNameOfWeek(dayOfWeek: Int): String {
            return if (dayOfWeek > 7 || dayOfWeek < 1) NON_VALUE else WEEK_ENTRIES[dayOfWeek - 1]
        }
    }

    private var mIsAutoUpdateTitle = true
    private var mIsWithViewDate = false
    private var mIsWithViewTime = false
    private var mDateValue: DatePickerView.DateValue? = null
    private var mTimeValue: TimePickerView.TimeValue? = null

    private var mDateStr: String? = null
    private var mTimeStr: String? = null

    private fun updateTitle(dateValue: DatePickerView.DateValue) {
        mDateStr = getDateString(dateValue)
        var title = mDateStr
        if (mIsWithViewTime) {
            title = "$mDateStr  $mTimeStr"
        }
        updateTitle(title)
    }

    private fun updateTitle(timeData: TimePickerView.TimeValue) {
        mTimeStr = getTimeString(timeData)
        var title = mTimeStr
        if (mIsWithViewDate) {
            title = "$mDateStr  $mTimeStr"
        }
        updateTitle(title)
    }

    private fun updateTitle(title: CharSequence?) {
        dialog.setTitle(title)
    }

    fun setAutoUpdateTitle(enable: Boolean) {
        mIsAutoUpdateTitle = enable
    }

    private fun setWithView(withDate: Boolean, withTime: Boolean) {
        mIsWithViewDate = withDate
        mIsWithViewTime = withTime
    }

    private fun setDateTimeChangeListener(listener: OnDateTimeChangeListener?) {
        datePickerView?.setOnDateChangedListener(object : DatePickerView.OnDateChangedListener {
            override fun onDateChanged(dateValue: DatePickerView.DateValue) {
                mDateValue = dateValue
                if (mIsAutoUpdateTitle) {
                    updateTitle(dateValue)
                }
                listener?.onDateChanged(this@DateTimePickerDialog, dateValue)
            }
        })

        timePickerView?.setOnTimeChangeListener(object : TimePickerView.OnTimeChangeListener {
            override fun onTimeChange(timeValue: TimePickerView.TimeValue) {
                mTimeValue = timeValue
                if (mIsAutoUpdateTitle) {
                    updateTitle(timeValue)
                }
                listener?.onTimeChanged(this@DateTimePickerDialog, timeValue)
            }
        })
    }

    private fun getTimeString(timeData: TimePickerView.TimeValue): String {
        val builder = StringBuilder()
        if (timeData.is24HourFormat) {
            builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, timeData.hour))
        } else {
            builder.append(mContext.resources.getStringArray(R.array.am_pm_entries)[timeData.apm])
            builder.append(timeData.hour)
        }
        builder.append(mContext.resources.getString(R.string.time_divider))
        builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, timeData.minute))
        return builder.toString()
    }

    @SuppressLint("WrongConstant")
    private fun getDateString(dateValue: DatePickerView.DateValue): String {
        val calendar = dateValue.calendar
        val builder = StringBuilder()
        builder.append(calendar[Calendar.YEAR]).append(mContext.getString(R.string.year))
        builder.append(calendar[Calendar.MONTH] + 1).append(mContext.getString(R.string.month))
        builder.append(calendar[Calendar.DAY_OF_MONTH]).append(mContext.getString(R.string.day))
        builder.append("  ")
        builder.append(getNameOfWeek(calendar[ChineseCalendar.DAY_OF_WEEK]))
        return builder.toString()
    }


    interface OnDateTimeChangeListener {
        fun onDateChanged(dialog: DateTimePickerDialog, calendarData: DatePickerView.DateValue) {}
        fun onTimeChanged(dialog: DateTimePickerDialog, timeData: TimePickerView.TimeValue) {}
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(private val mContext: Context, theme: Int = R.style.PickerDialog) {

        private val mCalendar: Calendar = Calendar.getInstance()

        private var mMinDateCalendar: Calendar? = null
        private var mMaxDateCalendar: Calendar? = null

        private val mBuilder: AlertDialog.Builder = AlertDialog.Builder(mContext, theme)

        private var mIsAutoUpdateTitle = true
        private var mIsShowGregorian = true
        private var mIsWithViewDate = false
        private var mIsWithViewTime = false
        private var mDateTimeChangeListener: OnDateTimeChangeListener? = null
        private var mShow24Hour = -1
        private var mCanceledOnTouchOutside = true
        private var mGravity = Gravity.BOTTOM

        init {
            mBuilder.setTitle(0.toString() + "") //避免外部未设置时无法显示title
        }

        constructor(context: Context, year: Int, month: Int, day: Int) : this(context, R.style.PickerDialog) {
            setDefaultDate(year, month, day)
        }

        constructor(context: Context, hour: Int, minute: Int) : this(context, R.style.PickerDialog) {
            setDefaultTime(hour, minute)
        }

        /**
         * 调用此方法后，将不默认自动更新title。如需自动，请再调用[.setAutoUpdateTitle]
         */
        fun setTitle(title: CharSequence?): Builder {
            mIsAutoUpdateTitle = false
            mBuilder.setTitle(title)
            return this
        }

        fun setAutoUpdateTitle(enable: Boolean): Builder {
            mIsAutoUpdateTitle = enable
            return this
        }

        fun setWithDate(isWith: Boolean): Builder {
            mIsWithViewDate = isWith
            return this
        }

        fun setWithTime(isWith: Boolean): Builder {
            mIsWithViewTime = isWith
            return this
        }

        /**
         * @param hour [0,23] 设置到Calendar.HOUR_OF_DAY
         */
        fun setDefaultTime(hour: Int, minute: Int): Builder {
            mCalendar[Calendar.HOUR_OF_DAY] = hour
            mCalendar[Calendar.MINUTE] = minute
            mIsWithViewTime = true
            return this
        }

        /**
         * @param hour 12-hour clock (0 - 11).  0 在12小时制显示为12
         * @param amPm [Calendar.AM] or [Calendar.PM]
         */
        fun setDefaultTime(hour: Int, minute: Int, amPm: Int): Builder {
            mCalendar[Calendar.HOUR] = hour
            mCalendar[Calendar.MINUTE] = minute
            if (amPm == Calendar.AM || amPm == Calendar.PM) {
                mCalendar[Calendar.AM_PM] = amPm
            }
            mIsWithViewTime = true
            return this
        }

        fun setTimeShow24Hour(isShow24Hour: Boolean): Builder {
            mShow24Hour = if (isShow24Hour) 1 else 0
            return this
        }

        fun setMinDate(year: Int, month: Int, day: Int): Builder {
            if (mMinDateCalendar == null) {
                mMinDateCalendar = Calendar.getInstance()
            }
            mMinDateCalendar!![Calendar.YEAR] = year
            mMinDateCalendar!![Calendar.MONTH] = month - 1
            mMinDateCalendar!![Calendar.DAY_OF_MONTH] = day
            return this
        }

        fun setMaxDate(year: Int, month: Int, day: Int): Builder {
            if (mMaxDateCalendar == null) {
                mMaxDateCalendar = Calendar.getInstance()
            }
            mMaxDateCalendar!![Calendar.YEAR] = year
            mMaxDateCalendar!![Calendar.MONTH] = month - 1
            mMaxDateCalendar!![Calendar.DAY_OF_MONTH] = day
            return this
        }

        fun setDefaultDate(year: Int, month: Int, day: Int): Builder {
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month - 1
            mCalendar[Calendar.DAY_OF_MONTH] = day
            mIsWithViewDate = true
            return this
        }

        fun setDateTimeChangeListener(listener: OnDateTimeChangeListener?): Builder {
            mDateTimeChangeListener = listener
            return this
        }

        fun create(): DateTimePickerDialog {
            val contentView: View
            var calendarView: DatePickerView? = null
            var timePickerView: TimePickerView? = null
            if (!mIsWithViewDate && mIsWithViewTime) {  // 只设置时间
                contentView = View.inflate(mContext, R.layout.dialog_time_picker, null)
                timePickerView = contentView.findViewById(R.id.time_picker_view)
                if (mShow24Hour >= 0) {
                    timePickerView.setIs24Hour(mShow24Hour == 1)
                }
            } else if (mIsWithViewDate && mIsWithViewTime) {    // 日期时间都设置
                contentView = View.inflate(mContext, R.layout.dialog_date_time_picker, null)
                calendarView = contentView.findViewById(R.id.date_picker_view)
                timePickerView = contentView.findViewById(R.id.time_picker_view)
                timePickerView.setIs24Hour(true) // UI设计日期时间同时显示时，只有24h
                timePickerView.setItemWrapContent()
                timePickerView.setItemPadding(mContext.resources.getDimensionPixelSize(R.dimen.dialog_time_item_padding))
            } else {    // 其他情况只设置日期，默认
                contentView = View.inflate(mContext, R.layout.dialog_date_picker, null)
                calendarView = contentView.findViewById(R.id.date_picker_view)
            }
            mBuilder.setView(contentView)
            val dialog = mBuilder.create()
            val window = dialog.window
            window?.setGravity(mGravity)
            dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            //先创建pickerDialog实例，后续设置数据回调onChange
            val pickerDialog = DateTimePickerDialog(mContext, dialog, calendarView, timePickerView)
            pickerDialog.setDateTimeChangeListener(mDateTimeChangeListener)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setWithView(mIsWithViewDate, mIsWithViewTime)

            //设置日期、时间数据，默认为当前系统日期时间
            if (calendarView != null) {
                Log.d(TAG, "show() mIsAutoUpdateTitle:$mIsAutoUpdateTitle,mIsShowGregorian:$mIsShowGregorian")
                mMinDateCalendar?.let {
                    calendarView.setMinValue(it)
                }
                mMaxDateCalendar?.let {
                    calendarView.setMaxValue(it)
                }
                calendarView.reset(mCalendar)
            }
            timePickerView?.initDisplayTime(mCalendar)
            Log.d(TAG, "show() withDate:$mIsWithViewDate,withTime:$mIsWithViewTime,autoUpdateTitle:$mIsAutoUpdateTitle")
            return pickerDialog
        }

        fun show(): DateTimePickerDialog {
            return create().also { it.dialog.show() }
        }

        fun setCanceledOnTouchOutside(enable: Boolean): Builder {
            mCanceledOnTouchOutside = enable
            return this
        }

        fun setCancelable(enable: Boolean): Builder {
            mBuilder.setCancelable(enable)
            return this
        }

        fun setPositiveButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setPositiveButton(textResId, listener)
            return this
        }

        fun setNegativeButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setNegativeButton(textResId, listener)
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setPositiveButton(text, listener)
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mBuilder.setNegativeButton(text, listener)
            return this
        }

        fun setGravity(gravity: Int): Builder {
            mGravity = gravity
            return this
        }
    }
}