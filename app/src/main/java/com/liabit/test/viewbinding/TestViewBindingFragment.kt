package com.liabit.test.viewbinding

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.test.R
import com.liabit.test.databinding.FragmentTestViewBindingBinding
import com.liabit.test.databinding.RecyclerviewTestViewBindingItemBinding
import com.liabit.viewbinding.bind
import kotlin.random.Random

class TestViewBindingFragment : BaseFragment<FragmentTestViewBindingBinding>() {

    override fun getLayoutResource(): Int {
        return R.layout.fragment_test_view_binding
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textView.text = "Fragment: 这是通过 ViewBinding 设置的文字"
        binding.imageView.setImageResource(R.mipmap.test4)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = TestAdapter()
    }

    class TestAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            val image: Int
                get() {
                    return when (Random.nextInt(5)) {
                        0 -> R.mipmap.test1
                        1 -> R.mipmap.test2
                        2 -> R.mipmap.test3
                        3 -> R.mipmap.test4
                        else -> R.mipmap.test5
                    }
                }
        }

        override fun getItemViewType(position: Int): Int {
            return if (position % 2 == 0) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                BHolder(RecyclerviewTestViewBindingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            } else {
                VHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_test_view_binding_item, parent, false))
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            if (holder is BHolder) {
                holder.setData(position)
            }
            if (holder is VHolder) {
                holder.setData(position)
            }
        }

        override fun getItemCount(): Int {
            return 10
        }

        class BHolder(private val binding: RecyclerviewTestViewBindingItemBinding) : RecyclerView.ViewHolder(binding.root) {
            @SuppressLint("SetTextI18n")
            fun setData(position: Int) {
                binding.textView.text = "$position BHolder: 这是通过 ViewBinding 设置的文字"
                binding.imageView.setImageResource(image)
            }
        }

        class VHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val binding by bind<RecyclerviewTestViewBindingItemBinding>()

            @SuppressLint("SetTextI18n")
            fun setData(position: Int) {
                binding.textView.text = "$position VHolder: 这是通过 ViewBinding 设置的文字"
                binding.imageView.setImageResource(image)
            }
        }
    }
}