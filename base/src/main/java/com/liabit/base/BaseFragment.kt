package com.liabit.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewmodel.ApplicationViewModel
import com.liabit.viewmodel.genericActivityViewModels
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : BaseVBFragment<VB>() {

    protected open val viewModel by genericViewModels<VM>()

    protected open val activityViewModel by genericActivityViewModels<VM>()

    override fun onBeforeBindView() {
        observeDialog(viewModel)
        observeDialog(activityViewModel)
    }

    private fun observeDialog(viewModel: ViewModel) {
        if (viewModel is ApplicationViewModel) {
            viewModel.observeDialog(viewLifecycleOwner) {
                if (it.show) {
                    showDialog(it.message)
                } else {
                    dismissDialog()
                }
            }
        }
    }
}