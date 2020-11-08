package com.liabit.test

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.liabit.extension.dip
import com.liabit.extension.dp
import com.liabit.tablayout.*
import com.liabit.tablayout.indicator.*
import kotlinx.android.synthetic.main.activity_test_tab_layout.*
import java.lang.StringBuilder
import kotlin.random.Random

class TestTabLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_tab_layout)

        fixMode.setOnClickListener {
            startActivity(Intent(this, TestTabLayoutFixModeActivity::class.java))
        }

        val test = arrayOf(R.mipmap.test1, R.mipmap.test2, R.mipmap.test3, R.mipmap.test4, R.mipmap.test5)

        viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun getCount() = 10

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(this@TestTabLayoutActivity)
                imageView.setImageResource(test[position % 5])
                container.addView(imageView)
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View?)
            }

            override fun getPageTitle(position: Int): CharSequence? {
                val randomLength = Random.nextInt(8)
                val pad = StringBuilder()
                for (i in 0 until randomLength) {
                    pad.append("P")
                }
                return "Title$pad$position"
            }
        }

        tab1.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return if (type == TabIndicator.INTERPOLATOR_START)
                    AccelerateInterpolator()
                else
                    DecelerateInterpolator(1.6f)
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return LineTabIndicator(context)
            }
        }
        tab1.setupWith(viewPager)

        tab2.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return if (type == TabIndicator.INTERPOLATOR_START)
                    AccelerateInterpolator()
                else
                    DecelerateInterpolator(1.6f)
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return BezierTabIndicator(context)
            }
        }
        tab2.setupWith(viewPager)

        tab3.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return if (type == TabIndicator.INTERPOLATOR_START)
                    AccelerateInterpolator()
                else
                    DecelerateInterpolator(1.6f)
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return TriangularTabIndicator(context)
            }
        }
        tab3.setupWith(viewPager)

        tab4.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return WrapTabIndicator(context)
            }
        }
        tab4.setupWith(viewPager)

        tab5.tabAdapter = object : TabAdapter {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateDecelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                val indicator = ScaleCircleIndicator(context)
                indicator.setCount(count)
                indicator.setFill(true)
                indicator.setMaxRadius(5.dp(this@TestTabLayoutActivity))
                indicator.color = 0xff333333.toInt()
                indicator.isClickable = true
                indicator.selectColor = 0xffff0000.toInt()
                return indicator
            }
        }
        tab5.setupWith(viewPager)

    }
}