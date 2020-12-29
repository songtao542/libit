package com.liabit.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.statebutton.SpringButton
import com.liabit.test.databinding.ActivityStatebuttonTestBinding
import com.liabit.viewbinding.inflate

class TestStateButtonActivity : AppCompatActivity() {

    private val binding by inflate<ActivityStatebuttonTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.pauseButton.setOnClickListener {
            binding.finishButton.show()
            binding.continueButton.show()
            binding.pauseButton.hide()
        }

        binding.finishButton.setOnLongPressListener(object : SpringButton.OnLongPressListener {
            override fun onLongClick(view: View) {
                Log.d("TTTT", "finishButton long clicked")
            }

            override fun onLongClickAbort(view: View) {
                Log.d("TTTT", "finishButton long click abort")
            }
        })

        binding.finishButton.setOnPressListener(object : SpringButton.OnPressListener {
            override fun onClick(view: View, isLongPressStart: Boolean) {
                //finish()
                Log.d("TTTT", "finishButton clicked  isLongPressStart: $isLongPressStart")
            }
        })

        binding.continueButton.setOnClickListener {
            binding.finishButton.hide()
            binding.continueButton.hide()
            binding.pauseButton.show()
        }
    }
}
