package com.liabit.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liabit.extension.layoutUnderSystemUI
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
import com.sport.day.net.Mock
import com.sport.day.net.MockPicture

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
                    .setAnimationStyle(R.style.DefaultCityPickerAnimation)
                    .setMultipleMode(false)
                    .setSectionEnabled(false)
                    .setSearchHint("学校名称")
                    .setItem(MutableList(10) {
                        School(Mock.schoolName(), Mock.collegeName())
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoSelector.onActivityResult(requestCode, resultCode, data)
    }

    class School(private val name: String, private val subname: String) : Item {
        override fun getItemTitle(): String {
            return name
        }

        override fun getItemSubtitle(): String {
            return subname
        }

        override fun toString(): String {
            return name
        }
    }

}