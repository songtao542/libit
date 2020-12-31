package com.liabit.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.widget.MaterialProgressDrawable
import com.liabit.test.databinding.ActivityTestProgressBarBinding
import com.liabit.viewbinding.inflate

class TestProgressBarActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestProgressBarBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val d = MaterialProgressDrawable(this, binding.drawableView)
        d.setColorSchemeColors(0xffff0000.toInt(), 0xff0000ff.toInt())
        binding.drawableView.background = d
        d.start()

    }
}