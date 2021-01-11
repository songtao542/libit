package com.liabit.listpicker

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.liabit.citypicker.R
import com.liabit.listpicker.model.Item

class HotItemListAdapter<I : Item>(val context: Context, val data: List<I>) : RecyclerView.Adapter<HotItemListAdapter.GridViewHolder>() {

    companion object {
        const val SPAN_COUNT = 3
    }

    private var mInnerListener: InnerListener<I>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        return GridViewHolder(LayoutInflater.from(context).inflate(R.layout.cp_grid_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val item = data[position]
        val screenWidth: Int = context.resources.displayMetrics.widthPixels
        val typedValue = TypedValue()
        context.theme.resolveAttribute(R.attr.cpGridItemSpace, typedValue, true)
        val space = context.resources.getDimension(typedValue.resourceId)
        val padding = context.resources.getDimension(R.dimen.cp_default_padding)
        val indexBarWidth = context.resources.getDimension(R.dimen.cp_index_bar_width)
        val itemWidth = (screenWidth - padding - space * (SPAN_COUNT - 1) - indexBarWidth) / SPAN_COUNT
        val lp = holder.container.layoutParams
        lp.width = itemWidth.toInt()
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        holder.container.layoutParams = lp
        holder.name.isSelected = item.isItemChecked()
        holder.name.text = item.getItemTitle()
        holder.container.setOnClickListener { mInnerListener?.onItemClick(item) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class GridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var container: FrameLayout = itemView.findViewById(R.id.cp_grid_item_layout)
        var name: TextView = itemView.findViewById(R.id.cp_gird_item_name)
    }

    fun setInnerListener(listener: InnerListener<I>?) {
        mInnerListener = listener
    }

}
