package com.liabit.base

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
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

    protected open val viewModel by genericViewModels<VM>(onInitialized = {
        observeDialog(it)
        return@genericViewModels false
    })

    protected open val activityViewModel by genericActivityViewModels<VM>(onInitialized = {
        observeDialog(it)
        return@genericActivityViewModels false
    })

    private var mDialogObserverAdded = false

    private val mDialogObserver = object : Observer<ApplicationViewModel.DialogMessage> {
        override fun onChanged(it: ApplicationViewModel.DialogMessage?) {
            if (it == null) return
            if (it.show) {
                showDialog(it.message, it.cancellable)
            } else {
                dismissDialog()
            }
        }
    }

    private fun observeDialog(viewModel: ViewModel) {
        if (!mDialogObserverAdded && lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (viewModel is ApplicationViewModel) {
                mDialogObserverAdded = true
                viewModel.observeDialog(viewLifecycleOwner, mDialogObserver)
            }
        }
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        mDialogObserverAdded = false
    }
}