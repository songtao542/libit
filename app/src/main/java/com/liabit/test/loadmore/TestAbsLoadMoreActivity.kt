package com.liabit.test.loadmore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.recyclerview.loadmore.AbstractLoadMoreAdapter
import com.liabit.test.R
import com.liabit.test.databinding.ActivityTestLoadMoreBinding
import com.liabit.test.mock.Mock
import com.liabit.viewbinding.inflate

class TestAbsLoadMoreActivity : AppCompatActivity() {

    private val binding by inflate<ActivityTestLoadMoreBinding>()

    private var mAdapter: Adapter = Adapter()

    private var mCount = 0

    private var mDataList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = mAdapter

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.root.postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
                mCount = 0
                mDataList.clear()
                val size = Mock.nextInt(4, 8)
                Log.d("TTTT", "swipeRefreshLayout ok size: $size")
                mDataList.addAll(MutableList(size) {
                    it
                })
                /*mDataList.addAll(MutableList(4) {
                    it
                })*/
                mAdapter.setData(mDataList)
                mAdapter.finishLoad()
            }, 2000)
        }

        mAdapter.setLoadMoreListener {
            binding.root.postDelayed({
                if (mCount < 10) {
                    if (mCount % 3 == 2) {
                        mCount++
                        it.failedLoad()
                    } else {
                        mCount++
                        val size = Mock.nextInt(5, 10)
                        Log.d("TTTT", "loadMore ok size: $size")
                        mDataList.addAll(MutableList(size) {
                            it
                        })
                        /*mDataList.addAll(MutableList(4) {
                            it
                        })*/
                        it.finishLoad()
                        mAdapter.setData(mDataList)
                    }
                } else {
                    it.isEnabled = false
                }
            }, 2 * 1000L)
        }

        mAdapter.setData(mDataList)

        binding.removeLast.setOnClickListener {
            if (mDataList.isNotEmpty()) {
                mDataList.removeLast()
                mAdapter.setData(mDataList)
            }
        }
        binding.removeLastTwo.setOnClickListener {
            if (mDataList.size >= 2) {
                mDataList.removeAt(mDataList.size - 1)
                mDataList.removeAt(mDataList.size - 1)
                mAdapter.setData(mDataList)
            }
        }
        binding.removeLastThree.setOnClickListener {
            if (mDataList.size >= 3) {
                mDataList.removeAt(mDataList.size - 1)
                mDataList.removeAt(mDataList.size - 1)
                mDataList.removeAt(mDataList.size - 1)
                mAdapter.setData1(mDataList)
                mAdapter.notifyItemRangeRemoved(mDataList.size, 3)
            }
        }
        binding.rangeInsert0.setOnClickListener {
            mDataList.add(0, 23)
            mAdapter.setData1(mDataList)
            mAdapter.notifyItemRangeInserted(0, 1)
        }

    }

    class Adapter : AbstractLoadMoreAdapter<Adapter.Holder>() {

        private var mData: List<Int> = emptyList()

        fun setData1(data: List<Int>) {
            mData = data
        }

        fun setData(data: List<Int>) {
            mData = data
            notifyDataSetChanged()
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {
            fun setData(position: Int, data: Int) {
                itemView.findViewById<TextView>(R.id.textView)?.text = "$position: $data"
            }
        }

        override fun onCreateHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.fragment_nestedrecyclerview_test_top_item,
                        parent, false
                    )
            )
        }

        override fun onBindHolder(holder: Holder, position: Int) {
            holder.setData(position, mData[position])
        }

        override fun getCount(): Int {
            return mData.size
        }

    }
}