package com.irun.runker.extension

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
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()
}

fun Float.dip(resources: Resources): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics).toInt()
}

fun Int.dp(context: Context): Float {
    return dp(context.resources)
}

fun Float.dp(context: Context): Float {
    return dp(context.resources)
}

fun Int.dp(resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)
}

fun Float.dp(resources: Resources): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)
}


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