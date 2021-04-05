package com.liabit.numberpicker

import android.content.Context
import android.os.Build
import android.util.TypedValue

fun <T> Array<T>.toStringArray(): Array<String> {
    return Array(this.size) {
        "${get(it)}"
    }
}

fun Context.colorOf(id: Int): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        getColor(id)
    } else {
        @Suppress("DEPRECATION")
        resources.getColor(id)
    }
}

fun Context.dip(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}
