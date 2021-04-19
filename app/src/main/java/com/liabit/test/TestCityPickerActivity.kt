package com.liabit.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liabit.dialog.AlertDialogBuilder
import com.liabit.dialog.InputDialogBuilder
import com.liabit.extension.layoutUnderSystemUI
import com.liabit.extension.toStringArray
import com.liabit.imageviewer.PhotoViewer
import com.liabit.integratepicker.PhotoFlowAdapter
import com.liabit.integratepicker.PhotoSelector
import com.liabit.integratepicker.Picker
import com.liabit.integratepicker.R
import com.liabit.listpicker.OnResultListener
import com.liabit.listpicker.PickerFragment
import com.liabit.listpicker.model.Item
import com.liabit.test.databinding.ActivityTestCityPickerBinding
import com.liabit.viewbinding.inflate
import com.liabit.test.mock.Mock
import com.liabit.test.mock.MockPicture

class TestCityPickerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestCityPickerBinding>()

    private val photoSelector by lazy { PhotoSelector(this) }

    private var mCollegeIndex = 0

    private val mColleges = MutableList(16) {
        Mock.collegeName()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI(true)
        setContentView(binding.root)

        binding.showCityPicker.setOnClickListener {
            Picker.pickCity(this, false) {
                Log.d("TTTT", "result===>$it")
                Toast.makeText(this@TestCityPickerActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.showMultiCityPicker.setOnClickListener {
            Picker.pickCity(this, true) {
                Log.d("TTTT", "result===>$it")
                Toast.makeText(this@TestCityPickerActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        val uris = MutableList(MockPicture.size) {
            Uri.parse(MockPicture[it])
        }

        binding.testPhotoViewer.setOnClickListener {
            PhotoViewer.start(this, uris)
        }

        photoSelector.setMaxShow(8)
                .setAddButtonStyle(PhotoFlowAdapter.AddButtonStyle.BORDER)
                .setShowAddWhenFull(false)
                .setLastAsAdd(true)
                .bind(binding.photos)

        binding.pickPhoto.setOnClickListener {
            Picker.pickPhoto(this)
        }

        binding.pickSchool.setOnClickListener {
            PickerFragment.Builder<School>()
                    .setFragmentManager(supportFragmentManager)
                    .setAnimationStyle(R.style.DefaultListPickerAnimation)
                    .setMultipleMode(false)
                    .setSectionEnabled(false)
                    .setSearchHint("学校名称")
                    .setItem(MutableList(40) {
                        School(Mock.schoolName())
                    })
                    .setOnResultListener(object : OnResultListener<School> {
                        override fun onResult(data: List<School>) {
                            Toast.makeText(this@TestCityPickerActivity, data.toString(), Toast.LENGTH_SHORT).show()
                        }
                    })
                    .show()
        }

        binding.pickCollege.setOnClickListener {
            val values = mColleges.toTypedArray()
            Picker.pick(this, "选择院系", mCollegeIndex, values) {
                Toast.makeText(this@TestCityPickerActivity, it.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        binding.pickWeight.setOnClickListener {
            val weight1 = Array(71) {
                "${30 + it}"
            }
            val weight2 = Array(10) {
                "$it"
            }
            com.liabit.numberpicker.PickerFragment.Builder()
                    .setTitle("选择体重")
                    .setColumn(column1Values = weight1.toStringArray(), column2Values = weight2.toStringArray())
                    .setCenterText(".")
                    .setColumnTextGravity(Gravity.END, Gravity.START)
                    .setOnIndexChangeListener { i1, i2 ->
                        Toast.makeText(this@TestCityPickerActivity, "${weight1[i1]}.${weight2[i2]}", Toast.LENGTH_SHORT).show()
                    }
                    .setValue(value1 = 0, value2 = 0)
                    .show(supportFragmentManager)
        }

        binding.inputDialog.setOnClickListener {
            InputDialogBuilder(this)
                    .setTitle("请输入姓名")
                    .setOnConfirmListener { _, text ->
                        Toast.makeText(this@TestCityPickerActivity, text, Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }
        binding.alertDialog.setOnClickListener {
            AlertDialogBuilder(this)
                    .setTitle("温馨提示")
                    .setMessage("确定要删除吗？")
                    .setOnConfirmListener {
                        Toast.makeText(this@TestCityPickerActivity, "alert dialog", Toast.LENGTH_SHORT).show()
                    }
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoSelector.onActivityResult(requestCode, resultCode, data)
    }

    data class School(private val name: String) : Item {
        override fun getItemTitle(): String {
            return name
        }

        override fun getItemSubtitle(): String {
            return ""
        }
    }

}