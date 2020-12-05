package com.liabit.test.viewbinding

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.liabit.viewbinding.findViewBindingClass
import com.liabit.viewbinding.inflate

open class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    protected val binding by inflate<VB>(this.javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TTTT", "xxxxxxxxxxx")
        findViewBindingClass<VB>(this.javaClass)
    }

}