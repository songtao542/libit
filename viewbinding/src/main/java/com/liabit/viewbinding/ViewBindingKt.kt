package com.liabit.viewbinding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

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

@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> findViewBindingClass(clazz: Class<*>): Class<VB> {
    val types: Array<Type>? = (clazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    if (!types.isNullOrEmpty()) {
        for (type in types) {
            val interfaces = (type as? Class<*>)?.interfaces
            if (interfaces != null) {
                for (face in interfaces) {
                    if (face.isAssignableFrom(ViewBinding::class.java)) {
                        return type as Class<VB>
                    }
                }
            }
        }
    }
    throw IllegalStateException("Not found Generic Type")
}
