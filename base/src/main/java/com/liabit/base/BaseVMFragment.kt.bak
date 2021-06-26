package com.liabit.base

import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.liabit.viewmodel.ApplicationViewModel
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseVMFragment<VM : ViewModel> : BaseCompatFragment() {

    protected open val viewModel by genericViewModels<VM>()

    @CallSuper
    override fun onViewCreated(activity: FragmentActivity) {
        val vm = viewModel
        if (vm is ApplicationViewModel) {
            vm.observeDialog(viewLifecycleOwner) {
                if (it.show) {
                    showDialog(it.message, it.cancellable)
                } else {
                    dismissDialog()
                }
            }
        }
    }

}