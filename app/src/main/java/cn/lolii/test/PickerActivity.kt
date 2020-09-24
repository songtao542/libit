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
                    .setNegativeButton(R.string.cancel,object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }
                    } )
                    .setPositiveButton(R.string.confirm, object : DialogInterface.OnClickListener {
                        override fun onClick(p0: DialogInterface?, p1: Int) {

                        }
                    })
                    .create()
            dialog.show()
        }
    }
}