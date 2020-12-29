package com.liabit.test


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.liabit.test.databinding.ActivityShimmerTestBinding
import com.liabit.viewbinding.inflate


class TestShimmerActivity : AppCompatActivity() {

    private val binding by inflate<ActivityShimmerTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //binding.shimmerLayout
    }
}
