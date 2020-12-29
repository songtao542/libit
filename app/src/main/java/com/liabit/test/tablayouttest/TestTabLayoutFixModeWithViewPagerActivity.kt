package com.liabit.test.tablayouttest

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
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestTablayoutFixModeWithViewpagerBinding
import com.liabit.viewbinding.inflate
import kotlin.random.Random

class TestTabLayoutFixModeWithViewPagerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestTablayoutFixModeWithViewpagerBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val test = arrayOf(R.mipmap.test1, R.mipmap.test2, R.mipmap.test3)

        binding.viewPager.adapter = object : PagerAdapter() {

            val pageCount = 3

            val titles = Array(pageCount) {
                val randomLength = Random.nextInt(8)
                val pad = StringBuilder()
                for (i in 0 until randomLength + 1) {
                    pad.append("P")
                }
                return@Array "Title$it-$pad"
            }

            override fun isViewFromObject(view: View, obj: Any): Boolean {
                return view == obj
            }

            override fun getCount() = pageCount

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(this@TestTabLayoutFixModeWithViewPagerActivity)
                imageView.setImageResource(test[position % 3])
                container.addView(imageView)
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
                container.removeView(obj as View?)
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }
        }

        binding.tab0.setupWithViewPager(binding.viewPager)

        binding.tab1.tabAdapter = object : DefaultTabAdapter() {
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
        binding.tab1.setupWith(binding.viewPager)

        binding.tab2.tabAdapter = object : DefaultTabAdapter() {
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
        binding.tab2.setupWith(binding.viewPager)

        binding.tab3.tabAdapter = object : DefaultTabAdapter() {
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
        binding.tab3.setupWith(binding.viewPager)

        binding.tab4.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return WrapTabIndicator(context)
            }
        }
        binding.tab4.setupWith(binding.viewPager)

        binding.tab5.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return WrapTabIndicator(context)
            }
        }
        binding.tab5.setupWith(binding.viewPager)
    }
}
