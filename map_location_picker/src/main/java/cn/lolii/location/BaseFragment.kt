package cn.lolii.location

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    private var progressDialog: ProgressDialog? = null

    fun enableOptionsMenu(toolbar: Toolbar?, showTitle: Boolean = true, menu: Int = 0) {
        toolbar?.let { toolbar ->
            activity?.let { activity ->
                setHasOptionsMenu(true)
                if (activity is AppCompatActivity) {
                    //activity.setSupportActionBar(toolbar)
                    if (menu != 0) {
                        toolbar.inflateMenu(menu)
                    }
                    activity.supportActionBar?.setDisplayShowTitleEnabled(showTitle)
                }
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

}