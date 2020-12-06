package com.liabit.test.viewbinding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.liabit.test.R
import com.liabit.test.databinding.FragmentTestDataBindingBinding

class TestDataBindingFragment : BaseFragment<AnViewModel, FragmentTestDataBindingBinding>() {

    override fun getLayoutResource(): Int {
        return R.layout.fragment_test_data_binding
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = "Fragment: 这是通过 DataBinding 设置的文字"
        binding.imageView.setImageResource(R.mipmap.test3)

        binding.textView.setOnClickListener { viewModel.test() }
        binding.imageView.setOnClickListener { viewModel.test() }

        binding.testData = "Fragment: 这是通过 DataBinding 变量 设置的文字"

    }

}