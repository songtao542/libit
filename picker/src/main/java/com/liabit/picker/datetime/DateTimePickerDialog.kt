package com.liabit.picker.datetime

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.liabit.picker.R
import java.util.*

/**
 * 修改标题和底部Button样式
 * <style name="YourActivityOrDialogTheme" parent="Theme.AppCompat.Light">
 *     <!-- dialog 样式 -->
 *     <item name="android:buttonBarNegativeButtonStyle">@style/DialogButtonStyle</item>
 *     <item name="android:buttonBarPositiveButtonStyle">@style/DialogButtonStyle</item>
 *     <item name="android:windowTitleStyle">@style/DialogTitleStyle</item>
 * </style>
 *
 * <style name="DialogTitleStyle" parent="TextAppearance.AppCompat.Title">
 *     <item name="android:textColor">#007cee</item>
 * </style>

 * <style name="DialogButtonStyle" parent="Widget.AppCompat.Button.ButtonBar.AlertDialog">
 *     <item name="android:textColor">#007cee</item>
 * </style>
 */
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
    private var mOnDateTimeChangeListener: OnDateTimeChangeListener? = null
    private var mOnDateChangeListener: OnDateChangeListener? = null
    private var mOnTimeChangeListener: OnTimeChangeListener? = null

    private var mDataTime: DateTime? = null

    private var mDateStr: String? = null
    private var mTimeStr: String? = null

    private val date: Date? get() = datePickerView?.value
    private val time: Time? get() = timePickerView?.value

    private val mDateChangeListener = object : DatePickerView.OnDateChangedListener {
        override fun onDateChanged(date: Date) {
            if (mIsAutoUpdateTitle) {
                updateTitle(date)
            }
            mOnDateChangeListener?.onDateChanged(this@DateTimePickerDialog, date)
            val time = time
            if (time != null) {
                mOnDateTimeChangeListener?.onDateTimeChanged(this@DateTimePickerDialog, DateTime.from(date, time))
            }
        }
    }

    private val mTimeChangeListener = object : TimePickerView.OnTimeChangedListener {
        override fun onTimeChanged(time: Time) {
            if (mIsAutoUpdateTitle) {
                updateTitle(time)
            }
            mOnTimeChangeListener?.onTimeChanged(this@DateTimePickerDialog, time)
            val date = date
            if (date != null) {
                mOnDateTimeChangeListener?.onDateTimeChanged(this@DateTimePickerDialog, DateTime.from(date, time))
            }
        }
    }

    init {
        datePickerView?.setOnDateChangedListener(mDateChangeListener)
        timePickerView?.setOnTimeChangedListener(mTimeChangeListener)
    }


    private fun updateTitle(dateValue: Date) {
        mDateStr = getDateString(dateValue)
        var title = mDateStr
        if (mIsWithViewTime) {
            title = "$mDateStr  $mTimeStr"
        }
        updateTitle(title)
    }

    private fun updateTitle(timeData: Time) {
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

    private fun setListener(onDateChangeListener: OnDateChangeListener? = null,
                            onTimeChangeListener: OnTimeChangeListener? = null,
                            onDateTimeChangeListener: OnDateTimeChangeListener? = null) {
        mOnDateChangeListener = onDateChangeListener
        mOnTimeChangeListener = onTimeChangeListener
        mOnDateTimeChangeListener = onDateTimeChangeListener
    }

    private fun getTimeString(time: Time): String {
        val builder = StringBuilder()
        if (time.is24HourFormat) {
            builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, time.hour))
        } else {
            builder.append(mContext.resources.getStringArray(R.array.picker_am_pm_entries)[time.apm])
            builder.append(time.hour)
        }
        builder.append(mContext.resources.getString(R.string.time_divider))
        builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, time.minute))
        return builder.toString()
    }

    @SuppressLint("WrongConstant")
    private fun getDateString(date: Date): String {
        val calendar = date.calendar
        val builder = StringBuilder()
        builder.append(calendar[Calendar.YEAR]).append(mContext.getString(R.string.year))
        builder.append(calendar[Calendar.MONTH] + 1).append(mContext.getString(R.string.month))
        builder.append(calendar[Calendar.DAY_OF_MONTH]).append(mContext.getString(R.string.day))
        builder.append("  ")
        builder.append(getNameOfWeek(calendar[Calendar.DAY_OF_WEEK]))
        return builder.toString()
    }


    interface OnDateTimeChangeListener {
        fun onDateTimeChanged(dialog: DateTimePickerDialog, date: DateTime) {}
    }

    interface OnDateChangeListener {
        fun onDateChanged(dialog: DateTimePickerDialog, date: Date) {}
    }

    interface OnTimeChangeListener {
        fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {}
    }

    interface OnActionListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         * @param which the button that was clicked (ex.
         *              {@link DialogInterface#BUTTON_POSITIVE}) or the position
         *              of the item clicked
         */
        fun onAction(dialog: DialogInterface, which: Int, date: Date) {}

        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         * @param which the button that was clicked (ex.
         *              {@link DialogInterface#BUTTON_POSITIVE}) or the position
         *              of the item clicked
         */
        fun onAction(dialog: DialogInterface, which: Int, time: Time) {}

        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         * @param which the button that was clicked (ex.
         *              {@link DialogInterface#BUTTON_POSITIVE}) or the position
         *              of the item clicked
         */
        fun onAction(dialog: DialogInterface, which: Int, dateTime: DateTime) {}
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Builder(private val mContext: Context, private var theme: Int = R.style.PickerDialog) {

        private val mCalendar: Calendar = Calendar.getInstance()

        private var mMinCalendar: Calendar? = null
        private var mMaxCalendar: Calendar? = null

        private val mBuilder: AlertDialog.Builder = AlertDialog.Builder(mContext, theme)

        private var mIsAutoUpdateTitle = true
        private var mIsShowGregorian = true
        private var mIsWithViewDate = false
        private var mIsWithViewTime = false
        private var mDateTimeChangeListener: OnDateTimeChangeListener? = null
        private var mDateChangeListener: OnDateChangeListener? = null
        private var mTimeChangeListener: OnTimeChangeListener? = null
        private var mIs24HourFormat = true
        private var mCanceledOnTouchOutside = true
        private var mGravity = Gravity.BOTTOM

        private var mActionListener: OnActionListener? = null
        private var mPositiveClickListener: DialogInterface.OnClickListener? = null
        private var mNegativeClickListener: DialogInterface.OnClickListener? = null
        private var mPositiveText: CharSequence? = null
        private var mNegativeText: CharSequence? = null

        init {
            mBuilder.setTitle(" ") //避免外部未设置时无法显示title
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

        fun setWithDate(isWithDate: Boolean): Builder {
            mIsWithViewDate = isWithDate
            return this
        }

        fun setWithTime(isWithTime: Boolean): Builder {
            mIsWithViewTime = isWithTime
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
         * @param apm [Calendar.AM] or [Calendar.PM]
         */
        fun setDefaultTime(hour: Int, minute: Int, apm: Int): Builder {
            mCalendar[Calendar.HOUR] = hour
            mCalendar[Calendar.MINUTE] = minute
            if (apm == Calendar.AM || apm == Calendar.PM) {
                mCalendar[Calendar.AM_PM] = apm
            }
            mIsWithViewTime = true
            return this
        }

        /**
         * @param year (1900-2100)
         * @param month  (1-12)
         * @param day (1-31)
         */
        fun setDefaultDate(year: Int, month: Int, day: Int): Builder {
            mCalendar[Calendar.YEAR] = year
            mCalendar[Calendar.MONTH] = month - 1
            mCalendar[Calendar.DAY_OF_MONTH] = day
            mIsWithViewDate = true
            return this
        }

        /**
         * @param calendar [Calendar.time]
         */
        fun setDefaultDate(calendar: Calendar): Builder {
            mCalendar.time = calendar.time
            mIsWithViewDate = true
            return this
        }

        fun set24HourFormat(is24HourFormat: Boolean): Builder {
            mIs24HourFormat = is24HourFormat
            return this
        }

        /**
         * @param month (1-12)
         * @param day (1-31)
         */
        fun setMinDate(year: Int, month: Int, day: Int): Builder {
            mMinCalendar = (mMinCalendar ?: Calendar.getInstance()).apply {
                set(year, month, day)
            }
            return this
        }

        /**
         * @param month (1-12)
         * @param day (1-31)
         * @param hourOfDay (0-23)
         * @param minute (0,59)
         */
        fun setMinDateTime(year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int): Builder {
            mMinCalendar = (mMinCalendar ?: Calendar.getInstance()).apply {
                set(year, month, day, hourOfDay, minute)
            }
            return this
        }

        /**
         * @param calendar [Calendar.time]
         */
        fun setMinDateTime(calendar: Calendar?): Builder {
            mMinCalendar = calendar
            return this
        }

        /**
         * @param hourOfDay (0-23)
         * @param minute (0,59)
         */
        fun setMinTime(hourOfDay: Int, minute: Int): Builder {
            mMinCalendar = (mMinCalendar ?: Calendar.getInstance()).apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            return this
        }

        /**
         * @param month (1-12)
         * @param day (1-31)
         */
        fun setMaxDate(year: Int, month: Int, day: Int): Builder {
            mMaxCalendar = (mMaxCalendar ?: Calendar.getInstance()).apply {
                set(year, month, day)
            }
            return this
        }

        /**
         * @param month (1-12)
         * @param day (1-31)
         * @param hourOfDay (0-23)
         * @param minute (0,59)
         */
        fun setMaxDateTime(year: Int, month: Int, day: Int, hourOfDay: Int, minute: Int): Builder {
            mMaxCalendar = (mMaxCalendar ?: Calendar.getInstance()).apply {
                set(year, month, day, hourOfDay, minute)
            }
            return this
        }

        /**
         * @param calendar [Calendar.time]
         */
        fun setMaxDateTime(calendar: Calendar?): Builder {
            mMaxCalendar = calendar
            return this
        }

        /**
         * @param hourOfDay (0-23)
         * @param minute (0,59)
         */
        fun setMaxTime(hourOfDay: Int, minute: Int): Builder {
            mMaxCalendar = (mMaxCalendar ?: Calendar.getInstance()).apply {
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            return this
        }

        fun setOnDateTimeChangedListener(listener: OnDateTimeChangeListener?): Builder {
            mDateTimeChangeListener = listener
            return this
        }

        fun setOnDateChangedListener(listener: OnDateChangeListener?): Builder {
            mDateChangeListener = listener
            return this
        }

        fun setOnTimeChangedListener(listener: OnTimeChangeListener?): Builder {
            mTimeChangeListener = listener
            return this
        }

        fun create(): DateTimePickerDialog {
            val contentView: View
            var datePickerView: DatePickerView? = null
            var timePickerView: TimePickerView? = null
            val title: CharSequence
            val context = ContextThemeWrapper(mContext, theme)
            if (!mIsWithViewDate && mIsWithViewTime) { // 只设置时间
                title = mContext.getString(R.string.picker_select_time)
                contentView = View.inflate(context, R.layout.picker_time_dialog, null)
                timePickerView = contentView.findViewById(R.id.time_picker_view)
                timePickerView.set24HourFormat(mIs24HourFormat)
            } else if (mIsWithViewDate && mIsWithViewTime) { // 日期时间都设置
                title = mContext.getString(R.string.picker_select_date_time)
                contentView = View.inflate(context, R.layout.picker_date_time_dialog, null)
                datePickerView = contentView.findViewById(R.id.date_picker_view)
                timePickerView = contentView.findViewById(R.id.time_picker_view)
                timePickerView.set24HourFormat(true) // UI设计日期时间同时显示时，只有24h
                timePickerView.setItemWrapContent()
                timePickerView.setItemPadding(mContext.resources.getDimensionPixelSize(R.dimen.time_picker_item_padding))
            } else { // 其他情况只设置日期，默认
                title = mContext.getString(R.string.picker_select_date)
                contentView = View.inflate(context, R.layout.picker_date_dialog, null)
                datePickerView = contentView.findViewById(R.id.date_picker_view)
            }

            var wrapPositiveListener: WrapDialogOnClickListener? = null
            var wrapNegativeListener: WrapDialogOnClickListener? = null

            val actionListener = mActionListener
            val positiveClickListener = mPositiveClickListener
            val positiveText = mPositiveText ?: mContext.getText(R.string.picker_dialog_confirm)
            if (actionListener != null) {
                wrapPositiveListener = if (positiveClickListener == null) {
                    WrapDialogOnClickListener(actionListener)
                } else {
                    WrapDialogOnClickListener(actionListener, positiveClickListener)
                }
                mBuilder.setPositiveButton(positiveText, wrapPositiveListener)
            } else if (positiveClickListener != null) {
                mBuilder.setPositiveButton(positiveText, positiveClickListener)
            }

            val negativeClickListener = mNegativeClickListener
            val negativeText = mNegativeText ?: mContext.getText(R.string.picker_dialog_cancel)
            if (actionListener != null) {
                wrapNegativeListener = if (negativeClickListener == null) {
                    WrapDialogOnClickListener(actionListener)
                } else {
                    WrapDialogOnClickListener(actionListener, negativeClickListener)
                }
                mBuilder.setNegativeButton(negativeText, wrapNegativeListener)
            } else if (positiveClickListener != null) {
                mBuilder.setNegativeButton(negativeText, negativeClickListener)
            }
            mBuilder.setTitle(title)
            mBuilder.setView(contentView)

            val dialog = mBuilder.create()
            val window = dialog.window
            window?.setGravity(mGravity)
            dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside)
            //先创建pickerDialog实例，后续设置数据回调onChange
            val pickerDialog = DateTimePickerDialog(mContext, dialog, datePickerView, timePickerView)
            pickerDialog.setListener(mDateChangeListener, mTimeChangeListener, mDateTimeChangeListener)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setWithView(mIsWithViewDate, mIsWithViewTime)

            //设置日期、时间数据，默认为当前系统日期时间
            if (datePickerView != null) {
                mMinCalendar?.let {
                    datePickerView.setMinValue(it)
                }
                mMaxCalendar?.let {
                    datePickerView.setMaxValue(it)
                }
                datePickerView.setupWithCalendar(mCalendar)
            }
            if (timePickerView != null) {
                mMinCalendar?.let {
                    val hour = it.get(Calendar.HOUR_OF_DAY)
                    val minute = it.get(Calendar.MINUTE)
                    timePickerView.setMin(hour, minute)
                }
                mMaxCalendar?.let {
                    val hour = it.get(Calendar.HOUR_OF_DAY)
                    val minute = it.get(Calendar.MINUTE)
                    timePickerView.setMax(hour, minute)
                }
                timePickerView.setupWithCalendar(mCalendar)
            }
            timePickerView?.setupWithCalendar(mCalendar)
            Log.d(TAG, "show() isAutoUpdateTitle:$mIsAutoUpdateTitle, withDate:$mIsWithViewDate, withTime:$mIsWithViewTime, autoUpdateTitle:$mIsAutoUpdateTitle")
            wrapPositiveListener?.setPickerDialog(pickerDialog)
            wrapNegativeListener?.setPickerDialog(pickerDialog)
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
            mPositiveClickListener = listener
            mPositiveText = mContext.getText(textResId)
            return this
        }

        fun setPositiveButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mPositiveClickListener = listener
            mPositiveText = text
            return this
        }

        fun setNegativeButton(textResId: Int, listener: DialogInterface.OnClickListener): Builder {
            mNegativeClickListener = listener
            mNegativeText = mContext.getText(textResId)
            return this
        }

        fun setNegativeButton(text: CharSequence, listener: DialogInterface.OnClickListener): Builder {
            mNegativeClickListener = listener
            mNegativeText = text
            return this
        }

        fun setPositiveButtonText(textResId: Int): Builder {
            mPositiveText = mContext.getText(textResId)
            return this
        }

        fun setPositiveButtonText(text: CharSequence): Builder {
            mPositiveText = text
            return this
        }

        fun setNegativeButtonText(textResId: Int): Builder {
            mNegativeText = mContext.getText(textResId)
            return this
        }

        fun setNegativeButtonText(text: CharSequence): Builder {
            mNegativeText = text
            return this
        }

        fun setActionListener(listener: OnActionListener): Builder {
            mActionListener = listener
            return this
        }

        fun setGravity(gravity: Int): Builder {
            mGravity = gravity
            return this
        }

        private class WrapDialogOnClickListener(private val actionListener: OnActionListener) : DialogInterface.OnClickListener {

            private var mPickerDialog: DateTimePickerDialog? = null
            private var mWrappedListener: DialogInterface.OnClickListener? = null

            constructor(actionListener: OnActionListener, listener: DialogInterface.OnClickListener) : this(actionListener) {
                mWrappedListener = listener
            }

            fun setPickerDialog(pickerDialog: DateTimePickerDialog) {
                mPickerDialog = pickerDialog
            }

            override fun onClick(dialog: DialogInterface, which: Int) {
                mWrappedListener?.onClick(dialog, which)
                val date = mPickerDialog?.date
                val time = mPickerDialog?.time
                if (date != null && time != null) {
                    actionListener.onAction(dialog, which, DateTime.from(date, time))
                } else if (date != null) {
                    actionListener.onAction(dialog, which, date)
                } else if (time != null) {
                    actionListener.onAction(dialog, which, time)
                }
            }
        }

    }
}