package com.liabit.extension

import android.graphics.Rect
import android.graphics.RectF

fun Rect.rectF(): RectF {
    return RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

fun RectF.rect(): Rect {
    return Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
}
