package cn.lolii.test

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.lolii.picker.datepicker.DateTimePickerDialog
import cn.lolii.test.test.R
import kotlinx.android.synthetic.main.activity_picker.*

class PickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        datePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(false)
                    .setTitle("选择日期")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
        timePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
        time24Picker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(false)
                    .setWithTime(true)
                    .setTimeShow24Hour(false)
                    .setTitle("选择时间")
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
        dateTimePicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
        lunarPicker.setOnClickListener {
            val dialog = DateTimePickerDialog.Builder(this)
                    .setWithDate(true)
                    .setWithTime(true)
                    .setChangeDateModeEnable(true)
                    .setNegativeButton(R.string.cancel) { _, _ -> }
                    .setPositiveButton(R.string.confirm) { _, _ -> }
                    .create()
            dialog.show()
        }
    }
}