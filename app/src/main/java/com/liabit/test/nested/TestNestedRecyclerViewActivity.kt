package com.liabit.test.nested

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.liabit.extension.dip
import com.liabit.extension.dp
import com.liabit.extension.layoutUnderSystemUI
import com.liabit.test.databinding.ActivityNestedrecyclerviewTestBinding
import com.liabit.viewbinding.inflate
import kotlin.math.min

class TestNestedRecyclerViewActivity : AppCompatActivity() {

    private val binding by inflate<ActivityNestedrecyclerviewTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutUnderSystemUI()
        setContentView(binding.root)

        val topAdapter = TopAdapter()
        val pagerAdapter = TestPagerAdapter(supportFragmentManager)
        val fixedAdapter = TestFixedAdapter()

        binding.recyclerView.setFixedHeight(80.dp(this))
        binding.recyclerView.setOnScrollChangeListener { _, scrollY ->
            binding.searchView.setBackgroundColor(Color.argb(min((scrollY * 0.2f).toInt(), 255), 0x60, 0x7D, 0x8B))
        }
        binding.recyclerView.setAdapter(topAdapter, pagerAdapter, fixedAdapter)


        binding.swipeRefreshLayout.setColorSchemeColors(Color.RED)
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
            }, 2000)
        }
    }


}
