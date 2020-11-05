package com.liabit.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.fragment_progress_dialog.*

class ProgressDialog : DialogFragment() {

    companion object {
        @JvmStatic
        fun newInstance(tip: String? = null) = ProgressDialog().apply {
            arguments = Bundle().apply {
                putString(Constants.Extra.TITLE, tip)
            }
        }
    }

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(Constants.Extra.TITLE, null)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title?.let {
            titleView.visibility = View.VISIBLE
            titleView.text = title
        }
    }

    fun cancelable(cancelable: Boolean): ProgressDialog {
        isCancelable = cancelable
        return this
    }

    fun show(fragmentManager: FragmentManager): ProgressDialog {
        show(fragmentManager, "progress")
        return this
    }

}