package com.liabit.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.liabit.extension.dip
import com.liabit.extension.dp
import com.liabit.statebutton.MaterialProgressDrawable
import com.liabit.timerview.TimerTextView
import com.liabit.timerview.TimerView
import kotlinx.android.synthetic.main.activity_test_timer.*

class TestTimerActivity : AppCompatActivity() {

    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_timer)

        /*ResourcesCompat.getFont(this, R.font.digi)?.let {
            timerView.setTypeface(it)
        }*/
        timerView.start(9000000000000)
        timerView.setTimeEndListener(object : TimerView.OnTimeEndListener {
            override fun onTimeEnd() {
                Toast.makeText(this@TestTimerActivity, "Finished", Toast.LENGTH_SHORT).show()
                timerView.reset()
                isRunning = false
                pauseButton.isEnabled = false
            }
        })

        pauseButton.setOnClickListener {
            if (isRunning) {
                isRunning = false
                timerView.pause()
                pauseButton.text = "继续"
            } else {
                isRunning = true
                timerView.resume()
                pauseButton.text = "暂停"
            }
        }

        //timerTextView.start(10009900)
        timerTextView.start(2090159900)
        timerTextView.setTimeEndListener(object : TimerTextView.OnTimeEndListener {
            override fun onTimeEnd() {
                Toast.makeText(this@TestTimerActivity, "Finished", Toast.LENGTH_SHORT).show()
                timerTextView.reset()
            }
        })

        val d = MaterialProgressDrawable(this, drawableView)
        d.setColorSchemeColors(0xffff0000.toInt(), 0xff0000ff.toInt())
        drawableView.background = d
        d.start()

        val od = com.liabit.test.temp.MaterialProgressDrawable(this, originMPD)
        od.alpha = 255
        od.setColorSchemeColors(0xffff0000.toInt(), 0xff0000ff.toInt())
        originMPD.background = od
        od.start()

    }
}