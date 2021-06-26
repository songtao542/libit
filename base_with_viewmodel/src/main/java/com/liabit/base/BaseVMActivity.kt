package com.liabit.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.liabit.viewmodel.ApplicationViewModel
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseVMActivity<VM : ViewModel> : BaseCompatActivity() {

    protected val viewModel by genericViewModels<VM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vm = viewModel
        if (vm is ApplicationViewModel) {
            vm.observeDialog(this) {
                if (it.show) {
                    showDialog(it.message, it.cancellable)
                } else {
                    dismissDialog()
                }
            }
        }
    }

}