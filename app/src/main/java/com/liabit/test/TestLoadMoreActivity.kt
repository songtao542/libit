package com.liabit.test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.autoclear.autoClearValue
import com.liabit.recyclerview.loadmore.LoadMoreAdapter
import com.liabit.test.databinding.ActivityTestLoadMoreBinding
import com.liabit.test.mock.Mock
import com.liabit.viewbinding.inflate

class TestLoadMoreActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestLoadMoreBinding>()

    private var mAdapter: Adapter = Adapter()
    private var mLoadMoreAdapter: LoadMoreAdapter<*>? = null

    private var mLoadMoreAdapter1 by autoClearValue<LoadMoreAdapter<*>>()

    private var mCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Log.d("TTTT", "mLoadMoreAdapter1==$mLoadMoreAdapter1")
        mLoadMoreAdapter1 = LoadMoreAdapter.wrap(mAdapter)
        Log.d("TTTT", "mLoadMoreAdapter2==$mLoadMoreAdapter1")

        mLoadMoreAdapter = LoadMoreAdapter.wrap(mAdapter).apply {
            setStyle(this@TestLoadMoreActivity, R.style.TestLoadMoreStyle)
            setShowNoMoreEnabled(true)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mLoadMoreAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
                mCount = 0
                mLoadMoreAdapter?.isLoadMoreEnabled = true
                mAdapter.setData(MutableList(Mock.nextInt(5, 10)) {
                    it
                })
            }, 2000)
        }

        mLoadMoreAdapter?.setLoadMoreListener {
            binding.root.postDelayed({
                if (mCount < 10) {
                    if (mCount % 3 == 2) {
                        mCount++
                        it.isLoadFailed = true
                    } else {
                        mCount++
                        mAdapter.addData(MutableList(Mock.nextInt(5, 10)) {
                            it
                        })
                    }
                } else {
                    it.isEnable = false
                }
            }, Mock.nextInt(2, 5) * 1000L)
        }

        mAdapter.setData(MutableList(Mock.nextInt(5, 10)) {
            it
        })

    }

    class Adapter : RecyclerView.Adapter<Adapter.Holder>() {

        private var mData = ArrayList<Int>()

        fun setData(data: List<Int>) {
            mData.clear()
            mData.addAll(data)
            notifyDataSetChanged()
        }

        fun addData(data: List<Int>) {
            mData.addAll(data)
            notifyDataSetChanged()
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            fun setData(position: Int) {
                itemView.findViewById<TextView>(R.id.textView)?.text = "$position"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.fragment_nestedrecyclerview_test_top_item,
                        parent, false
                    )
            )
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setData(position)
        }

        override fun getItemCount(): Int {
            return mData.size
        }

    }
}