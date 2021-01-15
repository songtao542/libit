package com.liabit.test.nested

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.test.R
import kotlin.random.Random

class TestFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_nestedrecyclerview_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = context ?: return
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewInPager)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TestAdapter()
    }

    class TestAdapter : RecyclerView.Adapter<TestAdapter.Holder>() {

        private val random = Random(System.currentTimeMillis())

        private var mData = MutableList(20/*random.nextInt(2, 50)*/) {
            return@MutableList it
        }

        override fun getItemCount(): Int = mData.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_nestedrecyclerview_test_top_item, parent, false))
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.setData(position)
        }

        class Holder(view: View) : RecyclerView.ViewHolder(view) {

            private val textView: TextView = view.findViewById(R.id.textView)

            fun setData(position: Int) {
                textView.text = "Test Item: $position"
            }
        }
    }

}

