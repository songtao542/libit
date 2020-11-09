package com.liabit.picker

import android.content.Context
import android.content.DialogInterface
import com.liabit.picker.address.Address
import com.liabit.picker.address.AddressPickerDialog
import com.liabit.picker.datetime.DateTimePickerDialog
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
object Picker {

    @JvmStatic
    fun pickDate(context: Context, listener: ((date: Date) -> Unit)? = null) {
        pickDate(context, 0, 20, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, yearOffset: Int, listener: ((date: Date) -> Unit)? = null) {
        pickDate(context, yearOffset, yearOffset, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, forwardYearOffset: Int, backwardYearOffset: Int, listener: ((date: Date) -> Unit)? = null) {
        val current: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val currentYear = current.get(Calendar.YEAR)
        val currentMonth = current.get(Calendar.MONTH)
        val currentDay = current.get(Calendar.DAY_OF_MONTH)
        val start: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val end: Calendar = Calendar.getInstance(TimeZone.getDefault())
        start.set(currentYear - forwardYearOffset, currentMonth, currentDay)
        end.set(currentYear + backwardYearOffset, currentMonth, currentDay)
        pickDate(context, current.time, start.time, end.time, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: ((date: Date) -> Unit)? = null) {
        if (startDate > endDate) {
            throw IllegalStateException("The start date must lower than end date")
        }
        val current: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val start: Calendar = Calendar.getInstance(TimeZone.getDefault())
        val end: Calendar = Calendar.getInstance(TimeZone.getDefault())

        current.time = currentDate
        start.time = startDate
        end.time = endDate
        DateTimePickerDialog.Builder(context)
                .setWithDate(true)
                .setWithTime(false)
                .setDefaultDate(current)
                .setMinDate(start)
                .setMaxDate(end)
                .setActionListener(object : DateTimePickerDialog.OnActionListener {
                    override fun onAction(dialog: DialogInterface, which: Int, date: com.liabit.picker.datetime.Date) {
                        listener?.invoke(date.calendar.time)
                    }
                })
                .show()
    }

    @JvmStatic
    fun pickAddress(context: Context, address: Address?, listener: ((address: Address) -> Unit)? = null) {
        AddressPickerDialog.Builder(context)
                .setDefaultAddress(address)
                .setActionListener(object : AddressPickerDialog.OnActionListener {
                    override fun onAction(dialog: DialogInterface, which: Int, address: Address) {
                        listener?.invoke(address)
                    }
                })
                .show()
    }

    @JvmStatic
    fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: ((number: Int) -> Unit)? = null) {
        PickerDialog.Builder(context)
                .setDefaultValue(number)
                .setDisplayValues(from, to)
                .setCycle(false)
                .setActionListener(object : PickerDialog.OnActionListener {
                    override fun onAction(dialog: DialogInterface, which: Int, index: Int, value: PickerDialog.DisplayValue) {
                        listener?.invoke(value.getDisplayValue().toString().toInt())
                    }
                })
                .show()
    }
}
