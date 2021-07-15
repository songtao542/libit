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

/* Outline.setPath() 无法clip
fun View.setCornerRadius(@DimenRes topDimenResId: Int, @DimenRes bottomDimenResId: Int) {
    val t = resources.getDimension(topDimenResId)
    val b = resources.getDimension(bottomDimenResId)
    setCornerRadius(t, t, b, b)
}

fun View.setCornerRadius(
    @DimenRes topLeftDimenResId: Int,
    @DimenRes topRightDimenResId: Int,
    @DimenRes bottomLeftDimenResId: Int,
    @DimenRes bottomRightDimenResId: Int
) {
    val tl = resources.getDimension(topLeftDimenResId)
    val tr = resources.getDimension(topRightDimenResId)
    val bl = resources.getDimension(bottomLeftDimenResId)
    val br = resources.getDimension(bottomRightDimenResId)
    setCornerRadius(tl, tr, bl, br)
}

fun View.setCornerRadius(top: Float, bottom: Float) {
    setCornerRadius(top, top, bottom, bottom)
}

fun View.setCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {

        val mRect = RectF()
        val mPath = Path()
        val mRadiusArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        override fun getOutline(view: View?, outline: Outline?) {
            val v = view ?: return
            mRect.set(0f, 0f, v.width.toFloat(), v.height.toFloat())
            mPath.reset()
            mRadiusArray[0] = topLeft
            mRadiusArray[1] = topLeft
            mRadiusArray[2] = topRight
            mRadiusArray[3] = topRight
            mRadiusArray[4] = bottomLeft
            mRadiusArray[5] = bottomLeft
            mRadiusArray[6] = bottomRight
            mRadiusArray[7] = bottomRight
            mPath.addRoundRect(mRect, mRadiusArray, Path.Direction.CW)
            outline?.setPath(mPath)

        }
    }
}*/

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