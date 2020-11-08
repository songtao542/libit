package com.liabit.test

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.picker.address.Address
import com.liabit.picker.address.AddressPickerDialog
import com.liabit.picker.datetime.DateTimePickerDialog
import kotlinx.android.synthetic.main.activity_picker_test.*
import java.util.*

class PickerTestActivity : AppCompatActivity() {

    private var mAddress: Address? = null

    private lateinit var mMinDate: Calendar
    private lateinit var mMaxDate: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_test)

        mMinDate = Calendar.getInstance()
        mMaxDate = Calendar.getInstance()

        mMinDate.set(2020, 9, 10)
        mMinDate.set(2030, 12, 30)

        datePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setMinDate(2020, 9, 10)
                    .setMaxDate(2030, 10, 10)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        timePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        time24Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .set24HourFormat(false)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        dateTimePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
        lunarPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
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
                        }
                    })
                    .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ -> })
                    .setPositiveButton(R.string.confirm, DialogInterface.OnClickListener { _, _ -> })
                    .create()
            dialog.show()
        }
    }
}