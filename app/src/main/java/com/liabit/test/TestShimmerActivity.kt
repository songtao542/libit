package com.liabit.test


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.shimmer.ShimmerLayout


class TestShimmerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shimmer_test)

        val shimmerLayout = findViewById<ShimmerLayout>(R.id.shimmerLayout)
    }
}
