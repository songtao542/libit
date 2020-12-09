package com.liabit.statebutton

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt

/**
 * Author:         songtao
 * CreateDate:     2020/12/8 10:00
 */
class SpringButton : FrameLayout {

    companion object {
        private const val ANIMATION_DURATION = 300L
        private const val LONG_ANIMATION_DURATION = 3000L
        private const val MIN_SCALE = 1f
        private const val MAX_SCALE = 1.2f

        // 空闲状态
        private const val STATE_IDLE = 0x00000000

        // 展开中
        private const val STATE_EXPANDING = 0x00000001

        // 展开结束
        private const val STATE_EXPANDED = 0x00000010

        // 关闭中
        private const val STATE_CLOSING = 0x00000100

        // 关闭结束
        private const val STATE_CLOSED = 0x00001000

        // 长按中
        private const val STATE_LONG_PRESSING = 0x00010000

        // 长按中途终止
        private const val STATE_LONG_PRESS_ABORTING = 0x00100000

        // 长按结束
        private const val STATE_LONG_PRESS_END = 0x01000000

        // 展开结束触发收缩
        private const val CLOSE_REASON_EXPANDING_END = 0x00000000

        // 触摸事件 UP 触发收缩
        private const val CLOSE_REASON_TOUCH_UP = 0x00000000

        // 长按结束触发收缩
        private const val CLOSE_REASON_LONG_PRESSING_END = 0x00000001

        // 长按中止结束触发结束
        private const val CLOSE_REASON_LONG_PRESS_ABORT_END = 0x00000010
    }

