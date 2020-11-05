package com.liabit.extension

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.View.ALPHA

fun View.alphaOut(duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: (() -> Unit)? = null) {
    alpha(1f, 0f, duration, update, end)
}

fun View.alphaIn(duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: (() -> Unit)? = null) {
    alpha(0f, 1f, duration, update, end)
}

fun View.alpha(from: Float, to: Float, duration: Long = 250, update: ((alpha: Float) -> Unit)? = null, end: (() -> Unit)? = null) {
    visibility = View.VISIBLE
    val animator = ObjectAnimator.ofFloat(this, ALPHA, from, to)
    animator.duration = duration
    animator.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator?) {
            end?.invoke()
        }
    })
    animator.addUpdateListener {
        update?.invoke(it.animatedValue as Float)
    }
    animator.start()
}