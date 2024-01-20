package com.liabit.viewbinding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
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

fun <VB : ViewBinding> getLayoutResource(context: Context, clazz: Class<*>): Int {
    val viewBindingClass = findViewBindingClassOrNull<VB>(clazz)
            ?: throw IllegalStateException("Not found Generic Type of ViewBinding in $clazz")
    return getLayoutResource(context, viewBindingClass.simpleName)
}

fun <VB : ViewBinding> inflate(fragment: Fragment, inflater: LayoutInflater, container: ViewGroup?): View {
    return inflater.inflate(getLayoutResource<VB>(inflater.context, fragment.javaClass), container, false)
}

fun getLayoutResource(context: Context, viewBindingClassName: String): Int {
    val matcher: Matcher = HUMP_PATTERN.matcher(viewBindingClassName)
    val sb = StringBuffer()
    while (matcher.find()) {
        val match = matcher.group(0)
        if (match != null) {
            matcher.appendReplacement(sb, "_" + match.lowercase(Locale.ROOT))
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
    if (ViewDataBinding::class.java.isAssignableFrom(viewBindingClass)) {
        val layoutId = getLayoutResource(inflater.context, viewBindingClass.simpleName)
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
    if (ViewDataBinding::class.java.isAssignableFrom(viewBindingClass)) {
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
    if (ViewDataBinding::class.java.isAssignableFrom(viewBindingClass)) {
        vb = DataBindingUtil.bind(view)
    }
    if (vb == null) {
        val bindMethod = viewBindingClass.getMethod("bind", View::class.java)
        vb = bindMethod(null, view) as VB
    }
    return vb
}

fun <VB : ViewBinding> findViewBindingClass(clazz: Class<*>): Class<VB> {
    val viewBindingClass = findViewBindingClassOrNull<VB>(clazz)
    if (viewBindingClass != null) {
        return viewBindingClass
    }
    throw IllegalStateException("Not found Generic Type of ViewBinding in $clazz")
}

/**
 * 在泛型中查找 ViewBinding 的类型
 */
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> findViewBindingClassOrNull(clazz: Class<*>): Class<VB>? {
    val types: Array<Type>? = (clazz.genericSuperclass as? ParameterizedType)?.actualTypeArguments
    if (!types.isNullOrEmpty()) {
        for (type in types) {
            val typeClazz = (type as? Class<*>) ?: continue
            if (ViewBinding::class.java.isAssignableFrom(typeClazz)) {
                return type as Class<VB>
            }
        }
    }
    return null
}
