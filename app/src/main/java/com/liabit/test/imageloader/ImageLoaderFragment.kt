package com.liabit.test.imageloader

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.imageloader.load
import com.liabit.test.R
import com.liabit.test.databinding.TestImageLoaderBinding
import com.liabit.test.databinding.TestImageLoaderItemBinding
import com.liabit.test.mock.MockPicture
import com.liabit.viewbinding.inflate

class ImageLoaderFragment : Fragment() {

    val binding by inflate<TestImageLoaderBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = context ?: return

        binding.image1.load(MockPicture.random(), R.mipmap.wan_dan)
        binding.image2.load(MockPicture.random(), R.mipmap.wan_dan)
        binding.image3.load(MockPicture.random(), R.mipmap.wan_dan)

        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)

        binding.recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {

            override fun getItemCount(): Int {
                return 50
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.test_image_loader_item, parent, false))
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.setData(position)
            }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = TestImageLoaderItemBinding.bind(itemView)

        fun setData(position: Int) {
            binding.image.load(MockPicture[position], R.mipmap.wan_dan)
        }
    }

}