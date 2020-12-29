package com.liabit.test.gesturetest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.liabit.test.R
import com.liabit.test.databinding.ActivityMainGestureSwipeBinding
import com.liabit.viewbinding.inflate

class TestSwipeActivity : AppCompatActivity() {

    private val binding by inflate<ActivityMainGestureSwipeBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val page1 = LayoutInflater.from(this).inflate(R.layout.page_swipe, binding.viewPager, false)
        val page2 = LayoutInflater.from(this).inflate(R.layout.page_swipe, binding.viewPager, false)
        val page3 = LayoutInflater.from(this).inflate(R.layout.page_swipe, binding.viewPager, false)

        binding.viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun getCount(): Int {
                return 3
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View)
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                if (position == 1) {
                    container.addView(page1)
                    return page1
                } else if (position == 2) {
                    container.addView(page2)
                    return page2
                } else {
                    container.addView(page3)
                    return page3
                }
            }

        }

    }
}
