@file:Suppress("unused")

package com.liabit.extension

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.BaselineLayout

fun MenuItem.onNavDestinationSelected(navController: NavController): Boolean {
    val builder = NavOptions.Builder().setLaunchSingleTop(true)
    builder.setEnterAnim(R.animator.nav_default_enter_anim)
            .setExitAnim(R.animator.nav_default_exit_anim)
            .setPopEnterAnim(R.animator.nav_default_pop_enter_anim)
            .setPopExitAnim(R.animator.nav_default_pop_exit_anim)
    builder.setPopUpTo(navController.graph.id, false)
    val options = builder.build()
    return try {
        navController.navigate(itemId, null, options)
        true
    } catch (e: Exception) {
        false
    }
}

@Suppress("UNUSED_PARAMETER")
fun BottomNavigationView.onDestinationChanged(@NonNull controller: NavController,
                                              @NonNull destination: NavDestination,
                                              @Nullable arguments: Bundle?) = try {
    val menu = menu
    var h = 0
    val size = menu.size()
    while (h < size) {
        val item = menu.getItem(h)
        val destId = item.itemId
        var currentDestination: NavDestination? = destination
        while (currentDestination?.id != destId && currentDestination?.parent != null) {
            currentDestination = currentDestination.parent
        }
        if (currentDestination?.id == destId) {
            item.isChecked = true
        }
        h++
    }
} catch (e: Throwable) {
    Log.e("BottomNavigationViewExt", "error: ", e)
}

@SuppressLint("RestrictedApi")
fun BottomNavigationView.hideLabel(index: Int) = try {
    val menuContainer = getChildAt(0) as ViewGroup
    // 禁用多点触摸
    menuContainer.isMotionEventSplittingEnabled = false
    for (i in 0 until menuContainer.childCount) {
        if (i == index) {
            val menuItem = menuContainer.getChildAt(i)
            if (menuItem is BottomNavigationItemView) {
                for (j in 0 until menuItem.childCount) {
                    val child = menuItem.getChildAt(j)
                    if (child is BaselineLayout) {
                        child.visibility = View.GONE
                    } else if (child is ImageView) {
                        child.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                child.viewTreeObserver.removeOnGlobalLayoutListener(this)
                                (child.parent as? ViewGroup)?.height?.let {
                                    val lp = child.layoutParams
                                    var topMargin = 0f
                                    menuItem.clipChildren = false
                                    menuItem.clipToPadding = false
                                    if (lp is ViewGroup.MarginLayoutParams) {
                                        topMargin = lp.topMargin.toFloat()
                                    }
                                    lp.width = it
                                    lp.height = it
                                    child.layoutParams = lp
                                    child.translationY = -topMargin
                                    child.scaleType = ImageView.ScaleType.CENTER_INSIDE
                                    child.requestLayout()
                                }
                            }
                        })
                    }
                }
            }
        }
    }
} catch (e: Throwable) {
    Log.e("BottomNavigationViewExt", "error: ", e)
}