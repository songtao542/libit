package com.liabit.test.viewbinding

import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.liabit.viewbinding.bind

open class BaseFragment<VB : ViewBinding> : Fragment() {
    protected val binding by bind<VB>(this.javaClass)
}