package com.liabit.viewbinding

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Author:         songtao
 * CreateDate:     2020/12/1 16:52
 */
val HUMP_PATTERN: Pattern = Pattern.compile("[A-Z]")

fun getLayoutResource(context: Context, viewBindingClass: Class<*>): Int {
    val viewDataBindingName = viewBindingClass.simpleName
    val matcher: Matcher = HUMP_PATTERN.matcher(viewDataBindingName)
    val sb = StringBuffer()
    while (matcher.find()) {
        val match = matcher.group(0)
        if (match != null) {
            matcher.appendReplacement(sb, "_" + match.toLowerCase(Locale.ROOT))
        }
    }
    matcher.appendTail(sb)
    val index = sb.lastIndexOf("_")
    val resourceName = sb.substring(1).substring(0, index - 1)
    return context.resources.getIdentifier(resourceName, "layout", context.packageName)
}

inline fun <reified VB : ViewBinding> inflate(viewBindingClass: Class<VB>,
                                              inflater: LayoutInflater,
                                              parent: ViewGroup?,
                                              attachToParent: Boolean): VB {
    var vb: VB? = null
    if (viewBindingClass.isAssignableFrom(ViewDataBinding::class.java)) {
        val layoutId = getLayoutResource(inflater.context, viewBindingClass)
        if (layoutId != 0) {
            vb = DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent)
        }
    }
    if (vb == null) {
        val inflateMethod = viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
        vb = inflateMethod(null, inflater, parent, attachToParent) as VB
    }
    return vb
}

inline fun <reified VB : ViewBinding> inflate(viewBindingClass: Class<VB>,
                                              inflater: LayoutInflater,
                                              layoutId: Int,
                                              parent: ViewGroup?,
                                              attachToParent: Boolean): VB {
    var vb: VB? = null
    if (viewBindingClass.isAssignableFrom(ViewDataBinding::class.java)) {
        vb = DataBindingUtil.inflate(inflater, layoutId, parent, attachToParent)
    }
    if (vb == null) {
        val inflateMethod = viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
        vb = inflateMethod(null, inflater, parent, attachToParent) as VB
    }
    return vb
}

inline fun <reified VB : ViewBinding> bind(viewBindingClass: Class<VB>, view: View): VB {
    var vb: VB? = null
    if (viewBindingClass.isAssignableFrom(ViewDataBinding::class.java)) {
        vb = DataBindingUtil.bind(view)
    }
    if (vb == null) {
        val bindMethod = viewBindingClass.getMethod("bind", View::class.java)
        vb = bindMethod(null, view) as VB
    }
    return vb
}


@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> findViewBindingClass(clazz: Class<*>): Class<VB> {
    val types: Array<Type>? = (clazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    if (!types.isNullOrEmpty()) {
        for (type in types) {
            val typeClazz = (type as? Class<*>) ?: continue
            if (ViewBinding::class.java.isAssignableFrom(typeClazz)) {
                return type as Class<VB>
            }
        }
    }
    throw IllegalStateException("Not found Generic Type")
}



