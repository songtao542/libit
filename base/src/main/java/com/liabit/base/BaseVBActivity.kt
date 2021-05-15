package com.liabit.base

import android.os.Bundle
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
        if (!onSetContentView()) {
            setContentView(binding.root)
        }

        onInitialize(savedInstanceState)
    }

    protected open fun onSetContentView(): Boolean {
        return false
    }

    protected open fun onBeforeInitialize() {
    }

    protected open fun onInitialize(savedInstanceState: Bundle?) {
    }
}