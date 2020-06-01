package cn.lolii.test14

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cn.lolii.location.LocationPicker
import cn.lolii.location.LocationPickerFragment
import kotlinx.android.synthetic.main.activity_test_map_location_picker.*

class TestMapLocationPicker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_map_location_picker)

        pick.setOnClickListener {
            val trans = supportFragmentManager.beginTransaction()
            trans.add(android.R.id.content, LocationPicker.newInstance(), "picker")
            trans.addToBackStack(null)
            trans.commitAllowingStateLoss()
        }


        pick1.setOnClickListener {
            val trans = supportFragmentManager.beginTransaction()
            trans.add(android.R.id.content, LocationPickerFragment.newInstance(), "picker")
            trans.addToBackStack(null)
            trans.commitAllowingStateLoss()
        }
    }
}
