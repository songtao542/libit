package com.liabit.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PatternMatcher
import android.util.Log
import com.liabit.extension.TAG
import com.liabit.util.MainThreadInitializedObject.ObjectProvider
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/6/15 15:01
 *
 * Observer for the resource config that specifies the navigation bar mode.
 */
class SystemNavigationMode(private val mContext: Context) {

    enum class Mode(val hasGestures: Boolean, val resValue: Int) {
        THREE_BUTTONS(false, 0),
        TWO_BUTTONS(true, 1),
        NO_BUTTON(true, 2);
    }

    var mode: Mode? = null

    private val mChangeListeners: MutableList<NavigationModeChangeListener> = ArrayList()

    init {
        initializeMode()
        mContext.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val oldMode: Mode? = mode
                initializeMode()
                if (mode != null && mode != oldMode) {
                    dispatchModeChange()
                }
            }
        }, getIntentFilter())
    }

    private fun initializeMode() {
        val modeInt = getNavBarInteractionMode(mContext)
        for (m in Mode.values()) {
            if (m.resValue == modeInt) {
                mode = m
            }
        }
    }

    private fun dispatchModeChange() {
        for (listener in mChangeListeners) {
            listener.onNavigationModeChanged(mode)
        }
    }

    fun addModeChangeListener(listener: NavigationModeChangeListener): Mode? {
        mChangeListeners.add(listener)
        return mode
    }

    fun removeModeChangeListener(listener: NavigationModeChangeListener) {
        mChangeListeners.remove(listener)
    }

    interface NavigationModeChangeListener {
        fun onNavigationModeChanged(newMode: Mode?)
    }

    companion object {
        fun getMode(context: Context): Mode? {
            return INSTANCE[context]?.mode
        }

        private val INSTANCE = MainThreadInitializedObject(object : ObjectProvider<SystemNavigationMode> {
            override fun get(context: Context): SystemNavigationMode {
                return SystemNavigationMode(context.applicationContext)
            }
        })

        private fun getNavBarInteractionMode(context: Context): Int {
            val res = context.resources
            val resId = res.getIdentifier("config_navBarInteractionMode", "integer", "android")
            return if (resId != 0) {
                res.getInteger(resId)
            } else {
                Log.e(TAG, "Failed to get system resource ID. Incompatible framework version?")
                -1
            }
        }

        /**
         * Creates an intent filter to listen for actions with a specific package in the data field.
         */
        private fun getIntentFilter(): IntentFilter {
            val packageFilter = IntentFilter("android.intent.action.OVERLAY_CHANGED")
            packageFilter.addDataScheme("package")
            packageFilter.addDataSchemeSpecificPart("android", PatternMatcher.PATTERN_LITERAL)
            return packageFilter
        }
    }


}
