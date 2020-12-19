package com.liabit.timerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

class TimerTextView : AppCompatTextView {

    companion object {
        private const val FORMAT_TWO = "%02d"
        private const val FORMAT = "%02d:%02d:%02d"
    }

    private var countDownTimer: CountDownTimer? = null
    private var timeEndListener: OnTimeEndListener? = null
    private var tickInterval = 1000
    private var prefix = ""
    private var suffix = ""
    private var dayUnit = ""
    private var remainingTime = 0L
    private var millisInFuture = 0L
    private var showStrokeProgress = false
    private var showAsCountDownButton = false
    private var showCountDownText = true
    private var strokeColor = Color.WHITE
    private var strokeWidth = 0f
    private lateinit var strokePaint: Paint

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
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView, defStyleAttr, 0)
            tickInterval = typedArray.getInt(R.styleable.TimerTextView_tickInterval, tickInterval)
            prefix = typedArray.getString(R.styleable.TimerTextView_prefix) ?: ""
            suffix = typedArray.getString(R.styleable.TimerTextView_suffix) ?: ""
            dayUnit = typedArray.getString(R.styleable.TimerView_dayUnit) ?: dayUnit
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

    fun start(millisInFuture: Long) {
        this.millisInFuture = millisInFuture
        countDownTimer?.cancel()
        remainingTime = millisInFuture
        countDownTimer = object : CountDownTimer(millisInFuture, tickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateDigit(millisUntilFinished)
            }

            override fun onFinish() {
                remainingTime = 0
                updateDigit(0)
                invalidate()
                timeEndListener?.onTimeEnd()
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
                prefix + String.format(FORMAT_TWO, days) + dayUnit + String.format(FORMAT, hours, minutes, seconds) + suffix
            } else {
                prefix + String.format(FORMAT, hours, minutes, seconds) + suffix
            }
        }
        setText(text)
    }

    fun setTimeEndListener(countdownListener: OnTimeEndListener?) {
        this.timeEndListener = countdownListener
    }

    interface OnTimeEndListener {
        fun onTimeEnd()
    }

    fun reset() {
        countDownTimer?.cancel()
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
            Log.d("TTTT", "progress=$progress")
            strokePaint.color = strokeColor
            canvas.drawArc(0f + halfStrokeWidth, 0f + halfStrokeWidth,
                    width.toFloat() - halfStrokeWidth, height.toFloat() - halfStrokeWidth,
                    0f, progress * 360, false, strokePaint)
        }
    }
}
