package com.liabit.test.viewbinding

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestDataBindingBinding

class TestDataBindingActivity : BaseActivity<ActivityTestDataBindingBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("TTTT", "TestDataBindingActivity binding: $binding")

        binding.textView.text = "Activity: 这是通过 DataBinding 设置的文字"
        binding.testData = "Activity: 这是通过 DataBinding 变量 设置的文字"
        binding.imageView.setImageResource(R.mipmap.test5)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TestDataBindingFragment())
                .commitAllowingStateLoss()
    }

}