package com.liabit.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.liabit.test.databinding.ActivityPickerTestBinding
import com.liabit.test.databinding.ActivityTestLoadMoreBinding
import com.liabit.viewbinding.inflate

class TestLoadMoreActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestLoadMoreBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}