package com.liabit.location

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

abstract class MapBaseFragment : Fragment() {

    companion object {
        private const val MAP_LOCATION_TAG = "map_location"
    }

    private var progressDialog: ProgressDialog? = null

    fun enableOptionsMenu(toolbar: Toolbar?, showTitle: Boolean = true, menu: Int = 0) {
        val activity = activity ?: return
        toolbar?.let {
            setHasOptionsMenu(true)
            if (activity is AppCompatActivity) {
                //activity.setSupportActionBar(toolbar)
                if (menu != 0) {
                    it.inflateMenu(menu)
                }
                activity.supportActionBar?.setDisplayShowTitleEnabled(showTitle)
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    fun showProgress(tip: String? = null, cancelable: Boolean = false) {
        progressDialog?.dismiss()
        progressDialog = ProgressDialog.newInstance(tip)
                .cancelable(cancelable)
                .show(childFragmentManager)
    }

    fun dismissProgress() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    fun show(activity: FragmentActivity) {
        if (activity.supportFragmentManager.findFragmentByTag(MAP_LOCATION_TAG) == null) {
            activity.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.map_location_anim_right_enter, R.anim.map_location_anim_right_exit,
                            R.anim.map_location_anim_right_enter, R.anim.map_location_anim_right_exit
                    )
                    .add(android.R.id.content, this, MAP_LOCATION_TAG)
                    .addToBackStack(MAP_LOCATION_TAG)
                    .commitAllowingStateLoss()
        } else {
            activity.supportFragmentManager
                    .beginTransaction()
                    .setCustomAnimations(
                            R.anim.map_location_anim_right_enter, R.anim.map_location_anim_right_exit,
                            R.anim.map_location_anim_right_enter, R.anim.map_location_anim_right_exit
                    )
                    .addToBackStack(MAP_LOCATION_TAG)
                    .show(this)
                    .commitAllowingStateLoss()
        }
    }

}