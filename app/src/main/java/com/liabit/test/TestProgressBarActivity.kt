package com.liabit.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.statebutton.MaterialProgressDrawable
import com.liabit.test.databinding.ActivityTestProgressBarBinding
import com.liabit.viewbinding.inflate

class TestProgressBarActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestProgressBarBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_progress_bar)

        val d = MaterialProgressDrawable(this, binding.drawableView)
        d.setColorSchemeColors(0xffff0000.toInt(), 0xff0000ff.toInt())
        binding.drawableView.background = d
        d.start()

        val od = com.liabit.test.temp.MaterialProgressDrawable(this, binding.originMPD)
        od.alpha = 255
        od.setColorSchemeColors(0xffff0000.toInt(), 0xff0000ff.toInt())
        binding.originMPD.background = od
        od.start()
    }
}