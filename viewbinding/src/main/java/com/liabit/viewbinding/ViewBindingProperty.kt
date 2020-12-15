package com.liabit.viewbinding

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Author:         songtao
 * CreateDate:     2020/12/1 12:52
 */
inline fun <reified VB : ViewBinding> inflate(): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty(null, VB::class.java)
}

inline fun <reified VB : ViewBinding> bind(view: View): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty({ view }, VB::class.java)
}

inline fun <reified VB : ViewBinding> bind(noinline viewProvider: () -> View): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty(viewProvider, VB::class.java)
}

/**
 * if [Fragment.getView] is null, will use inflate method to create ViewBinding.
 */
inline fun <reified VB : ViewBinding> Fragment.bind(): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty(this::getView, VB::class.java)
}

/**
 * 需要保证 Fragment 的 view 已经创建
 */
inline fun <reified VB : ViewBinding> Fragment.bind(viewId: Int): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty({ requireView().findViewById(viewId) }, VB::class.java)
}

inline fun <reified VB : ViewBinding> RecyclerView.ViewHolder.bind(): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty({ itemView }, VB::class.java)
}

/**********以下非 inline 方法通过 泛型 查找来生成具体的 VB 对象***********/

/**
 * 通过查找 [clazz] 上的泛型来生成具体的 VB 对象
 */
fun <VB : ViewBinding> genericBinding(clazz: Class<*>): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty(null, findViewBindingClass(clazz))
}

/**
 * 通过查找 [Activity] 上的泛型来生成具体的 VB 对象
 * Returns a property delegate to access [ViewBinding],
 *
 * ```
 * class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
 *     val binding by genericBinding<VB>()
 * }
 * ```
 */
fun <VB : ViewBinding> Activity.genericBinding(): ReadOnlyProperty<Any, VB> {
    return genericBinding(this.javaClass)
}

/**
 * 通过查找 [clazz] 上的泛型来生成具体的 VB 对象
 */
fun <VB : ViewBinding> Fragment.genericBinding(clazz: Class<*>): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty(this::getView, findViewBindingClass(clazz))
}

/**
 * 通过查找 [Fragment] 上的泛型来生成具体的 VB 对象
 * Returns a property delegate to access [ViewBinding],
 *
 * ```
 * class BaseFragment<VB : ViewBinding> : Fragment() {
 *     val binding by genericBinding<VB>()
 * }
 * ```
 */
fun <VB : ViewBinding> Fragment.genericBinding(): ReadOnlyProperty<Any, VB> {
    return genericBinding(this.javaClass)
}

@Suppress("UNCHECKED_CAST")
class ViewBindingProperty<VB : ViewBinding>(private val viewProvider: (() -> View?)?, private val viewBindingClass: Class<VB>) : ReadOnlyProperty<Any, VB> {

    /**
     * Cache static method `ViewBinding.bind(View)`
     */
    private val bindMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.getMethod("bind", View::class.java)
    }

    /**
     * Cache static method `ViewBinding.inflate(LayoutInflater)`
     */
    private val inflateMethod by lazy(LazyThreadSafetyMode.NONE) {
        viewBindingClass.getMethod("inflate", LayoutInflater::class.java)
    }

    private var thisRef: Any? = null
    private var viewBinding: VB? = null
    private val mainHandler = Handler(Looper.getMainLooper())
    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            /*thisRef?.let {
                when (it) {
                    is Fragment -> it.lifecycle
                    is ComponentActivity -> it.lifecycle
                    is LifecycleOwner -> it.lifecycle
                    else -> null
                }?.removeObserver(this)
            }*/
            owner.lifecycle.removeObserver(this)
            mainHandler.post { viewBinding = null }
        }
    }

    @MainThread
    override fun getValue(thisRef: Any, property: KProperty<*>): VB {
        this.viewBinding?.let { return it }
        this.thisRef = thisRef

        when (thisRef) {
            is Fragment -> thisRef.lifecycle
            is ComponentActivity -> thisRef.lifecycle
            is LifecycleOwner -> thisRef.lifecycle
            else -> null
        }?.let {
            if (it.currentState == Lifecycle.State.DESTROYED) {
                mainHandler.post { viewBinding = null }
            } else {
                it.addObserver(lifecycleObserver)
            }
        }

        val view = viewProvider?.invoke()
        Log.d("ViewBindingProperty", "view: $view  thisRef: $thisRef"/*, Throwable()*/)
        return if (view != null) {
            bind(view).also { viewBinding = it }
        } else {
            when (thisRef) {
                is Activity -> {
                    inflate(thisRef).also { viewBinding = it }
                }
                is Fragment -> {
                    inflate(thisRef).also { viewBinding = it }
                }
                is View -> {
                    inflate(thisRef.context).also { viewBinding = it }
                }
                is Context -> {
                    inflate(thisRef).also { viewBinding = it }
                }
                is RecyclerView.ViewHolder -> {
                    inflate(thisRef.itemView.context).also { viewBinding = it }
                }
                is ContextProvider -> {
                    inflate(thisRef.getContext()).also { viewBinding = it }
                }
                else -> {
                    throw IllegalStateException("Can't inflate or bind view!")
                }
            }
        }
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun bind(view: View): VB {
        var vb: VB? = null
        if (viewBindingClass.isAssignableFrom(ViewDataBinding::class.java)) {
            vb = DataBindingUtil.bind(view)
        }
        if (vb == null) {
            vb = bindMethod(null, view) as VB
        }
        return vb
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun inflate(inflater: LayoutInflater): VB {
        var vb: VB? = null
        if (viewBindingClass.isAssignableFrom(ViewDataBinding::class.java)) {
            val layoutId = getLayoutResource(inflater.context, viewBindingClass)
            if (layoutId != 0) {
                vb = DataBindingUtil.inflate(inflater, layoutId, null, false)
            }
        }
        if (vb == null) {
            vb = inflateMethod(null, inflater) as VB
        }
        return vb
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun inflate(context: Context): VB {
        return inflate(LayoutInflater.from(context))
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun inflate(fragment: Fragment): VB {
        return inflate(fragment.layoutInflater)
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun inflate(activity: Activity): VB {
        return inflate(activity.layoutInflater)
    }
}

interface ContextProvider {
    fun getContext(): Context
}
