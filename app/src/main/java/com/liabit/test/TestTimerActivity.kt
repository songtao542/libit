package com.liabit.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.liabit.timerview.TimerView
import kotlinx.android.synthetic.main.activity_test_timer.*

class TestTimerActivity : AppCompatActivity() {

    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_timer)

        ResourcesCompat.getFont(this, R.font.dinalternate)?.let {
            timerView.setTypeface(it)
        }
        timerView.start(5000)
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

    }
}