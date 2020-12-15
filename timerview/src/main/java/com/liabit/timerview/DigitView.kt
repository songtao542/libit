package com.liabit.timerview

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import android.widget.TextView

internal class DigitView : RelativeLayout {
    private var animationDuration = 600L

    private var frontTopText: TextView
    private var frontBottomText: TextView
    private var backTopText: TextView
    private var backBottomText: TextView

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        inflate(context, R.layout.digit_view, this)

        frontTopText = findViewById(R.id.frontTopText)
        frontBottomText = findViewById(R.id.frontBottomText)
        backTopText = findViewById(R.id.backTopText)
        backBottomText = findViewById(R.id.backBottomText)
    }

    fun setText(text: String) {
        frontTopText.clearAnimation()
        frontBottomText.clearAnimation()
        backTopText.clearAnimation()
        backBottomText.clearAnimation()

        frontTopText.text = text
        frontBottomText.text = text
        backTopText.text = text
        backBottomText.text = text
    }

    fun setTextSize(unit: Int, size: Float) {
        frontTopText.setTextSize(unit, size)
        backTopText.setTextSize(unit, size)
        frontBottomText.setTextSize(unit, size)
        backBottomText.setTextSize(unit, size)
    }

    fun setTextColor(colors: ColorStateList) {
        frontTopText.setTextColor(colors)
        backTopText.setTextColor(colors)
        frontBottomText.setTextColor(colors)
        backBottomText.setTextColor(colors)
    }

    override fun setBackgroundColor(color: Int) {
        super.setBackgroundColor(color)
        frontTopText.setBackgroundColor(color)
        backTopText.setBackgroundColor(color)
        frontBottomText.setBackgroundColor(color)
        backBottomText.setBackgroundColor(color)
    }

    fun animateTextChange(text: String) {
        if (backTopText.text == text) {
            return
        }

        if (height == 0) {
            backTopText.text = text
            frontBottomText.text = text
            frontTopText.text = text
            backBottomText.text = text
            return
        }

        frontTopText.clearAnimation()
        frontBottomText.clearAnimation()

        backTopText.text = text

        frontTopText.pivotX = (frontTopText.right - frontTopText.left) / 2f
        frontTopText.pivotY = (frontTopText.bottom - frontTopText.top) / 2f

        frontBottomText.pivotX = (frontBottomText.right - frontBottomText.left) / 2f
        frontBottomText.pivotY = (frontBottomText.bottom - frontBottomText.top) / 2f

        frontTopText.rotationX = 0f
        frontTopText.animate()
                .setDuration(animationDuration / 2)
                .rotationX(-90f)
                .setInterpolator(AccelerateInterpolator())
                .withEndAction {
                    frontBottomText.rotationX = 90f
                    frontBottomText.text = backTopText.text

                    frontTopText.text = backTopText.text
                    frontTopText.rotationX = 0f

                    frontBottomText.animate()
                            .setDuration(animationDuration / 2)
                            .rotationX(0f)
                            .setInterpolator(DecelerateInterpolator())
                            .withEndAction {
                                backBottomText.text = frontBottomText.text
                            }
                            .start()
                }
                .start()
    }

    fun setAnimationDuration(duration: Long) {
        this.animationDuration = duration
    }

    fun setTypeFace(typeFace: Typeface) {
        frontTopText.typeface = typeFace
        frontBottomText.typeface = typeFace
        backTopText.typeface = typeFace
        backBottomText.typeface = typeFace
    }
}
