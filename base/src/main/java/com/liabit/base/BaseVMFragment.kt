package com.liabit.base

import androidx.lifecycle.ViewModel
import com.liabit.viewmodel.ApplicationViewModel
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseVMFragment<VM : ViewModel> : BaseCompatFragment() {
    protected open val viewModel by genericViewModels<VM>()

    override fun onBeforeBindView() {
        val vm = viewModel
        if (vm is ApplicationViewModel) {
            vm.observeDialog(viewLifecycleOwner) {
                if (it.show) {
                    showDialog(it.message)
                } else {
                    dismissDialog()
                }
            }
        }
    }

}