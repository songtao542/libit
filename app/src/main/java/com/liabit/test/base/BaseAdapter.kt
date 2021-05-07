package com.liabit.test.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseAdapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return onCreateViewHolder(LayoutInflater.from(parent.context), parent, viewType)
    }

    abstract fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup, viewType: Int): VH
}