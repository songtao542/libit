package com.liabit.test.nested

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.extension.dip
import com.liabit.extension.layoutUnderSystemUI
import com.liabit.test.databinding.ActivityNestedrecyclerviewTestBinding
import com.liabit.viewbinding.inflate

class TestNestedRecyclerViewActivity : AppCompatActivity() {

    private val binding by inflate<ActivityNestedrecyclerviewTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI()
        setContentView(binding.root)

        val topAdapter = TopAdapter()
        val pagerAdapter = TestPagerAdapter(supportFragmentManager)
        val fixedAdapter = TestFixedAdapter()

        binding.recyclerView.setFixedHeight(100.dip(this))

        binding.recyclerView.setAdapter(topAdapter, pagerAdapter, fixedAdapter)

        binding.swipeRefreshLayout.setColorSchemeColors(Color.RED)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }
    }


}
