package com.liabit.test

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.liabit.tablayout.DefaultTabAdapter
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.indicator.BezierTabIndicator
import com.liabit.tablayout.indicator.LineTabIndicator
import com.liabit.tablayout.indicator.TriangularTabIndicator
import com.liabit.tablayout.indicator.WrapTabIndicator
import kotlinx.android.synthetic.main.activity_test_tablayout_fix_mode.*
import java.lang.StringBuilder
import kotlin.random.Random

class TestTabLayoutFixModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_tablayout_fix_mode)

        val test = arrayOf(R.mipmap.test1, R.mipmap.test2, R.mipmap.test3)

        viewPager.adapter = object : PagerAdapter() {
            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun getCount() = 3

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(this@TestTabLayoutFixModeActivity)
                imageView.setImageResource(test[position % 3])
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

        tab5.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return WrapTabIndicator(context)
            }
        }
        tab5.setupWith(viewPager)
    }
}
