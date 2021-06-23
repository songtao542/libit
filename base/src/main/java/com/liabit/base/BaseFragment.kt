package com.liabit.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.liabit.viewmodel.ApplicationViewModel
import com.liabit.viewmodel.genericActivityViewModels
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : BaseVBFragment<VB>() {

    protected open val viewModel by genericViewModels<VM>(onInitialized = {
        observeDialog(it)
    })

    protected open val activityViewModel by genericActivityViewModels<VM>(onInitialized = {
        observeDialog(it)
    })

    private fun observeDialog(viewModel: ViewModel) {
        lifecycleScope.launchWhenResumed {
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
}