package com.liabit.extension

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.internal.BaselineLayout


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

fun BottomNavigationView.setupWithNavController(controller: NavController, menuReTapListener: ((item: MenuItem) -> Unit)? = null) {
    setOnNavigationItemSelectedListener {
        if (it.isChecked) {
            menuReTapListener?.invoke(it)
            return@setOnNavigationItemSelectedListener true
        }
        return@setOnNavigationItemSelectedListener it.onNavDestinationSelected(controller)
    }
    controller.addOnDestinationChangedListener(::onDestinationChanged)
}

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

fun BottomNavigationView.onDestinationChanged(
    controller: NavController,
    destination: NavDestination,
    arguments: Bundle?
) = try {
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

private const val SUPPORT_FIXED_START_DESTINATION = false

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 *
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
fun BottomNavigationView.setupWithNavController(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
): LiveData<NavController> {
    // Result. Mutable live data with the selected controlled
    val selectedNavController = MutableLiveData<NavController>()

    var firstFragmentGraphId = 0

    // Map of tags
    val graphIdToTagMap = SparseArray<String>()

    // First create a NavHostFragment for each NavGraph ID
    for (index in navGraphIds.indices) {
        val navGraphId = navGraphIds[index]
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)

        // Obtain its id
        val graphId = navHostFragment.navController.graph.id

        if (index == 0) {
            firstFragmentGraphId = graphId
        }

        // Save to the map
        graphIdToTagMap[graphId] = fragmentTag

        // Attach or detach nav host fragment depending on whether it's the selected item.
        if (this.selectedItemId == graphId) {
            // Update livedata with the selected graph
            selectedNavController.value = navHostFragment.navController
            attachNavHostFragment(fragmentManager, navHostFragment, index == 0)
        } else {
            detachNavHostFragment(fragmentManager, navHostFragment)
        }
    }

    // Now connect selecting an item with swapping Fragments
    var selectedItemTag = graphIdToTagMap[this.selectedItemId]
    val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
    var isOnFirstFragment = selectedItemTag == firstFragmentTag

    // When a navigation item is selected
    setOnNavigationItemSelectedListener { item ->
        // Don't do anything if the state is state has already been saved.
        if (fragmentManager.isStateSaved) {
            return@setOnNavigationItemSelectedListener false
        } else {
            val newSelectedTag = graphIdToTagMap[item.itemId]
            if (selectedItemTag != newSelectedTag) {
                if (SUPPORT_FIXED_START_DESTINATION) {
                    // Pop everything above the first fragment (the "fixed start destination")
                    fragmentManager.popBackStack(firstFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                val selectedFragment = fragmentManager.findFragmentByTag(newSelectedTag) as NavHostFragment

                // Exclude the first fragment tag because it's always in the back stack.
                if (firstFragmentTag != newSelectedTag) {
                    // Commit a transaction that cleans the back stack and adds the first fragment
                    // to it, creating the fixed started destination.
                    fragmentManager.beginTransaction()
                        .setCustomAnimations(
                            R.anim.nav_default_enter_anim,
                            R.anim.nav_default_exit_anim,
                            R.anim.nav_default_pop_enter_anim,
                            R.anim.nav_default_pop_exit_anim
                        )
                        .attach(selectedFragment)
                        .setPrimaryNavigationFragment(selectedFragment)
                        .apply {
                            // Detach all other Fragments
                            graphIdToTagMap.forEach { _, fragmentTag ->
                                if (fragmentTag != newSelectedTag) {
                                    val tag = if (SUPPORT_FIXED_START_DESTINATION) firstFragmentTag else fragmentTag
                                    fragmentManager.findFragmentByTag(tag)?.let {
                                        detach(it)
                                    }
                                }
                            }
                        }
                        .addToBackStack(firstFragmentTag)
                        .setReorderingAllowed(true)
                        .commit()
                }
                selectedItemTag = newSelectedTag
                isOnFirstFragment = selectedItemTag == firstFragmentTag
                selectedNavController.value = selectedFragment.navController
                return@setOnNavigationItemSelectedListener true
            } else {
                return@setOnNavigationItemSelectedListener false
            }
        }
    }

    // Optional: on item reselected, pop back stack to the destination of the graph
    setupItemReselected(graphIdToTagMap, fragmentManager)

    // Handle deep link
    setupDeepLinks(navGraphIds, fragmentManager, containerId, intent)

    // Finally, ensure that we update our BottomNavigationView when the back stack changes
    fragmentManager.addOnBackStackChangedListener {
        if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
            this.selectedItemId = firstFragmentGraphId
        }

        // Reset the graph if the currentDestination is not valid (happens when the back
        // stack is popped after using the back button).
        selectedNavController.value?.let { controller ->
            if (controller.currentDestination == null) {
                controller.navigate(controller.graph.id)
            }
        }
    }
    return selectedNavController
}

private fun BottomNavigationView.setupDeepLinks(
    navGraphIds: List<Int>,
    fragmentManager: FragmentManager,
    containerId: Int,
    intent: Intent
) {
    navGraphIds.forEachIndexed { index, navGraphId ->
        val fragmentTag = getFragmentTag(index)

        // Find or create the Navigation host fragment
        val navHostFragment = obtainNavHostFragment(fragmentManager, fragmentTag, navGraphId, containerId)

        // Handle Intent
        if (navHostFragment.navController.handleDeepLink(intent)
            && selectedItemId != navHostFragment.navController.graph.id
        ) {
            this.selectedItemId = navHostFragment.navController.graph.id
        }
    }
}

private fun BottomNavigationView.setupItemReselected(
    graphIdToTagMap: SparseArray<String>,
    fragmentManager: FragmentManager
) {
    setOnNavigationItemReselectedListener { item ->
        val newlySelectedItemTag = graphIdToTagMap[item.itemId]
        val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment
        val navController = selectedFragment.navController
        // Pop the back stack to the start destination of the current navController graph
        navController.popBackStack(navController.graph.startDestination, false)
    }
}

private fun detachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment
) {
    fragmentManager.beginTransaction()
        .detach(navHostFragment)
        .commitNow()
}

private fun attachNavHostFragment(
    fragmentManager: FragmentManager,
    navHostFragment: NavHostFragment,
    isPrimaryNavFragment: Boolean
) {
    fragmentManager.beginTransaction()
        .attach(navHostFragment)
        .apply {
            if (isPrimaryNavFragment) {
                setPrimaryNavigationFragment(navHostFragment)
            }
        }
        .commitNow()

}

private fun obtainNavHostFragment(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    navGraphId: Int,
    containerId: Int
): NavHostFragment {
    // If the Nav Host fragment exists, return it
    val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as? NavHostFragment
    existingFragment?.let { return it }

    // Otherwise, create it and return it.
    val navHostFragment = NavHostFragment.create(navGraphId)
    fragmentManager.beginTransaction()
        .add(containerId, navHostFragment, fragmentTag)
        .commitNow()
    return navHostFragment
}

private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
    val backStackCount = backStackEntryCount
    for (index in 0 until backStackCount) {
        if (getBackStackEntryAt(index).name == backStackName) {
            return true
        }
    }
    return false
}

private fun getFragmentTag(index: Int) = "bottomNavigation#$index"
