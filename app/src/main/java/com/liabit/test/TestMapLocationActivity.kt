package com.liabit.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.location.LocationBottomSheetDialog
import com.liabit.location.LocationPicker
import com.liabit.location.LocationPickerFragment
import com.liabit.location.LocationViewer
import com.liabit.location.model.PoiAddress
import com.liabit.test.databinding.ActivityTestMapLocationBinding
import com.liabit.viewbinding.inflate

class TestMapLocationActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestMapLocationBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.locationPicker -> {
                LocationPicker.newInstance().show(this)
            }
            R.id.locationPickerFragment -> {
                LocationPickerFragment.newInstance().show(this)
            }
            R.id.locationBottomSheetDialog -> {
                LocationBottomSheetDialog.newInstance("中科大厦", arrayListOf(
                        PoiAddress(title = "中科大厦", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                        PoiAddress(title = "中科大厦停车场", province = "广东省", city = "深圳市", address = "高新南路"),
                )).show(supportFragmentManager, "map_location")
            }
            R.id.locationViewer -> {
                LocationViewer.newInstance(
                        title = "中科大厦",
                        subTitle = "中科大厦",
                        latitude = 22.543959,
                        longitude = 113.95889
                ).show(this)
            }
        }
    }
}