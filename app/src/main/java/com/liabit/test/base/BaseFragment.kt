package com.liabit.test.base

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewmodel.genericActivityViewModels
import com.liabit.viewmodel.genericViewModels

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:05
 */
abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : BaseVBFragment<VB>() {

    protected open val viewModel by genericViewModels<VM>()

    protected open val activityViewModel by genericActivityViewModels<VM>()

}