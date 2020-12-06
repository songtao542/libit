package com.liabit.test.viewbinding

import android.annotation.SuppressLint
import android.os.Bundle
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestViewBindingBinding

/**
 * Author:         songtao
 * CreateDate:     2020/12/1 15:23
 */
class TestViewBindingActivity : BaseActivity<AnViewModel, ActivityTestViewBindingBinding>() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.textView.text = "Activity: 这是通过 ViewBinding 设置的文字"
        binding.imageView.setImageResource(R.mipmap.test2)

        binding.textView.setOnClickListener { viewModel.test() }
        binding.imageView.setOnClickListener { viewModel.test() }

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TestViewBindingFragment())
                .commitAllowingStateLoss()

    }

}