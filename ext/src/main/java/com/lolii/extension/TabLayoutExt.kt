package com.lolii.extension

import android.graphics.drawable.RippleDrawable
import android.view.ViewGroup
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout
import com.lolii.ext.R

fun TabLayout.setupStyle() {
    (getChildAt(0) as? ViewGroup)?.forEach {
        try {
            (((it as? TabLayout.TabView)?.background)
                    as? RippleDrawable)?.setDrawableByLayerId(
                    android.R.id.mask,
                    context.getDrawable(R.drawable.tab_ripple_mask)
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}