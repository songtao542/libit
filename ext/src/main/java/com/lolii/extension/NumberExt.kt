package com.lolii.extension

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

fun Int.dip(context: Context): Int {
    return dip(context.resources)
}

fun Float.dip(context: Context): Int {
    return dip(context.resources)
}

fun Int.dip(resources: Resources): Int {
    return convert2dp(resources).toInt()
}

fun Float.dip(resources: Resources): Int {
    return convert2dp(resources).toInt()
}

fun Int.dp(context: Context): Float {
    return dp(context.resources)
}

fun Float.dp(context: Context): Float {
    return dp(context.resources)
}

fun Int.dp(resources: Resources): Float {
    return convert2px(resources)
}

fun Float.dp(resources: Resources): Float {
    return convert2px(resources)
}

fun Int.convert2px(resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)
}

fun Float.convert2px(resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)
}

/**
 * Convert px to dp
 * @return The value converted to dp
 */
fun Int.convert2dp(context: Context): Float {
    return convert2dp(context.resources)
}

/**
 * Convert px to dp
 * @return The value converted to dp
 */
fun Float.convert2dp(context: Context): Float {
    return convert2dp(context.resources)
}

/**
 * Convert px to dp
 * @return The value converted to dp
 */
fun Int.convert2dp(resources: Resources): Float {
    val scale = resources.displayMetrics.density
    return this / scale + 0.5f
}

/**
 * Convert px to dp
 * @return The value converted to dp
 */
fun Float.convert2dp(resources: Resources): Float {
    val scale = resources.displayMetrics.density
    return this / scale + 0.5f
}

/**
 * @return max value
 */
fun max(vararg args: Int): Int {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.max(m, args[i])
    }
    return m
}

fun min(vararg args: Int): Int {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.min(m, args[i])
    }
    return m
}

fun max(vararg args: Float): Float {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.max(m, args[i])
    }
    return m
}

fun min(vararg args: Float): Float {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.min(m, args[i])
    }
    return m
}

fun max(vararg args: Double): Double {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.max(m, args[i])
    }
    return m
}

fun min(vararg args: Double): Double {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.min(m, args[i])
    }
    return m
}

fun max(vararg args: Long): Long {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.max(m, args[i])
    }
    return m
}

fun min(vararg args: Long): Long {
    if (args.size < 2) {
        return args[0]
    }
    var m = args[0]
    for (i in 1 until args.size) {
        m = kotlin.math.min(m, args[i])
    }
    return m
}