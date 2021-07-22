package com.domain.scaffold.util

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

class ClickSpan(
    @ColorInt
    private val color: Int,
    private val underline: Boolean,
    private val onClick: () -> Unit
) : ClickableSpan() {

    override fun onClick(widget: View) {
        onClick()
    }

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = underline
        ds.linkColor = Color.TRANSPARENT
    }
}