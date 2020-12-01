package com.liabit.viewbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding

/**
 * Author:         songtao
 * CreateDate:     2020/12/1 16:52
 */
inline fun <reified VB : ViewBinding> inflate(viewBindingClass: Class<VB>,
                                              layoutInflater: LayoutInflater,
                                              parent: ViewGroup?,
                                              attachToParent: Boolean): VB {
    val inflateMethod = viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
    return inflateMethod(null, layoutInflater, parent, attachToParent) as VB
}

inline fun <reified VB : ViewBinding> bind(viewBindingClass: Class<VB>, view: View): VB {
    val bindMethod = viewBindingClass.getMethod("bind", View::class.java)
    return bindMethod(null, view) as VB
}
