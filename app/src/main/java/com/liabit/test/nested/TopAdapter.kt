package com.liabit.test.nested

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liabit.test.R

class TopAdapter : RecyclerView.Adapter<TopAdapter.Holder>() {

    private var mData = MutableList(10) {
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
            textView.text = "Top Item: $position"
        }
    }
}