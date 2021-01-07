package com.liabit.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.extension.layoutUnderSystemUI
import com.liabit.imageviewer.PhotoViewer
import com.liabit.integratepicker.PhotoFlowAdapter
import com.liabit.integratepicker.PhotoSelector
import com.liabit.integratepicker.Picker
import com.liabit.test.databinding.ActivityTestCityPickerBinding
import com.liabit.viewbinding.inflate

class TestCityPickerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestCityPickerBinding>()

    private val photoSelector by lazy { PhotoSelector(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI(true)
        setContentView(binding.root)

        binding.showCityPicker.setOnClickListener {
            Picker.pickCity(this, false) {
                Log.d("TTTT", "result===>$it")
            }
        }

        val uris = MutableList(MockPictures.size) {
            Uri.parse(MockPictures[it])
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoSelector.onActivityResult(requestCode, resultCode, data)
    }

}