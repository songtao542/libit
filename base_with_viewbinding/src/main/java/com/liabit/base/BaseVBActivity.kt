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

    override fun onCreateView(inflater: LayoutInflater, savedInstanceState: Bundle?): View? {
        return binding.root
    }

}