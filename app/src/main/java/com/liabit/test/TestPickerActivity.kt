package com.liabit.test

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.picker.address.Address
import com.liabit.picker.address.AddressPickerDialog
import com.liabit.picker.datetime.Date
import com.liabit.picker.datetime.DateTime
import com.liabit.picker.datetime.DateTimePickerDialog
import com.liabit.picker.datetime.Time
import kotlinx.android.synthetic.main.activity_picker_test.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class TestPickerActivity : AppCompatActivity() {

    private var mAddress: Address? = null

    private lateinit var mMinDate: Calendar
    private lateinit var mMaxDate: Calendar

    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val mTimeFormat = SimpleDateFormat("HH:mm")
    private val mDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_test)

        mMinDate = Calendar.getInstance()
        mMaxDate = Calendar.getInstance()

        mMinDate.set(2020, 9, 10)
        mMinDate.set(2030, 12, 30)

        time12Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        time12BoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setMinTime(8, 9)
                    .setMaxTime(15, 35)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        time24Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(true)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        time24BoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(true)
                    .setMinTime(8, 9)
                    .setMaxTime(15, 45)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        datePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateChangedListener(object : DateTimePickerDialog.OnDateChangeListener {
                        override fun onDateChanged(dialog: DateTimePickerDialog, date: Date) {
                            dateTimeText.text = mDateFormat.format(date.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        dateBoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setMinDate(2020, 9, 10)
                    .setMaxDate(2030, 10, 10)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateChangedListener(object : DateTimePickerDialog.OnDateChangeListener {
                        override fun onDateChanged(dialog: DateTimePickerDialog, date: Date) {
                            dateTimeText.text = mDateFormat.format(date.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        dateTimePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateTimeChangedListener(object : DateTimePickerDialog.OnDateTimeChangeListener {
                        override fun onDateTimeChanged(dialog: DateTimePickerDialog, dateTime: DateTime) {
                            dateTimeText.text = mDateTimeFormat.format(dateTime.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        dateTimeBoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setMinDateTime(2008, 9, 12, 8, 23)
                    .setMaxDateTime(2030, 10, 20, 20, 40)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateTimeChangedListener(object : DateTimePickerDialog.OnDateTimeChangeListener {
                        override fun onDateTimeChanged(dialog: DateTimePickerDialog, dateTime: DateTime) {
                            dateTimeText.text = mDateTimeFormat.format(dateTime.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        addressPicker.setOnClickListener {
            val dialog = AddressPickerDialog.Builder(this)
                    .setAutoUpdateTitle(true)
                    .setDefaultAddress(mAddress)
                    .setOnAddressChangedListener(object : AddressPickerDialog.OnAddressChangedListener {
                        override fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {
                            Log.d("TTTT", "picked address: $address")
                            mAddress = address
                            addressText.text = address.formatted
                        }
                    })
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
    }
}