package com.liabit.test

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.liabit.imageviewer.PhotoViewer
import com.liabit.imageviewer.PhotoViewerActivity
import com.liabit.integratepicker.PhotoFlowAdapter
import com.liabit.integratepicker.Picker
import com.liabit.test.databinding.ActivityTestCityPickerBinding
import com.liabit.viewbinding.bind
import com.liabit.viewbinding.inflate
import com.zhihu.matisse.Matisse

class TestCityPickerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestCityPickerBinding>()

    private val mAdapter by lazy { PhotoFlowAdapter(this) }

    private val mUris by lazy { ArrayList<Uri>() }

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

        mAdapter.setMaxShow(8)
                .setAddButtonStyle(PhotoFlowAdapter.AddButtonStyle.BORDER)
                .setShowAddWhenFull(false)
                .setUris(mUris)
                .setLastAsAdd(true)
                .setOnAddClickListener {
                    Picker.pickPhoto(this, max = 8)
                }

        binding.photos.setOnItemClickListener { _, index ->
//            val fragment = PhotoViewerFragment.newInstance(Bundle().apply {
//                putParcelableArrayList(Constants.Extra.URI_LIST, selectedPhotoUris)
//                putInt(Constants.Extra.INDEX, index)
//                putBoolean(Constants.Extra.DELETABLE, true)
//            }).apply {
//                setOnUriResultListener { deleted ->
//                    selectedPhotoUris.removeAll(deleted)
//                    photoView?.notifyAdapterSizeChanged()
//                }
//            }
//            activity?.addFragmentSafely(fragment, "photo_viewer", true)
        }

        binding.pickPhoto.setOnClickListener {
            Picker.pickPhoto(this)
        }

        binding.photos.setAdapter(mAdapter)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handlePhotoPick(requestCode, resultCode, data)
    }

    private fun handlePhotoPick(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Picker.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val paths = Matisse.obtainPathResult(data)
            val uris = Matisse.obtainResult(data)
            paths?.let {
                mUris.addAll(uris)
                binding.photos.notifyAdapterSizeChanged()
                return@let
            }
        }
    }

}