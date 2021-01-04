package com.liabit.test

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liabit.imageviewer.PhotoViewer
import com.liabit.imageviewer.PhotoViewerActivity
import com.liabit.integratepicker.Picker
import com.liabit.test.databinding.ActivityTestCityPickerBinding
import com.liabit.viewbinding.inflate

class TestCityPickerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestCityPickerBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}