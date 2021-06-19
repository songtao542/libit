package com.liabit.imageloader

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

@Suppress("DEPRECATION")
class LifeCycleFragment : Fragment(), LifecycleOwner {

    companion object {

        private const val FRAGMENT_TAG = "imageloader.lifecycle.fragment"

        private val mTempMap = HashMap<Context, LifeCycleFragment>()

        fun getLifecycleOwner(activity: Activity): LifecycleOwner {
            val fm = activity.fragmentManager
            var current = mTempMap[activity] ?: (fm.findFragmentByTag(FRAGMENT_TAG) as? LifeCycleFragment)
            if (current == null) {
                current = LifeCycleFragment().also { mTempMap[activity] = it }
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss()
            }
            return current
        }
    }

    private val mLifecycleRegistry = LifecycleRegistry(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // 移除临时记录
        mTempMap.remove(context)
    }

    override fun onStart() {
        super.onStart()
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
    }

    override fun onStop() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        super.onStop()
    }

    override fun onDestroyView() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroyView()
    }

    override fun onDestroy() {
        mLifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

}