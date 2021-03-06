package com.liabit.timerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class TimerTextView : AppCompatTextView {

    companion object {
        private const val FORMAT_TWO = "%02d"
        private const val FORMAT = "%02d:%02d:%02d"
    }

    private var countDownTimer: CountDownTimer? = null
    private var onTimeEndListener: OnTimeEndListener? = null
    private var onCountDownListener: OnCountDownListener? = null
    private var tickInterval = 1000
    private var prefix = ""
    private var suffix = ""
    private var dayUnit = ""
    private var remainingTime = 0L
    private var pauseTime = 0L
    private var millisInFuture = 0L
    private var showStrokeProgress = false
    private var showAsCountDownButton = false
    private var showCountDownText = true
    private var strokeColor = Color.WHITE
    private var strokeWidth = 0f
    private lateinit var strokePaint: Paint

    private var dayFormat = FORMAT_TWO
    private var timeFormat = FORMAT

    private val format by lazy { DecimalFormat.getInstance().apply { this.maximumFractionDigits = 0 } }

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        gravity = Gravity.CENTER
        var millisInFuture = 0
        isClickable = true
        strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context.resources.displayMetrics)
        strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = strokeWidth
        }
        dayUnit = resources.getString(R.string.days)
        if (attrs != null) {
            val gravityAttr = intArrayOf(android.R.attr.gravity)
            val gravityTypedArray = context.obtainStyledAttributes(attrs, gravityAttr)
            gravity = gravityTypedArray.getInt(0, Gravity.CENTER)
            gravityTypedArray.recycle()

            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView, defStyleAttr, 0)
            tickInterval = typedArray.getInt(R.styleable.TimerTextView_tickInterval, tickInterval)
            prefix = typedArray.getString(R.styleable.TimerTextView_prefix) ?: ""
            suffix = typedArray.getString(R.styleable.TimerTextView_suffix) ?: ""
            dayUnit = typedArray.getString(R.styleable.TimerTextView_dayUnit) ?: dayUnit
            dayFormat = typedArray.getString(R.styleable.TimerTextView_dayFormat) ?: dayFormat
            timeFormat = typedArray.getString(R.styleable.TimerTextView_timeFormat) ?: timeFormat
            millisInFuture = typedArray.getInt(R.styleable.TimerTextView_millisInFuture, 0)
            showStrokeProgress = typedArray.getBoolean(R.styleable.TimerTextView_showStrokeProgress, false)
            strokeColor = typedArray.getColor(R.styleable.TimerTextView_strokeColor, Color.WHITE)
            strokeWidth = typedArray.getDimension(R.styleable.TimerTextView_strokeWidth, strokeWidth)
            strokePaint.strokeWidth = strokeWidth
            showAsCountDownButton = typedArray.getBoolean(R.styleable.TimerTextView_showAsCountDownButton, false)
            showCountDownText = typedArray.getBoolean(R.styleable.TimerTextView_showCountDownText, true)
            val backgroundAttr = intArrayOf(android.R.attr.background)
            val backgroundTypedArray = context.obtainStyledAttributes(attrs, backgroundAttr)
            val backgroundDrawable = backgroundTypedArray.getDrawable(0)
            backgroundTypedArray.recycle()
            if (backgroundDrawable == null && showAsCountDownButton) {
                setBackgroundResource(R.drawable.circle_timer_selector)
            }
            if (showStrokeProgress) {
                tickInterval = 10
            }
            typedArray.recycle()
        }
        if (millisInFuture < 0) {
            millisInFuture = 0
        }
        if (millisInFuture == 0) {
            @SuppressLint("SetTextI18n")
            text = prefix + String.format(FORMAT, 0, 0, 0) + suffix
        } else {
            start(millisInFuture.toLong())
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (remainingTime > 0) {
            remainingTime -= (SystemClock.elapsedRealtime() - pauseTime)
            if (countDownTimer == null && remainingTime > 0) {
                start(remainingTime)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pauseTime = SystemClock.elapsedRealtime()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    /*override fun onSaveInstanceState(): Parcelable? {
        val parcelable = super.onSaveInstanceState()
        val state = TimerSavedState(parcelable)
        state.time = remainingTime
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is TimerSavedState) {
            super.onRestoreInstanceState(state)
        }
        val ss = state as TimerSavedState
        super.onRestoreInstanceState(ss.superState)
    }

    class TimerSavedState : BaseSavedState {
        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<TimerSavedState?> = object : Parcelable.Creator<TimerSavedState?> {
                override fun createFromParcel(source: Parcel): TimerSavedState {
                    return TimerSavedState(source)
                }

                override fun newArray(size: Int): Array<TimerSavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }

        var time: Long = 0

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeLong(time)
        }

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel?) : super(parcel) {
            time = parcel?.readLong() ?: 0
        }
    }*/

    fun start(millisInFuture: Long) {
        if (millisInFuture <= 0) return
        this.millisInFuture = millisInFuture
        countDownTimer?.cancel()
        remainingTime = millisInFuture
        countDownTimer = object : CountDownTimer(millisInFuture, tickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateDigit(millisUntilFinished)
                onCountDownListener?.onCountDown(millisUntilFinished)
            }

            override fun onFinish() {
                remainingTime = 0
                updateDigit(0)
                invalidate()
                onTimeEndListener?.onTimeEnd()
            }
        }
        countDownTimer?.start()
    }

    private fun updateDigit(timeToStart: Long) {
        val text = if (showAsCountDownButton) {
            prefix + (if (showCountDownText) format.format(ceil(timeToStart.toDouble() / 1000.0)) else "") + suffix
        } else {
            val days = TimeUnit.MILLISECONDS.toDays(timeToStart)
            val dm = TimeUnit.DAYS.toMillis(days)
            val hours = TimeUnit.MILLISECONDS.toHours(timeToStart - dm)
            val hm = TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToStart - (dm + hm))
            val mm = TimeUnit.MINUTES.toMillis(minutes)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToStart - (dm + hm + mm))
            if (days > 0) {
                prefix + String.format(dayFormat, days) + dayUnit + String.format(timeFormat, hours, minutes, seconds) + suffix
            } else {
                prefix + String.format(timeFormat, hours, minutes, seconds) + suffix
            }
        }
        setText(text)
    }

    fun setTimeEndListener(onTimeEndListener: OnTimeEndListener?) {
        this.onTimeEndListener = onTimeEndListener
    }

    fun setTimeEndListener(onTimeEndListener: (() -> Unit)) {
        this.onTimeEndListener = object : OnTimeEndListener {
            override fun onTimeEnd() {
                onTimeEndListener.invoke()
            }
        }
    }

    fun setOnCountDownListener(countdownListener: OnCountDownListener?) {
        this.onCountDownListener = countdownListener
    }

    fun setOnCountDownListener(countdownListener: ((millisUntilFinished: Long) -> Unit)) {
        this.onCountDownListener = object : OnCountDownListener {
            override fun onCountDown(millisUntilFinished: Long) {
                countdownListener.invoke(millisUntilFinished)
            }
        }
    }

    interface OnTimeEndListener {
        fun onTimeEnd()
    }

    interface OnCountDownListener {
        fun onCountDown(millisUntilFinished: Long)
    }

    fun cancel() {
        countDownTimer?.cancel()
        remainingTime = 0L
        millisInFuture = 0L
    }

    fun pause() {
        countDownTimer?.cancel()
    }

    fun resume() {
        start(remainingTime)
    }

    override fun onDraw(c: Canvas?) {
        val canvas = c ?: return
        super.onDraw(canvas)
        if (showStrokeProgress) {
            val halfStrokeWidth = strokePaint.strokeWidth / 2
            val progress = 1f - (remainingTime.toDouble() / millisInFuture.toDouble()).toFloat()
            strokePaint.color = strokeColor
            canvas.drawArc(
                0f + halfStrokeWidth, 0f + halfStrokeWidth,
                width.toFloat() - halfStrokeWidth, height.toFloat() - halfStrokeWidth,
                0f, progress * 360, false, strokePaint
            )
        }
    }
}
