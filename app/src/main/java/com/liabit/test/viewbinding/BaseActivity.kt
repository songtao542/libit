package com.liabit.test.viewbinding

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding
import com.liabit.viewbinding.findViewBindingClass
import com.liabit.viewbinding.genericBinding
import com.liabit.viewmodel.genericViewModels

open class BaseActivity<VM : ViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected val binding by genericBinding<VB>()

    protected val viewModel by genericViewModels<VM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBindingClass = findViewBindingClass<VB>(this.javaClass)
        Log.d("TTTT", "viewBindingClass: $viewBindingClass")
    }

}