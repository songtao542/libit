package com.liabit.test.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
open class BaseActivity<VM : ViewModel, VB : ViewBinding> : BaseVBActivity<VB>() {
    protected val viewModel by genericViewModels<VM>()
}