    private lateinit var mIconView: ImageView
    private lateinit var mTextView: TextView
    private var mOnPressListener: OnPressListener? = null
    private var mOnLongPressListener: OnLongPressListener? = null

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        LayoutInflater.from(context).inflate(R.layout.spring_button, this, true)
        val padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, resources.displayMetrics).toInt()
        setPadding(padding, padding, padding, padding)
        clipChildren = false
        clipToPadding = false
        mIconView = findViewById(R.id.iconView)
        mTextView = findViewById(R.id.textView)

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SpringButton, defStyleAttr, defStyleRes)
            val icon = typedArray.getResourceId(R.styleable.SpringButton_icon, 0)
            if (icon != 0) {
                mIconView.setImageResource(icon)
            }
            var iconWidth = typedArray.getDimension(R.styleable.SpringButton_iconWidth, 0f)
            var iconHeight = typedArray.getDimension(R.styleable.SpringButton_iconHeight, 0f)
            val iconSize = typedArray.getDimension(R.styleable.SpringButton_iconSize, 0f)
            val textSize = typedArray.getDimensionPixelSize(R.styleable.SpringButton_android_textSize, 0)
            if (textSize != 0) {
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
            }
            val text = typedArray.getText(R.styleable.SpringButton_android_text)
            mTextView.visibility = if (text == null) {
                View.GONE
            } else {
                mTextView.text = text
                View.VISIBLE
            }
            val textColor = typedArray.getColorStateList(R.styleable.SpringButton_android_textColor)
            if (textColor != null) {
                mTextView.setTextColor(textColor)
            }
            if (iconSize > 0) {
                iconWidth = iconSize
                iconHeight = iconSize
            }
            if (iconWidth != 0f || iconHeight != 0f) {
                val layoutParams = mIconView.layoutParams ?: ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                if (iconWidth > 0) {
                    layoutParams.width = iconWidth.toInt()
                }
                if (iconHeight > 0) {
                    layoutParams.height = iconHeight.toInt()
                }
                mIconView.layoutParams = layoutParams
            }
            typedArray.recycle()
        }
        super.setOnClickListener {}
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener {}
        mOnPressListener = if (l != null) WrappedClickListener(l) else null
    }

    fun setOnPressListener(l: OnPressListener?) {
        super.setOnClickListener {}
        mOnPressListener = l
    }

    fun setOnPressListener(l: ((v: View) -> Unit)?) {
        super.setOnClickListener {}
        mOnPressListener = if (l == null) null else object : OnPressListener {
            override fun onClick(view: View, isLongPressStart: Boolean) {
                l.invoke(view)
            }
        }
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener { false }
        mOnLongPressListener = if (l != null) WrappedLongClickListener(l) else null
    }

    fun setOnLongPressListener(l: OnLongPressListener?) {
        mOnLongPressListener = l
    }

    fun hide() {
        ValueAnimator.ofFloat(scaleX, 0f).apply {
            interpolator = LinearInterpolator()
            duration = ANIMATION_DURATION
            addUpdateListener {
                val value = it.animatedValue as Float
                scaleX = value
                scaleY = value
                alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    visibility = View.GONE
                }
            })
        }.start()
    }

    fun show() {
        ValueAnimator.ofFloat(scaleX, MIN_SCALE).apply {
            interpolator = LinearInterpolator()
            duration = ANIMATION_DURATION
            addUpdateListener {
                val value = it.animatedValue as Float
                scaleX = value
                scaleY = value
                alpha = value
            }
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    visibility = View.VISIBLE
                }
            })
        }.start()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val ev = event ?: return super.onTouchEvent(event)
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (mState == STATE_IDLE || mState == STATE_CLOSED || mState == STATE_LONG_PRESS_END) {
                    expand()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when (mState) {
                    // 正在长按中
                    STATE_LONG_PRESSING -> {
                        abortLongPress()
                    }
                    // 展开已结束
                    STATE_EXPANDED -> {
                        close(CLOSE_REASON_TOUCH_UP)
                    }
                    // 正在展开中
                    STATE_EXPANDING -> {
                        mCloseAfterExpandEnd = true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    // 展开动画完成后是否收缩
    private var mCloseAfterExpandEnd = false

    private var mState = STATE_IDLE
    private var mCloseReason = CLOSE_REASON_EXPANDING_END

    private fun expand() {
        if (scaleX == MIN_SCALE) {
            mState = STATE_EXPANDING
            mEAnimator.start()
        }
    }

    private fun close(closeReason: Int) {
        if (scaleX == MAX_SCALE) {
            mCloseReason = closeReason
            mState = STATE_CLOSING
            mCAnimator.start()
        }
    }

    private fun startLongPress() {
        mState = STATE_LONG_PRESSING
        mLAnimator.duration = LONG_ANIMATION_DURATION
        mPaint.alpha = 255
        mLAnimator.setFloatValues(0f, 360f)
        mLAnimator.start()
    }

    private fun abortLongPress() {
        mState = STATE_LONG_PRESS_ABORTING
        mLAnimator.duration = ANIMATION_DURATION
        mLAnimator.setFloatValues(mLongProgress, 0f)
        mLAnimator.start()
    }

    private var mLongProgress = 0f

    private val mLAnimator = ValueAnimator.ofFloat(0f, 360f).apply {
        interpolator = LinearInterpolator()
        duration = LONG_ANIMATION_DURATION
        addUpdateListener {
            mLongProgress = it.animatedValue as Float
            invalidate()
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                if (mState == STATE_LONG_PRESSING) {
                    mOnPressListener?.onClick(this@SpringButton, true)
                }
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                val isLongPressEnd = mState == STATE_LONG_PRESSING
                // 长按动画结束或者长按中止动画结束后,恢复按钮大小
                close(if (mState == STATE_LONG_PRESSING) CLOSE_REASON_LONG_PRESSING_END else CLOSE_REASON_LONG_PRESS_ABORT_END)
                if (isLongPressEnd) {
                    // 如果长按没有中止, 则调用长按监听器
                    mOnLongPressListener?.onLongClick(this@SpringButton)
                } else {
                    mOnLongPressListener?.onLongClickAbort(this@SpringButton)
                }
                // 更新状态为: 长按结束
                mState = STATE_LONG_PRESS_END
            }
        })
    }

    // 展开动画
    private val mEAnimator = ValueAnimator.ofFloat(MIN_SCALE, MAX_SCALE).apply {
        interpolator = LinearInterpolator()
        duration = ANIMATION_DURATION
        addUpdateListener {
            val value = it.animatedValue as Float
            scaleX = value
            scaleY = value
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                if (mOnLongPressListener != null && isPressed) {
                    startLongPress()
                } else if (mState == STATE_EXPANDING) {
                    if (mCloseAfterExpandEnd) {
                        close(CLOSE_REASON_EXPANDING_END)
                    } else {
                        // 更新状态为: 已展开
                        mState = STATE_EXPANDED
                    }
                }
            }
        })
    }

    private val mCAnimator = ValueAnimator.ofFloat(MAX_SCALE, MIN_SCALE).apply {
        interpolator = LinearInterpolator()
        duration = ANIMATION_DURATION
        addUpdateListener {
            val value = it.animatedValue as Float
            scaleX = value
            scaleY = value
            if (mCloseReason == CLOSE_REASON_LONG_PRESSING_END) {
                mPaint.alpha = 255 - (255 * (value - MIN_SCALE) / (MAX_SCALE - MIN_SCALE)).toInt()
            }
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                if (mCloseReason == CLOSE_REASON_EXPANDING_END || mCloseReason == CLOSE_REASON_TOUCH_UP) {
                    mOnPressListener?.onClick(this@SpringButton, false)
                }
            }

            override fun onAnimationEnd(animation: Animator?, isReverse: Boolean) {
                if (mState == STATE_CLOSING) {
                    mCloseAfterExpandEnd = false
                    mState = STATE_CLOSED
                }
                if (mCloseReason == CLOSE_REASON_LONG_PRESSING_END) {
                    mLongProgress = 0f
                    mPaint.alpha = 255
                }
            }
        })
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        style = Paint.Style.STROKE
        color = Color.WHITE
    }

    private fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
    }

    fun setStrokeWidth(strokeWidth: Float) {
        mPaint.strokeWidth = dp2px(strokeWidth)
    }

    fun setStrokeColor(@ColorInt color: Int) {
        mPaint.color = color
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val c = canvas ?: return
        val halfStrokeWidth = mPaint.strokeWidth / 2
        c.drawArc(0f + halfStrokeWidth, 0f + halfStrokeWidth,
                width.toFloat() - halfStrokeWidth, height.toFloat() - halfStrokeWidth,
                0f, mLongProgress, false, mPaint)
    }

    interface OnLongPressListener {
        fun onLongClick(view: View)
        fun onLongClickAbort(view: View) {}
    }

    interface OnPressListener {
        fun onClick(view: View, isLongPressStart: Boolean)
    }

    class WrappedClickListener(private val clickListener: OnClickListener) : OnPressListener {
        override fun onClick(view: View, isLongPressStart: Boolean) {
            clickListener.onClick(view)
        }
    }

    class WrappedLongClickListener(private val longClickListener: OnLongClickListener) : OnLongPressListener {
        override fun onLongClick(view: View) {
            longClickListener.onLongClick(view)
        }

        override fun onLongClickAbort(view: View) {
        }
    }
}