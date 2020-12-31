package com.liabit.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.widget.SpringButton
import com.liabit.test.databinding.ActivityStatebuttonTestBinding
import com.liabit.viewbinding.inflate
import com.liabit.widget.ProgressButton

class TestStateButtonActivity : AppCompatActivity() {

    private val binding by inflate<ActivityStatebuttonTestBinding>()

    private var mProgressVisibility1 = View.GONE
    private var mProgressVisibility2 = View.GONE

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

        binding.progressButton1.setOnClickListener {
            if (mProgressVisibility1 == View.GONE) {
                mProgressVisibility1 = View.VISIBLE
                binding.progressButton1.setMode(ProgressButton.PROGRESS)
            } else {
                mProgressVisibility1 = View.GONE
                binding.progressButton1.setMode(ProgressButton.TEXT)
            }
        }

        binding.progressButton2.setOnClickListener {
            if (mProgressVisibility2 == View.GONE) {
                mProgressVisibility2 = View.VISIBLE
                binding.progressButton2.setMode(ProgressButton.PROGRESS)
            } else {
                mProgressVisibility2 = View.GONE
                binding.progressButton2.setMode(ProgressButton.TEXT)
            }
        }
    }
}
