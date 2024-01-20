package com.irun.runker.util

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.widget.TextView
import androidx.annotation.IntDef

class Timer {
    companion object {
        const val FORWARD = 0
        const val REVERSE = 1
    }

    private var mTime: Int = 0

    // 当前的计时
    private var mMode = REVERSE
    private var mTextWhenFinished: CharSequence? = null
    private var mTextViewGoneWhenFinished: Boolean = true

    @IntDef(FORWARD, REVERSE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    private var mTextView: TextView? = null

    constructor(time: Int) {
        mTime = time
    }

    /**
     *  @param mode  [FORWARD] 正数计时, [REVERSE] 倒数计时
     */
    fun setMode(@Mode mode: Int): Timer {
        mMode = mode
        return this
    }

    /**
     * @param time 单位: 秒
     */
    fun setTime(time: Int): Timer {
        mTime = time
        return this
    }

    /**
     * @param textView 要被倒计时的控件
     */
    fun setTextView(textView: TextView): Timer {
        mTextView = textView
        return this
    }

    fun setTextWhenFinished(text: CharSequence?): Timer {
        mTextWhenFinished = text
        return this
    }

    fun setTextViewGoneWhenFinished(goneWhenFinished: Boolean): Timer {
        mTextViewGoneWhenFinished = goneWhenFinished
        return this
    }

    fun start(callback: Callback? = null) {
        if (mTime <= 0) {
            callback?.onEnd()
            return
        }
        var seconds = if (mMode == REVERSE) mTime else 0
        val animator = ObjectAnimator.ofFloat(1f, 0f)
        animator.interpolator = AnticipateOvershootInterpolator()
        animator.duration = 1000 * mTime.toLong()
        animator.repeatCount = mTime
        animator.repeatMode = ValueAnimator.RESTART
        animator.addUpdateListener { v ->
            mTextView?.let {
                val value = v.animatedValue as Float
                it.alpha = value
                it.scaleX = value
                it.scaleY = value
            }
        }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                callback?.onStart()
            }

            override fun onAnimationEnd(animation: Animator) {
                if (mTextViewGoneWhenFinished) {
                    mTextView?.visibility = View.GONE
                }
                callback?.onEnd()
            }

            override fun onAnimationCancel(animation: Animator) {
                if (mTextViewGoneWhenFinished) {
                    mTextView?.visibility = View.GONE
                }
                callback?.onEnd()
            }

            override fun onAnimationRepeat(animation: Animator) {
                if (mMode == REVERSE) {
                    seconds--
                } else {
                    seconds++
                }
                if (seconds == 0 && mTextWhenFinished != null) {
                    mTextView?.text = mTextWhenFinished
                } else {
                    mTextView?.text = seconds.toString()
                }
                callback?.onCount(seconds)
            }
        })
        mTextView?.let {
            it.text = seconds.toString()
            it.visibility = View.VISIBLE
        }
        animator.start()
    }

    interface Callback {
        fun onStart() {}
        fun onCount(count: Int) {}
        fun onEnd() {}
    }
}