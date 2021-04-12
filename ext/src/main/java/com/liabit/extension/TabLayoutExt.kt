package com.liabit.extension

import android.graphics.drawable.RippleDrawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.google.android.material.tabs.TabLayout

fun TabLayout.setupStyle() {
    (getChildAt(0) as? ViewGroup)?.forEach {
        try {
            (((it as? TabLayout.TabView)?.background) as? RippleDrawable)?.setDrawableByLayerId(
                    android.R.id.mask,
                    ContextCompat.getDrawable(context, R.drawable.tab_ripple_mask)
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
