package com.liabit.tablayout

import android.view.View
import androidx.annotation.IntDef

interface TabIndicator {
    @IntDef(value = [INTERPOLATOR_START, INTERPOLATOR_END])
    @Retention(AnnotationRetention.SOURCE)
    annotation class InterpolatorType

    @IntDef(value = [MATCH_TAB_WIDTH, MATCH_TAB_CONTENT])
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    companion object {
        const val MATCH_TAB_WIDTH = 0
        const val MATCH_TAB_CONTENT = 1
        const val INTERPOLATOR_START = 0
        const val INTERPOLATOR_END = 1
    }

    fun onPageScrolled(tabLayout: TabLayout, position: Int, positionOffset: Float)

    fun onPageSelected(tabLayout: TabLayout, position: Int)

    fun onTabCreated(tabLayout: TabLayout, position: Int) {
        // DO NOTHING
    }

    /**
     * @return is in front of tab container
     */
    fun isFront(): Boolean {
        return true
    }

    fun getTabLayout(): TabLayout? {
        if (this is View) {
            var parent = parent
            while (parent != null) {
                if (parent is TabLayout) {
                    return parent
                }
                parent = parent.parent
            }
        }
        return null
    }

}