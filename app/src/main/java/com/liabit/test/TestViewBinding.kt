package com.liabit.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.test.databinding.ActivityViewbindingTestBinding
import com.liabit.test.databinding.FragmentViewbindingTestBinding
import com.liabit.test.databinding.RecyclerviewItemViewbindingTestBinding
import com.liabit.viewbinding.bind
import com.liabit.viewbinding.inflate
import kotlin.random.Random

/**
 * Author:         songtao
 * CreateDate:     2020/12/1 15:23
 */
class TestViewBinding : AppCompatActivity() {

    private val binding by inflate<ActivityViewbindingTestBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.textView.text = "Activity 这是通过ViewBinding设置的文字"
        binding.imageView.setImageResource(R.mipmap.test2)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, TestFragment())
                .commitAllowingStateLoss()
    }


    class TestFragment : Fragment() {

        private val binding by bind<FragmentViewbindingTestBinding>()

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            return inflater.inflate(R.layout.fragment_viewbinding_test, container, false)
            //return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.textView.text = "Fragment 这是通过ViewBinding设置的文字"
            binding.imageView.setImageResource(R.mipmap.test4)

            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            binding.recyclerView.adapter = TestAdapter()
        }

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

        //private val binding by viewBinding<RecyclerviewItemViewbindingTestBinding>()

        override fun getItemViewType(position: Int): Int {
            return if (position % 2 == 0) 0 else 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return if (viewType == 0) {
                BHolder(RecyclerviewItemViewbindingTestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            } else {
                VHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item_viewbinding_test, parent, false))
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

        class BHolder(private val binding: RecyclerviewItemViewbindingTestBinding) : RecyclerView.ViewHolder(binding.root) {
            fun setData(position: Int) {
                binding.textView.text = "$position BHolder 这是通过ViewBinding设置的文字"
                binding.imageView.setImageResource(image)
            }
        }

        class VHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val binding by bind<RecyclerviewItemViewbindingTestBinding>()

            fun setData(position: Int) {
                binding.textView.text = "$position VHolder 这是通过ViewBinding设置的文字"
                binding.imageView.setImageResource(image)
            }
        }
    }

}