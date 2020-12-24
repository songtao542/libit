package com.liabit.timerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.CountDownTimer
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import java.util.concurrent.TimeUnit

class TimerView : LinearLayout {
    private lateinit var day0: AppCompatTextView
    private lateinit var day1: DigitView
    private lateinit var day2: DigitView
    private lateinit var hour1: DigitView
    private lateinit var hour2: DigitView
    private lateinit var minute1: DigitView
    private lateinit var minute2: DigitView
    private lateinit var second1: DigitView
    private lateinit var second2: DigitView

    private lateinit var delimiterDayHour: TextView
    private lateinit var delimiterHourMinute: TextView
    private lateinit var delimiterMinuteSecond: TextView

    private lateinit var dayLayout: LinearLayout
    private lateinit var hourLayout: LinearLayout
    private lateinit var minuteLayout: LinearLayout
    private lateinit var secondLayout: LinearLayout

    private var countDownTimer: CountDownTimer? = null
    private var timeEndListener: OnTimeEndListener? = null

    private var tickInterval = 1000
    private var remainingTime = 0L
    private var resetSymbol = 0
    private var dayVisibility = View.VISIBLE
    private var dayUnit = ""

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
        orientation = HORIZONTAL
        View.inflate(context, R.layout.timer_view, this)

        day0 = findViewById(R.id.day0)
        day1 = findViewById(R.id.day1)
        day2 = findViewById(R.id.day2)
        hour1 = findViewById(R.id.hour1)
        hour2 = findViewById(R.id.hour2)
        minute1 = findViewById(R.id.minute1)
        minute2 = findViewById(R.id.minute2)
        second1 = findViewById(R.id.second1)
        second2 = findViewById(R.id.second2)

        delimiterDayHour = findViewById(R.id.delimiterDayHour)
        delimiterHourMinute = findViewById(R.id.delimiterHourMinute)
        delimiterMinuteSecond = findViewById(R.id.delimiterMinuteSecond)

        dayLayout = findViewById(R.id.dayLayout)
        hourLayout = findViewById(R.id.hourLayout)
        minuteLayout = findViewById(R.id.minuteLayout)
        secondLayout = findViewById(R.id.secondLayout)

        dayUnit = resources.getString(R.string.days)

