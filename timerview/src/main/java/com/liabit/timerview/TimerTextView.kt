package com.liabit.timerview

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import java.util.concurrent.TimeUnit

class TimerTextView : AppCompatTextView {

    companion object {
        private const val FORMAT_TWO = "%02d"
        private const val FORMAT = "%02d:%02d:%02d"
    }

    private var countDownTimer: CountDownTimer? = null
    private var timeEndListener: OnTimeEndListener? = null
    private var tickInterval = 1000
    private var prefix = ""
    private var remainingTime = 0L

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
        var millisInFuture = 0
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerTextView, defStyleAttr, 0)
            tickInterval = typedArray.getInt(R.styleable.TimerTextView_tickInterval, tickInterval)
            prefix = typedArray.getString(R.styleable.TimerTextView_prefix) ?: ""
            millisInFuture = typedArray.getInt(R.styleable.TimerTextView_millisInFuture, 0)
            typedArray.recycle()
        }
        if (millisInFuture < 0) {
            millisInFuture = 0
        }
        if (millisInFuture == 0) {
            @SuppressLint("SetTextI18n")
            text = prefix + String.format(FORMAT, 0, 0, 0)
        } else {
            start(millisInFuture.toLong())
        }
    }

    fun start(millisInFuture: Long) {
        countDownTimer?.cancel()
        remainingTime = millisInFuture
        countDownTimer = object : CountDownTimer(millisInFuture, tickInterval.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                updateDigit(millisUntilFinished)
            }

            override fun onFinish() {
                timeEndListener?.onTimeEnd()
            }
        }
        countDownTimer?.start()
    }

    private fun updateDigit(timeToStart: Long) {
        val days = TimeUnit.MILLISECONDS.toDays(timeToStart)
        val dm = TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(timeToStart - dm)
        val hm = TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeToStart - (dm + hm))
        val mm = TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeToStart - (dm + hm + mm))
        val text = if (days > 0) {
            prefix + String.format(FORMAT_TWO, days) + String.format(FORMAT, hours, minutes, seconds)
        } else {
            prefix + String.format(FORMAT, hours, minutes, seconds)
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

}
