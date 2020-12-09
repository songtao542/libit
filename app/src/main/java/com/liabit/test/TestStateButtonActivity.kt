package com.liabit.test

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.liabit.statebutton.SpringButton
import kotlinx.android.synthetic.main.activity_statebutton_test.*

class TestStateButtonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statebutton_test)

        pauseButton.setOnClickListener {
            finishButton.show()
            continueButton.show()
            pauseButton.hide()
        }

        finishButton.setOnLongPressListener(object : SpringButton.OnLongPressListener {
            override fun onLongClick(view: View) {
                Log.d("TTTT", "finishButton long clicked")
            }

            override fun onLongClickAbort(view: View) {
                Log.d("TTTT", "finishButton long click abort")
            }
        })

        finishButton.setOnPressListener(object : SpringButton.OnPressListener {
            override fun onClick(view: View, isLongPressStart: Boolean) {
                //finish()
                Log.d("TTTT", "finishButton clicked  isLongPressStart: $isLongPressStart")
            }
        })

        continueButton.setOnClickListener {
            finishButton.hide()
            continueButton.hide()
            pauseButton.show()
        }
    }
}
