package com.liabit.test.imageloader

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.base.startActivity
import com.liabit.imageloader.load
import com.liabit.test.R
import com.liabit.test.databinding.TestImageLoaderBinding
import com.liabit.test.databinding.TestImageLoaderItemBinding
import com.liabit.test.mock.MockPicture
import com.liabit.viewbinding.inflate

class ImageLoaderAppActivity : AppCompatActivity() {

    val binding by inflate<TestImageLoaderBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.image1.load(MockPicture.random(), R.mipmap.wan_dan)
        binding.image2.load(MockPicture.random(), R.mipmap.wan_dan)
        binding.image3.load(MockPicture.random(), R.mipmap.wan_dan)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        binding.recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {

            override fun getItemCount(): Int {
                return 360
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                return ViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.test_image_loader_item, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.setData(position)
            }
        }

        binding.image1.setOnClickListener { clear() }
        binding.image2.setOnClickListener { clear() }
        binding.image3.setOnClickListener { clear() }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = TestImageLoaderItemBinding.bind(itemView)

        fun setData(position: Int) {
            binding.image.load(MockPicture[position], R.mipmap.wan_dan)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.test_image_loader_clear, menu)
        return true
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                clear()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        val files = externalCacheDir?.listFiles()
        files?.let {
            for (file in it) {
                Log.d("TTTT", "file: " + file.absolutePath)
                if (file.isFile) {
                    file.delete()
                }
            }
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }
}