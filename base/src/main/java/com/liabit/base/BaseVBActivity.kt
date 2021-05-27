package com.liabit.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.liabit.viewbinding.genericBinding

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BaseVBActivity<VB : ViewBinding> : BaseCompatActivity() {

    protected val binding by genericBinding<VB>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = onCreateView(LayoutInflater.from(this), savedInstanceState) ?: binding.root
        setContentView(view)
        onViewCreated()
        onViewCreated(savedInstanceState)
    }

    protected open fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return null
    }

    protected open fun onViewCreated() {
    }

    protected open fun onViewCreated(savedInstanceState: Bundle?) {
    }
}