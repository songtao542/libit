package com.liabit.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.liabit.location.databinding.MapLocationProgressDialogBinding
import com.liabit.viewbinding.bind

class ProgressDialog : DialogFragment() {

    companion object {
        private const val TITLE = "title"

        @JvmStatic
        fun newInstance(tip: String? = null) = ProgressDialog().apply {
            arguments = Bundle().apply {
                putString(TITLE, tip)
            }
        }
    }

    private var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(TITLE, null)
        }
    }

    private val binding by bind<MapLocationProgressDialogBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_location_progress_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        title?.let {
            binding.titleView.visibility = View.VISIBLE
            binding.titleView.text = title
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