package com.liabit.test

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.databinding.ActivityTestTimerBinding
import com.liabit.timerview.TimerTextView
import com.liabit.timerview.TimerView
import com.liabit.viewbinding.inflate

class TestTimerActivity : AppCompatActivity() {

    private var isRunning = true

    private val binding by inflate<ActivityTestTimerBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_timer)

        /*ResourcesCompat.getFont(this, R.font.digi)?.let {
            timerView.setTypeface(it)
        }*/
        //timerView.start(9000000000000 -  55L * 24 * 60 * 60 * 1000 )
        binding.timerView.start(55L * 24 * 60 * 60 * 1000)
        binding.timerView.setTimeEndListener(object : TimerView.OnTimeEndListener {
            override fun onTimeEnd() {
                Toast.makeText(this@TestTimerActivity, "Finished", Toast.LENGTH_SHORT).show()
                binding.timerView.reset()
                isRunning = false
                binding.pauseButton.isEnabled = false
            }
        })

        binding.pauseButton.setOnClickListener {
            if (isRunning) {
                isRunning = false
                binding.timerView.pause()
                binding.pauseButton.text = "继续"
            } else {
                isRunning = true
                binding.timerView.resume()
                binding.pauseButton.text = "暂停"
            }
        }

        //timerTextView.start(10009900)
        binding.timerTextView.start(2090159900)
        binding.timerTextView.setTimeEndListener(object : TimerTextView.OnTimeEndListener {
            override fun onTimeEnd() {
                Toast.makeText(this@TestTimerActivity, "Finished", Toast.LENGTH_SHORT).show()
                binding.timerTextView.reset()
            }
        })
    }
}