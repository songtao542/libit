package com.liabit.test.tablayouttest

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.liabit.extension.dp
import com.liabit.tablayout.DefaultTabAdapter
import com.liabit.tablayout.TabAdapter
import com.liabit.tablayout.TabIndicator
import com.liabit.tablayout.indicator.*
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestTabLayoutWithViewpager2Binding
import com.liabit.viewbinding.inflate
import kotlin.random.Random

class TestTabLayoutWithViewPager2Activity : AppCompatActivity() {

    private val binding by inflate<ActivityTestTabLayoutWithViewpager2Binding>()

    private lateinit var mAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_tab_layout_with_viewpager2)
        mAdapter = ViewPagerAdapter()
        binding.viewPager.adapter = mAdapter

        val mediator = TabLayoutMediator(binding.tab0, binding.viewPager) { tab, position ->
            tab.text = mAdapter.titles[position]
        }
        //要执行这一句才是真正将两者绑定起来
        mediator.attach()

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
        binding.tab1.setupWith(binding.viewPager, mAdapter.titles)

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
        binding.tab2.setupWith(binding.viewPager, mAdapter.titles)

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
        binding.tab3.setupWith(binding.viewPager, mAdapter.titles)

        binding.tab4.tabAdapter = object : DefaultTabAdapter() {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                return WrapTabIndicator(context)
            }
        }
        binding.tab4.setupWith(binding.viewPager, mAdapter.titles)

        binding.tab5.tabAdapter = object : TabAdapter {
            override fun onCreateInterpolator(type: Int): Interpolator {
                return AccelerateDecelerateInterpolator()
            }

            override fun onCreateTabIndicator(context: Context, count: Int): TabIndicator {
                val indicator = ScaleCircleIndicator(context)
                indicator.setCount(count)
                indicator.setFill(true)
                indicator.setMaxRadius(5.dp(this@TestTabLayoutWithViewPager2Activity))
                indicator.color = 0xff333333.toInt()
                indicator.isClickable = true
                indicator.selectColor = 0xffff0000.toInt()
                return indicator
            }
        }
        binding.tab5.setupWith(binding.viewPager, mAdapter.titles)

    }

    class ViewPagerAdapter : RecyclerView.Adapter<ViewPagerAdapter.Holder>() {

        private val pageCount = 10

        val test = arrayOf(R.mipmap.test1, R.mipmap.test2, R.mipmap.test3)

        val titles = Array(pageCount) {
            val randomLength = Random.nextInt(8)
            val pad = StringBuilder()
            for (i in 0 until randomLength + 1) {
                pad.append("P")
            }
            return@Array "Title$it-$pad"
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.activity_test_tab_layout_with_viewpager2_item,
                            parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int = pageCount

        inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
            fun setData(position: Int) {
                itemView.findViewById<ImageView>(R.id.imageView)
                        .setImageResource(test[position % (test.size)])
            }
        }
    }
}