        ResourcesCompat.getFont(context, R.font.digit)?.let {
            setTypeface(it)
        }

        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimerView, defStyleAttr, 0)
            resetSymbol = typedArray.getInt(R.styleable.TimerView_resetSymbol, 0)
            reset()

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                typedArray.getFont(R.styleable.TimerView_android_fontFamily)?.let {
                    setTypeface(it)
                }
            }

            dayVisibility = typedArray.getInt(R.styleable.TimerView_dayVisibility, dayVisibility)
            dayLayout.visibility = dayVisibility
            delimiterDayHour.visibility = dayVisibility

            dayUnit = typedArray.getString(R.styleable.TimerView_dayUnit) ?: dayUnit
            delimiterDayHour.text = dayUnit

            val corners = typedArray.getDimension(R.styleable.TimerView_corners, 0f)
            setOutlineProvider(dayLayout, corners)
            setOutlineProvider(hourLayout, corners)
            setOutlineProvider(minuteLayout, corners)
            setOutlineProvider(secondLayout, corners)

            val digitBackgroundColor = typedArray.getColor(R.styleable.TimerView_digitBackgroundColor, Color.WHITE)
            dayLayout.setBackgroundColor(digitBackgroundColor)
            hourLayout.setBackgroundColor(digitBackgroundColor)
            minuteLayout.setBackgroundColor(digitBackgroundColor)
            secondLayout.setBackgroundColor(digitBackgroundColor)
            day0.setBackgroundColor(digitBackgroundColor)
            day1.setBackgroundColor(digitBackgroundColor)
            day2.setBackgroundColor(digitBackgroundColor)
            hour1.setBackgroundColor(digitBackgroundColor)
            hour2.setBackgroundColor(digitBackgroundColor)
            minute1.setBackgroundColor(digitBackgroundColor)
            minute2.setBackgroundColor(digitBackgroundColor)
            second1.setBackgroundColor(digitBackgroundColor)
            second2.setBackgroundColor(digitBackgroundColor)

            val delimiterTextColor = typedArray.getColorStateList(R.styleable.TimerView_delimiterTextColor)
            if (delimiterTextColor != null) {
                delimiterDayHour.setTextColor(delimiterTextColor)
                delimiterHourMinute.setTextColor(delimiterTextColor)
                delimiterMinuteSecond.setTextColor(delimiterTextColor)
            }

            val delimiterTextSize = typedArray.getDimensionPixelSize(R.styleable.TimerView_delimiterTextSize, 0).toFloat()
            if (delimiterTextSize > 0) {
                delimiterDayHour.setTextSize(TypedValue.COMPLEX_UNIT_PX, delimiterTextSize)
                delimiterHourMinute.setTextSize(TypedValue.COMPLEX_UNIT_PX, delimiterTextSize)
                delimiterMinuteSecond.setTextSize(TypedValue.COMPLEX_UNIT_PX, delimiterTextSize)
            }

            val digitTextColor = typedArray.getColorStateList(R.styleable.TimerView_digitTextColor)
            if (digitTextColor != null) {
                day0.setTextColor(digitTextColor)
                day1.setTextColor(digitTextColor)
                day2.setTextColor(digitTextColor)
                hour1.setTextColor(digitTextColor)
                hour2.setTextColor(digitTextColor)
                minute1.setTextColor(digitTextColor)
                minute2.setTextColor(digitTextColor)
                second1.setTextColor(digitTextColor)
                second2.setTextColor(digitTextColor)
            }

            val digitTextSize = typedArray.getDimension(R.styleable.TimerView_digitTextSize, 0f)
            if (digitTextSize > 0) {
                day0.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                day1.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                day2.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                hour1.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                hour2.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                minute1.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                minute2.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                second1.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
                second2.setTextSize(TypedValue.COMPLEX_UNIT_PX, digitTextSize)
            }

            val digitPadding = typedArray.getDimensionPixelSize(R.styleable.TimerView_digitPadding, 0)
            if (digitPadding > 0) {
                dayLayout.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
                hourLayout.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
                minuteLayout.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
                secondLayout.setPadding(digitPadding, digitPadding, digitPadding, digitPadding)
            }

            val delimiterPadding = typedArray.getDimensionPixelSize(R.styleable.TimerView_delimiterPadding, 0)
            if (delimiterPadding > 0) {
                delimiterDayHour.setPadding(delimiterPadding, delimiterPadding, delimiterPadding, delimiterPadding)
                delimiterHourMinute.setPadding(delimiterPadding, delimiterPadding, delimiterPadding, delimiterPadding)
                delimiterMinuteSecond.setPadding(delimiterPadding, delimiterPadding, delimiterPadding, delimiterPadding)
            }

            val animationDuration = typedArray.getInt(R.styleable.TimerView_animationDuration, 0)
            if (animationDuration in 1..999) {
                setAnimationDuration(animationDuration.toLong())
            }

            tickInterval = typedArray.getInt(R.styleable.TimerView_tickInterval, tickInterval)

            typedArray.recycle()
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setOutlineProvider(view: View, corners: Float) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            view.clipToOutline = true
            view.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    if (view == null) return
                    val w = view.width
                    val h = view.height
                    outline?.setRoundRect(0, 0, w, h, corners)
                }
            }
        }
    }

    fun start(millisInFuture: Long) {
        countDownTimer?.cancel()
        val days = TimeUnit.MILLISECONDS.toDays(millisInFuture)
        if (days > 0) {
            dayLayout.visibility = View.VISIBLE
            delimiterDayHour.visibility = View.VISIBLE
        } else {
            dayLayout.visibility = dayVisibility
            delimiterDayHour.visibility = dayVisibility
        }
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

        val daysString = days.toString()
        val hoursString = hours.toString()
        val minutesString = minutes.toString()
        val secondsString = seconds.toString()

        val dayLength = daysString.length
        when {
            dayLength > 2 -> {
                day0.text = daysString.substring(0, dayLength - 2)
                day1.animateTextChange((daysString[dayLength - 2].toString()))
                day2.animateTextChange((daysString[dayLength - 1].toString()))
            }
            dayLength == 2 -> {
                day1.animateTextChange((daysString[0].toString()))
                day2.animateTextChange((daysString[1].toString()))
            }
            dayLength == 1 -> {
                day1.animateTextChange(("0"))
                day2.animateTextChange((daysString[0].toString()))
            }
            else -> {
                day1.animateTextChange(("3"))
                day2.animateTextChange(("0"))
            }
        }

        when (hoursString.length) {
            2 -> {
                hour1.animateTextChange((hoursString[0].toString()))
                hour2.animateTextChange((hoursString[1].toString()))
            }
            1 -> {
                hour1.animateTextChange(("0"))
                hour2.animateTextChange((hoursString[0].toString()))
            }
            else -> {
                hour1.animateTextChange(("1"))
                hour2.animateTextChange(("1"))
            }
        }

        when (minutesString.length) {
            2 -> {
                minute1.animateTextChange((minutesString[0].toString()))
                minute2.animateTextChange((minutesString[1].toString()))
            }
            1 -> {
                minute1.animateTextChange(("0"))
                minute2.animateTextChange((minutesString[0].toString()))
            }
            else -> {
                minute1.animateTextChange(("5"))
                minute2.animateTextChange(("9"))
            }
        }
        when (secondsString.length) {
            2 -> {
                second1.animateTextChange((secondsString[0].toString()))
                second2.animateTextChange((secondsString[1].toString()))
            }
            1 -> {
                second1.animateTextChange(("0"))
                second2.animateTextChange((secondsString[0].toString()))
            }
            else -> {
                second1.animateTextChange((secondsString[secondsString.length - 2].toString()))
                second2.animateTextChange((secondsString[secondsString.length - 1].toString()))
            }
        }
    }

    private fun setAnimationDuration(animationDuration: Long) {
        day1.setAnimationDuration(animationDuration)
        day2.setAnimationDuration(animationDuration)
        hour1.setAnimationDuration(animationDuration)
        hour2.setAnimationDuration(animationDuration)
        minute1.setAnimationDuration(animationDuration)
        minute2.setAnimationDuration(animationDuration)
        second1.setAnimationDuration(animationDuration)
        second2.setAnimationDuration(animationDuration)
    }

    fun setTimeEndListener(countdownListener: OnTimeEndListener?) {
        this.timeEndListener = countdownListener
    }

    interface OnTimeEndListener {
        fun onTimeEnd()
    }

    fun reset() {
        countDownTimer?.cancel()
        val symbol = resetSymbol.toString()
        day0.text = symbol
        day1.setText(symbol)
        day2.setText(symbol)
        hour1.setText(symbol)
        hour2.setText(symbol)
        minute1.setText(symbol)
        minute2.setText(symbol)
        second1.setText(symbol)
        second2.setText(symbol)
    }

    fun pause() {
        countDownTimer?.cancel()
    }

    fun resume() {
        start(remainingTime)
    }

    fun setTypeface(typeface: Typeface) {
        day0.typeface = typeface
        day1.setTypeFace(typeface)
        day2.setTypeFace(typeface)
        hour1.setTypeFace(typeface)
        hour2.setTypeFace(typeface)
        minute1.setTypeFace(typeface)
        minute2.setTypeFace(typeface)
        second1.setTypeFace(typeface)
        second2.setTypeFace(typeface)
    }

}
