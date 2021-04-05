package com.irun.runker.extension

import android.annotation.SuppressLint
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

@SuppressLint("RestrictedApi")
fun BottomNavigationView.disableShiftMode() = try {
    for (menu in this.asSequence()) {
        if (menu is BottomNavigationMenuView) {
            for (item in menu.asSequence()) {
                if (item is BottomNavigationItemView) {
                    item.setShifting(false)
                    item.setChecked(item.itemData.isChecked)
                }
            }
        }
    }
} catch (e: Throwable) {
    Log.e("BottomNavigationViewExt", "error: ", e)
}

@SuppressLint("RestrictedApi")
fun BottomNavigationView.hideIcon() = try {
    for (menu in this.asSequence()) {
        if (menu is BottomNavigationMenuView) {
            for (item in menu.asSequence()) {
                if (item is BottomNavigationItemView) {
                    item.findViewById<View>(com.google.android.material.R.id.icon).visibility = View.GONE
                    val label = item.getChildAt(1)
                    val lp = label.layoutParams as FrameLayout.LayoutParams
                    lp.gravity = Gravity.CENTER
                    label.layoutParams = lp
                    label.setPadding(0, 0, 0, 0)
                    label.findViewById<TextView>(com.google.android.material.R.id.smallLabel).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    label.findViewById<TextView>(com.google.android.material.R.id.largeLabel).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                }
            }
        }
    }
} catch (e: Throwable) {
    Log.e("BottomNavigationViewExt", "error: ", e)
}

@SuppressLint("RestrictedApi")
fun BottomNavigationView.hideLabel() = try {
    for (menu in this.asSequence()) {
        if (menu is BottomNavigationMenuView) {
            for (item in menu.asSequence()) {
                if (item is BottomNavigationItemView) {
                    val label = item.getChildAt(1)
                    label.findViewById<TextView>(com.google.android.material.R.id.smallLabel).visibility = View.GONE
                    label.findViewById<TextView>(com.google.android.material.R.id.largeLabel).visibility = View.GONE
                }
            }
        }
    }
} catch (e: Throwable) {
    Log.e("BottomNavigationViewExt", "error: ", e)
}