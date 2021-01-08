package com.liabit.integratepicker

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.widget.NumberPicker

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

/**
 * 设置picker分割线的颜色
 */
fun NumberPicker.setDividerColor(color: Int) {
    try {
        NumberPicker::class.java.getDeclaredField("mSelectionDivider").let {
            it.isAccessible = true
            it.set(this, ColorDrawable(color))
        }
    } catch (e: Throwable) {
        Log.d("NumberPickerExt", "NumberPicker.setDividerColor error: ", e)
    }
}

/**
 * 设置picker分割线的宽度
 */
fun NumberPicker.setDividerHeight(height: Float) {
    try {
        val fields = NumberPicker::class.java.declaredFields
        for (field in fields) {
            if (field.name == "mSelectionDividerHeight") {
                field.isAccessible = true
                field.set(this, context.dip(height))
                break
            }
        }
    } catch (e: Throwable) {
        Log.d("NumberPickerExt", "NumberPicker.setDividerHeight error: ", e)
    }
}