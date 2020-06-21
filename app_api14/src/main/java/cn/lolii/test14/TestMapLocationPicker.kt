package cn.lolii.test14

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import cn.lolii.location.LocationPicker
import cn.lolii.location.LocationPickerFragment
import cn.lolii.location.LocationViewer
import kotlinx.android.synthetic.main.activity_test_map_location_picker.*

class TestMapLocationPicker : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_map_location_picker)

        pick.setOnClickListener {
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


        pick1.setOnClickListener {
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
