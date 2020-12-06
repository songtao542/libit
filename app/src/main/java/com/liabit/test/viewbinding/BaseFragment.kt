package com.liabit.test.viewbinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewbinding.genericBinding
import com.liabit.viewmodel.genericViewModels

abstract class BaseFragment<VM : ViewModel, VB : ViewBinding> : Fragment() {
    protected val binding by genericBinding<VB>()

    protected val viewModel by genericViewModels<VM>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutResource(), container, false)
    }

    abstract fun getLayoutResource(): Int

}