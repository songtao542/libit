package com.liabit.test14

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.liabit.location.LocationPicker
import com.liabit.location.LocationPickerFragment
import com.liabit.location.LocationViewer
import com.liabit.test14.databinding.ActivityTestMapLocationPickerBinding
import com.liabit.viewbinding.inflate

class TestMapLocationPicker : AppCompatActivity() {

    private val binding by inflate<ActivityTestMapLocationPickerBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_map_location_picker)

        binding.pick.setOnClickListener {
            val picker = LocationPicker.newInstance()
            picker.setOnResultListener {
                Log.d("TTTT", "open location viewer: $it")
                val trans1 = supportFragmentManager.beginTransaction()
                trans1.add(android.R.id.content, LocationViewer.newInstance(it.title, it.address, it.latitude, it.longitude),
                        "viewer")
                trans1.addToBackStack("viewer")
                trans1.commitAllowingStateLoss()
            }
            val trans = supportFragmentManager.beginTransaction()
            trans.add(android.R.id.content, picker, "picker")
            trans.addToBackStack("picker")
            trans.commitAllowingStateLoss()
        }


        binding.pick1.setOnClickListener {
            val trans = supportFragmentManager.beginTransaction()
            trans.add(android.R.id.content, LocationPickerFragment.newInstance(), "picker")
            trans.addToBackStack(null)
            trans.commitAllowingStateLoss()
        }
    }

    override fun finish() {
        super.finish()
        Log.d("TTTT", "finished", Throwable())
    }
}
