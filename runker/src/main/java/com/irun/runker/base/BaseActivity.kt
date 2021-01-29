package com.irun.runker.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
open class BaseActivity<VM : ViewModel, VB : ViewBinding> : BaseVBActivity<VB>() {
    protected val viewModel by genericViewModels<VM>()

    private var mToast = false

    @CallSuper
    override fun onInitialize(savedInstanceState: Bundle?) {
        (viewModel as? AppViewModel)?.let {
            it.progress.observe(this) { progress ->
                showProgressBar(progress)
            }
            it.error.observe(this) { error ->
                if (mToast) {
                    showToast(error)
                }
            }
            it.success.observe(this) { success ->
                if (mToast) {
                    showToast(success)
                }
            }
        }
    }

    protected open fun setShowToast(toast: Boolean) {
        mToast = toast
    }
}