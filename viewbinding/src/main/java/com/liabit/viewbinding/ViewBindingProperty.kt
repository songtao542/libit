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
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
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
    return ViewBindingProperty(requireView().findViewById(viewId), VB::class.java)
}

inline fun <reified VB : ViewBinding> RecyclerView.ViewHolder.bind(): ReadOnlyProperty<Any, VB> {
    return ViewBindingProperty({ itemView }, VB::class.java)
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
            thisRef?.let {
                when (it) {
                    is Fragment -> {
                        it.lifecycle.removeObserver(this)
                        it.viewLifecycleOwner.lifecycle.removeObserver(this)
                    }
                    is ComponentActivity -> {
                        it.lifecycle.removeObserver(this)
                    }
                    is LifecycleOwner -> {
                        it.lifecycle.removeObserver(this)
                    }
                }
            }
            mainHandler.post { viewBinding = null }
        }
    }

    @MainThread
    override fun getValue(thisRef: Any, property: KProperty<*>): VB {
        this.viewBinding?.let { return it }
        this.thisRef = thisRef

        when (thisRef) {
            is Fragment -> thisRef.viewLifecycleOwner.lifecycle
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
        Log.d("ViewBindingProperty", "view: $view  thisRef: $thisRef", Throwable())
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
        return bindMethod(null, view) as VB
    }

    /**
     * Create new [ViewBinding] instance
     */
    private fun inflate(layoutInflater: LayoutInflater): VB {
        return inflateMethod(null, layoutInflater) as VB
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
