package com.liabit.test.tablayouttest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestTabLayoutBinding
import com.liabit.viewbinding.inflate

class TestTabLayoutActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestTabLayoutBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.viewPager -> {
                startActivity(Intent(this, TestTabLayoutWithViewPagerActivity::class.java))
            }
            R.id.viewPagerFixMode -> {
                startActivity(Intent(this, TestTabLayoutFixModeWithViewPagerActivity::class.java))
            }
            R.id.viewPager2 -> {
                startActivity(Intent(this, TestTabLayoutWithViewPager2Activity::class.java))
            }
            R.id.viewPager2FixMode -> {
                startActivity(Intent(this, TestTabLayoutFixModeWithViewPager2Activity::class.java))
            }
        }
    }
}