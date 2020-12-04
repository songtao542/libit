package com.liabit.picker.datetime

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
                                               private val dateTimePickerView: DateTimePickerView?,
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
        private fun getNameOfWeek(dayOfWeek: Int): String {
            return if (dayOfWeek > 7 || dayOfWeek < 1) NON_VALUE else WEEK_ENTRIES[dayOfWeek - 1]
        }

        private fun getDateTimeTitle(context: Context, dateTime: DateTime?): String {
            val dateStr = getDateTitle(context, dateTime?.date)
            val timeStr = getTimeTitle(context, dateTime?.time, true)
            return if (dateStr.isNotBlank() && timeStr.isNotBlank()) {
                "$dateStr  $timeStr"
            } else if (dateStr.isNotBlank()) {
                dateStr
            } else if (timeStr.isNotBlank()) {
                timeStr
            } else {
                ""
            }
        }

        private fun getDateTitle(context: Context, date: Date?): String {
            if (date == null) return ""
            val calendar = date.calendar
            val builder = StringBuilder()
            builder.append(calendar[Calendar.YEAR]).append(context.getString(R.string.year))
            builder.append(calendar[Calendar.MONTH] + 1).append(context.getString(R.string.month))
            builder.append(calendar[Calendar.DAY_OF_MONTH]).append(context.getString(R.string.day))
            builder.append("  ")
            builder.append(getNameOfWeek(calendar[Calendar.DAY_OF_WEEK]))
            return builder.toString()
        }

        private fun getTimeTitle(context: Context, time: Time?, is24HourFormat: Boolean): String {
            if (time == null) return ""
            val builder = StringBuilder()
            if (is24HourFormat) {
                builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, time.hourOfDay))
            } else {
                val apm = if (time.isAm) Calendar.AM else Calendar.PM
                builder.append(context.resources.getStringArray(R.array.picker_am_pm_entries)[apm])
                builder.append(time.hourOfDay)
            }
            builder.append(context.resources.getString(R.string.time_divider))
            builder.append(String.format(Locale.getDefault(), FORMAT_TWO_NUMBER, time.minute))
            return builder.toString()
        }
    }

    private var mIsAutoUpdateTitle = true
    private var mIsWithViewDate = false
    private var mIsWithViewTime = false
    private var mOnDateTimeChangeListener: OnDateTimeChangeListener? = null
    private var mOnDateChangeListener: OnDateChangeListener? = null
    private var mOnTimeChangeListener: OnTimeChangeListener? = null

    private val dateTime: DateTime? get() = dateTimePickerView?.value
    private val date: Date? get() = datePickerView?.value
    private val time: Time? get() = timePickerView?.value

    private val mDateTimeChangeListener = object : DateTimePickerView.OnDateTimeChangedListener {
        override fun onDateTimeChanged(dateTime: DateTime) {
            if (mIsAutoUpdateTitle) {
                updateTitle(dateTime)
            }
            mOnDateChangeListener?.onDateChanged(this@DateTimePickerDialog, dateTime.date)
            mOnTimeChangeListener?.onTimeChanged(this@DateTimePickerDialog, dateTime.time)
            mOnDateTimeChangeListener?.onDateTimeChanged(this@DateTimePickerDialog, dateTime)
        }
    }

    private val mDateChangeListener = object : DatePickerView.OnDateChangedListener {
        override fun onDateChanged(date: Date) {
            if (mIsAutoUpdateTitle) {
                updateTitle(date)
            }
            mOnDateChangeListener?.onDateChanged(this@DateTimePickerDialog, date)
        }
    }

    private val mTimeChangeListener = object : TimePickerView.OnTimeChangedListener {
        override fun onTimeChanged(time: Time) {
            if (mIsAutoUpdateTitle) {
                updateTitle(time, timePickerView?.is24HourFormat() ?: false)
            }
            mOnTimeChangeListener?.onTimeChanged(this@DateTimePickerDialog, time)
        }
    }

    private fun updateTitle(date: Date?) {
        val title = getDateTitle(mContext, date)
        if (title.isNotBlank()) {
            dialog.setTitle(title)
        }
    }

    private fun updateTitle(time: Time?, is24HourFormat: Boolean) {
        val title = getTimeTitle(mContext, time, is24HourFormat)
        if (title.isNotBlank()) {
            dialog.setTitle(title)
        }
    }

    private fun updateTitle(dateTime: DateTime?) {
        val title = getDateTimeTitle(mContext, dateTime)
        if (title.isNotBlank()) {
            dialog.setTitle(title)
        }
    }

    private fun setAutoUpdateTitle(enable: Boolean) {
        mIsAutoUpdateTitle = enable
    }

    private fun setWithView(withDate: Boolean, withTime: Boolean) {
        mIsWithViewDate = withDate
        mIsWithViewTime = withTime
    }

    private fun setListener(onDateTimeChangeListener: OnDateTimeChangeListener? = null,
                            onDateChangeListener: OnDateChangeListener? = null,
                            onTimeChangeListener: OnTimeChangeListener? = null) {
        mOnDateTimeChangeListener = onDateTimeChangeListener
        mOnDateChangeListener = onDateChangeListener
        mOnTimeChangeListener = onTimeChangeListener
        dateTimePickerView?.setOnDateTimeChangedListener(mDateTimeChangeListener)
        datePickerView?.setOnDateChangedListener(mDateChangeListener)
        timePickerView?.setOnTimeChangedListener(mTimeChangeListener)
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
                set(year, month - 1, day)
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
                set(year, month - 1, day, hourOfDay, minute)
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
                set(year, month - 1, day)
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
                set(year, month - 1, day, hourOfDay, minute)
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

        private fun fixCalendarTime() {
            val currentTime = mCalendar.time.time
            val currentYear = mCalendar[Calendar.YEAR]
            val currentMonth = mCalendar[Calendar.MONTH]
            val currentDay = mCalendar[Calendar.DAY_OF_MONTH]
            mMinCalendar?.let {
                // 使年月日保持一致，进而比较时间
                it[Calendar.YEAR] = currentYear
                it[Calendar.MONTH] = currentMonth
                it[Calendar.DAY_OF_MONTH] = currentDay
                if (currentTime < it.time.time) {
                    mCalendar.time = it.time
                }
            }
            mMaxCalendar?.let {
                // 使年月日保持一致，进而比较时间
                it[Calendar.YEAR] = currentYear
                it[Calendar.MONTH] = currentMonth
                it[Calendar.DAY_OF_MONTH] = currentDay
                if (currentTime > it.time.time) {
                    mCalendar.time = it.time
                }
            }
        }

        private fun fixCalendarDate() {
            val currentTime = mCalendar.time.time
            mCalendar[Calendar.HOUR_OF_DAY] = 0
            mCalendar[Calendar.MINUTE] = 0
            mMinCalendar?.let {
                // 使时间保持一致，进而比较年月日
                it[Calendar.HOUR_OF_DAY] = 0
                it[Calendar.MINUTE] = 0
                if (currentTime < it.time.time) {
                    mCalendar.time = it.time
                }
            }
            mMaxCalendar?.let {
                // 使时间保持一致，进而比较年月日
                it[Calendar.HOUR_OF_DAY] = 0
                it[Calendar.MINUTE] = 0
                if (currentTime > it.time.time) {
                    mCalendar.time = it.time
                }
            }
        }

        fun create(): DateTimePickerDialog {
            val contentView: View
            var datePickerView: DatePickerView? = null
            var dateTimePickerView: DateTimePickerView? = null
            var timePickerView: TimePickerView? = null
            val title: CharSequence
            val context = ContextThemeWrapper(mContext, theme)

            if (!mIsWithViewDate && mIsWithViewTime) { // 只设置时间
                fixCalendarTime()
                title = if (mIsAutoUpdateTitle) {
                    getTimeTitle(mContext, Time.from(mCalendar), mIs24HourFormat)
                } else {
                    mContext.getString(R.string.picker_select_time)
                }
                contentView = View.inflate(context, R.layout.picker_time_dialog, null)
                timePickerView = contentView.findViewById(R.id.time_picker_view)
                timePickerView.set24HourFormat(mIs24HourFormat)
                mMinCalendar?.let {
                    timePickerView?.setMin(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
                }
                mMaxCalendar?.let {
                    timePickerView?.setMax(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
                }
                timePickerView.setupWithCalendar(mCalendar)
            } else if (mIsWithViewDate && mIsWithViewTime) { // 日期时间都设置
                title = if (mIsAutoUpdateTitle) {
                    getDateTimeTitle(mContext, DateTime.from(mCalendar))
                } else {
                    mContext.getString(R.string.picker_select_date_time)
                }
                contentView = View.inflate(context, R.layout.picker_date_time_dialog, null)
                dateTimePickerView = contentView.findViewById(R.id.date_time_picker_view)
                mMinCalendar?.let { dateTimePickerView?.setMinValue(it) }
                mMaxCalendar?.let { dateTimePickerView?.setMaxValue(it) }
                dateTimePickerView?.setupWithCalendar(mCalendar)
            } else { // 其他情况只设置日期，默认
                fixCalendarDate()
                title = if (mIsAutoUpdateTitle) {
                    getDateTitle(mContext, Date.from(mCalendar))
                } else {
                    mContext.getString(R.string.picker_select_date)
                }
                contentView = View.inflate(context, R.layout.picker_date_dialog, null)
                datePickerView = contentView.findViewById(R.id.date_picker_view)
                mMinCalendar?.let { datePickerView?.setMinValue(it) }
                mMaxCalendar?.let { datePickerView?.setMaxValue(it) }
                datePickerView?.setupWithCalendar(mCalendar)
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
            val pickerDialog = DateTimePickerDialog(mContext, dialog, dateTimePickerView, datePickerView, timePickerView)
            pickerDialog.setAutoUpdateTitle(mIsAutoUpdateTitle)
            pickerDialog.setWithView(mIsWithViewDate, mIsWithViewTime)

            Log.d(TAG, "show() isAutoUpdateTitle:$mIsAutoUpdateTitle, withDate:$mIsWithViewDate, withTime:$mIsWithViewTime, autoUpdateTitle:$mIsAutoUpdateTitle")
            wrapPositiveListener?.setPickerDialog(pickerDialog)
            wrapNegativeListener?.setPickerDialog(pickerDialog)
            pickerDialog.setListener(mDateTimeChangeListener, mDateChangeListener, mTimeChangeListener)
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
                val dateTime = mPickerDialog?.dateTime
                val date = mPickerDialog?.date
                val time = mPickerDialog?.time
                when {
                    dateTime != null -> {
                        actionListener.onAction(dialog, which, dateTime.date)
                        actionListener.onAction(dialog, which, dateTime.time)
                        actionListener.onAction(dialog, which, dateTime)
                    }
                    date != null -> {
                        actionListener.onAction(dialog, which, date)
                    }
                    time != null -> {
                        actionListener.onAction(dialog, which, time)
                    }
                }
            }
        }

    }
}