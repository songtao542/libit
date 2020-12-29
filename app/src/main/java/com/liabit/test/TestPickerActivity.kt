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
import com.liabit.test.databinding.ActivityPickerTestBinding
import com.liabit.viewbinding.inflate
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
class TestPickerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityPickerTestBinding>()

    private var mAddress: Address? = null

    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd")
    private val mTimeFormat = SimpleDateFormat("HH:mm")
    private val mDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.time12Picker.setOnClickListener {
            Log.d("TTTT", "vvvvvvvvvvvvvvvvvvv: ")
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            binding.dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.time12BoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setMinTime(8, 23)
                    .setMaxTime(20, 40)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            binding.dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.time24Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(true)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            binding.dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.time24BoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(true)
                    .setMinTime(8, 23)
                    .setMaxTime(20, 40)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnTimeChangedListener(object : DateTimePickerDialog.OnTimeChangeListener {
                        override fun onTimeChanged(dialog: DateTimePickerDialog, time: Time) {
                            binding.dateTimeText.text = mTimeFormat.format(time.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.datePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateChangedListener(object : DateTimePickerDialog.OnDateChangeListener {
                        override fun onDateChanged(dialog: DateTimePickerDialog, date: Date) {
                            binding.dateTimeText.text = mDateFormat.format(date.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.dateBoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setMinDate(2008, 9, 12)
                    .setMaxDate(2030, 10, 20)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateChangedListener(object : DateTimePickerDialog.OnDateChangeListener {
                        override fun onDateChanged(dialog: DateTimePickerDialog, date: Date) {
                            binding.dateTimeText.text = mDateFormat.format(date.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.dateTimePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateTimeChangedListener(object : DateTimePickerDialog.OnDateTimeChangeListener {
                        override fun onDateTimeChanged(dialog: DateTimePickerDialog, dateTime: DateTime) {
                            binding.dateTimeText.text = mDateTimeFormat.format(dateTime.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.dateTimeBoundPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setMinDateTime(2008, 9, 12, 8, 23)
                    .setMaxDateTime(2030, 10, 20, 20, 40)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .setOnDateTimeChangedListener(object : DateTimePickerDialog.OnDateTimeChangeListener {
                        override fun onDateTimeChanged(dialog: DateTimePickerDialog, dateTime: DateTime) {
                            binding.dateTimeText.text = mDateTimeFormat.format(dateTime.calendar.time)
                        }
                    })
                    .create()
            dialog.show()
        }
        binding.addressPicker.setOnClickListener {
            val dialog = AddressPickerDialog.Builder(this)
                    .setAutoUpdateTitle(true)
                    .setDefaultAddress(mAddress)
                    .setOnAddressChangedListener(object : AddressPickerDialog.OnAddressChangedListener {
                        override fun onAddressChanged(dialog: AddressPickerDialog, address: Address) {
                            Log.d("TTTT", "picked address: $address")
                            mAddress = address
                            binding.addressText.text = address.formatted
                        }
                    })
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.ml_confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
    }
}