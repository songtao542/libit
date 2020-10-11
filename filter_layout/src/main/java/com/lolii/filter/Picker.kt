package com.lolii.filter;

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import cn.lolii.picker.datetime.DatePickerView
import cn.lolii.picker.datetime.DateTimePickerDialog
import cn.lolii.picker.datetime.Time
import java.lang.IllegalStateException
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
object Picker {

    @JvmStatic
    fun pickDate(context: Context, listener: OnDateSelectListener) {
        pickDate(context, 0, 20, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, yearOffset: Int, listener: OnDateSelectListener) {
        pickDate(context, yearOffset, yearOffset, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, forwardYearOffset: Int, backwardYearOffset: Int, listener: OnDateSelectListener) {
        val current: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = current.get(Calendar.YEAR)
        val currentMonth = current.get(Calendar.MONTH)
        val currentDay = current.get(Calendar.DAY_OF_MONTH)
        val start: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val end: Calendar = Calendar.getInstance(TimeZone.getDefault())
        start.set(currentYear - forwardYearOffset, currentMonth, currentDay)
        end.set(currentYear + backwardYearOffset, currentMonth, currentDay)
        pickDate(context, start.time, end.time, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, startDate: Date, endDate: Date, listener: OnDateSelectListener) {
        if (startDate > endDate) {
            throw IllegalStateException("The start date must lower than end date")
        }
        val current: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val start: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val end: Calendar = Calendar.getInstance(TimeZone.getDefault())

        start.time = startDate
        end.time = endDate
        DateTimePickerDialog.Builder(context)
                .setWithDate(true)
                .setWithTime(false)
                .setDefaultDate(current)
                .setMinDate(start)
                .setMaxDate(end)
                .setActionListener(object : DateTimePickerDialog.OnActionListener {
                    override fun onAction(dialog: DialogInterface, which: Int, date: cn.lolii.picker.datetime.Date) {
                        listener.onDateSelect(date.calendar.time)
                    }
                })
                .show()
    }

    interface OnDateSelectListener {
        fun onDateSelect(date: Date)
    }
}
