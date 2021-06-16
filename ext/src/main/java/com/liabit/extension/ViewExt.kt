package com.liabit.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.graphics.Outline
import android.util.TypedValue
import android.view.View
import android.view.View.ALPHA
import android.view.ViewOutlineProvider
import androidx.annotation.DimenRes

fun View.alphaOut(duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: ((view: View) -> Unit)? = null) {
    alpha(1f, 0f, duration, update, end)
}

fun View.alphaIn(duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: ((view: View) -> Unit)? = null) {
    alpha(0f, 1f, duration, update, end)
}

fun View.alpha(from: Float, to: Float, duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: ((view: View) -> Unit)? = null) {
    visibility = View.VISIBLE
    val view = this
    val animator = ObjectAnimator.ofFloat(this, ALPHA, from, to)
    animator.duration = duration
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            end?.invoke(view)
        }
    })
    animator.addUpdateListener {
        update?.invoke(it.animatedValue as Float)
    }
    animator.start()
}

fun View.setCornerRadius(radius: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline?) {
            val v = view ?: return
            outline?.setRoundRect(0, 0, v.width, v.height, radius)
        }
    }
}

fun View.setCornerRadius(@DimenRes dimenResId: Int) {
    setCornerRadius(resources.getDimension(dimenResId))
}

fun View.setSelectableItemBackground() {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
    val attribute = intArrayOf(android.R.attr.selectableItemBackground)
    val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
    background = typedArray.getDrawable(0)
}

fun View.setSelectableItemBackgroundBorderless() {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
    val attribute = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
    val typedArray = context.theme.obtainStyledAttributes(typedValue.resourceId, attribute)
    background = typedArray.getDrawable(0)
}