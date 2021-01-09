package com.liabit.listpicker

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.liabit.citypicker.R
import com.liabit.listpicker.decoration.GridItemDecoration
import com.liabit.listpicker.model.HotItem
import com.liabit.listpicker.model.Item
import com.liabit.listpicker.model.VariableState

class ItemListAdapter<I : Item>(val context: Context, val multiple: Boolean) : RecyclerView.Adapter<ItemListAdapter.BaseViewHolder>() {

    private var mVariableState = VariableState.LOCATING
    private var mVariableItem: I? = null
    private var mHotItem: HotItem<I>? = null
    private var mItems: List<I>? = null
    private var mInnerListener: InnerListener<I>? = null
    private var mLayoutManager: LinearLayoutManager? = null
    private var mStateChanged = false

    fun setItem(variableItem: I?, hotItem: HotItem<I>?, items: List<I>?) {
        mVariableItem = variableItem
        mHotItem = hotItem
        mItems = items
        notifyDataSetChanged()
    }

    val selected: List<I>
        get() {
            val list = ArrayList<I>()
            mVariableItem?.let {
                if (it.isItemChecked()) {
                    list.add(it)
                }
            }
            mHotItem?.let {
                for (item in it.getHotItems()) {
                    if (item.isItemChecked()) {
                        list.add(item)
                    }
                }
            }
            mItems?.let {
                for (item in it) {
                    if (item.isItemChecked()) {
                        list.add(item)
                    }
                }
            }
            return list
        }

    fun setLayoutManager(manager: LinearLayoutManager?) {
        mLayoutManager = manager
    }

    fun updateVariableState(variable: I?, state: Int) {
        mVariableItem = variable
        mStateChanged = mVariableState != state
        mVariableState = state
        refresh()
    }

    fun refresh() {
        //如果定位城市的item可见则进行刷新
        if (mStateChanged && 0 == mLayoutManager?.findFirstVisibleItemPosition()) {
            mStateChanged = false
            notifyItemChanged(0)
        }
    }

    override fun getItemCount(): Int {
        return (if (mVariableItem == null) 0 else 1) + (if (mHotItem == null) 0 else 1) + (mItems?.size ?: 0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (mVariableItem == null) {
            if (mHotItem != null && position == 0) 1 else 2
        } else {
            if (position == 0) 0 else if (mHotItem != null && position == 1) 1 else 2
        }
    }

    /**
     * 滚动RecyclerView到索引位置
     *
     * @param index
     */
    fun scrollToSection(index: String) {
        Log.d("TTTT", "index======>$index")
        val layoutManager = mLayoutManager ?: return
        val variableItem = mVariableItem
        var offset = 0
        if (variableItem != null) {
            if (TextUtils.equals(index.substring(0, 1), variableItem.getItemSection().substring(0, 1))) {
                layoutManager.scrollToPositionWithOffset(0, 0)
                return
            }
            offset += 1
        }
        val hotItem = mHotItem
        if (hotItem != null) {
            if (TextUtils.equals(index.substring(0, 1), hotItem.getItemSection().substring(0, 1))) {
                layoutManager.scrollToPositionWithOffset(offset, 0)
                return
            }
            offset += 1
        }
        val items = mItems
        if (items != null) {
            for (i in items.indices) {
                if (TextUtils.equals(index.substring(0, 1), items[i].getItemSection().substring(0, 1))) {
                    layoutManager.scrollToPositionWithOffset(offset + i, 0)
                    return
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            0 -> {
                VariableViewHolder(LayoutInflater.from(context).inflate(R.layout.cp_list_item_location_layout, parent, false))
            }
            1 -> {
                HotViewHolder(LayoutInflater.from(context).inflate(R.layout.cp_list_item_hot_layout, parent, false))
            }
            else -> {
                ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.cp_list_item_default_layout, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ItemViewHolder) {
            val items = mItems ?: return
            val realPosition = position - (if (mVariableItem == null) 0 else 1) - (if (mHotItem == null) 0 else 1)
            val item = items[realPosition]
            holder.setSelected(item.isItemChecked())
            holder.title.text = item.getItemTitle()
            holder.checked.visibility = if (multiple) View.VISIBLE else View.GONE
            holder.title.setOnClickListener {
                if (multiple) {
                    item.setItemChecked(!item.isItemChecked())
                    notifyDataSetChanged()
                } else {
                    mInnerListener?.onItemClick(item)
                }
            }
        }
        //定位城市
        if (holder is VariableViewHolder) {
            val variableItem = mVariableItem ?: return
            val screenWidth = context.resources.displayMetrics.widthPixels
            val typedValue = TypedValue()
            context.theme.resolveAttribute(R.attr.cpGridItemSpace, typedValue, true)
            val space = context.resources.getDimensionPixelSize(R.dimen.cp_grid_item_space)
            val padding = context.resources.getDimensionPixelSize(R.dimen.cp_default_padding)
            val indexBarWidth = context.resources.getDimensionPixelSize(R.dimen.cp_index_bar_width)
            val itemWidth = (screenWidth - padding - space * (HotItemListAdapter.SPAN_COUNT - 1) - indexBarWidth) / HotItemListAdapter.SPAN_COUNT
            val lp = holder.container.layoutParams
            lp.width = itemWidth
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            holder.container.layoutParams = lp
            when (mVariableState) {
                VariableState.LOCATING -> holder.current.setText(R.string.cp_locating)
                VariableState.SUCCESS -> {
                    holder.current.isSelected = variableItem.isItemChecked()
                    holder.current.text = variableItem.getItemTitle()
                }
                VariableState.FAILURE -> holder.current.setText(R.string.cp_locate_failed)
            }
            holder.container.setOnClickListener {
                if (mVariableState == VariableState.SUCCESS) {
                    if (multiple) {
                        variableItem.setItemChecked(!variableItem.isItemChecked())
                        notifyDataSetChanged()
                    } else {
                        mInnerListener?.onItemClick(variableItem)
                    }
                } else if (mVariableState == VariableState.FAILURE) {
                    mVariableState = VariableState.LOCATING
                    notifyItemChanged(0)
                    mInnerListener?.requestVariable()
                }
            }
        }
        //热门城市
        if (holder is HotViewHolder) {
            val hotItems = mHotItem?.getHotItems() ?: return
            holder.mRecyclerView.adapter = HotItemListAdapter(context, hotItems).apply {
                setInnerListener(object : InnerListener<I> {
                    override fun onItemClick(item: I?) {
                        if (multiple) {
                            if (item != null) {
                                item.setItemChecked(!item.isItemChecked())
                                notifyDataSetChanged()
                            }
                        } else {
                            mInnerListener?.onItemClick(item)
                        }
                    }

                    override fun requestVariable() {
                        mInnerListener?.requestVariable()
                    }
                })
            }
        }
    }

    fun setInnerListener(listener: InnerListener<I>?) {
        mInnerListener = listener
    }

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ItemViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.cp_list_item_name)
        var checked: ImageView = itemView.findViewById(R.id.cp_selected)

        fun setSelected(select: Boolean) {
            if (select) {
                checked.setImageResource(R.drawable.cp_selected)
                checked.isSelected = true
            } else {
                checked.setImageResource(R.drawable.cp_unselected)
                checked.isSelected = false
            }
        }
    }

    class HotViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var mRecyclerView: RecyclerView = itemView.findViewById(R.id.cp_hot_list)

        init {
            mRecyclerView.setHasFixedSize(true)
            mRecyclerView.layoutManager = GridLayoutManager(itemView.context, HotItemListAdapter.SPAN_COUNT, RecyclerView.VERTICAL, false)
            val space: Int = itemView.context.resources.getDimensionPixelSize(R.dimen.cp_grid_item_space)
            mRecyclerView.addItemDecoration(GridItemDecoration(HotItemListAdapter.SPAN_COUNT, space))
        }
    }

    class VariableViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var container: FrameLayout = itemView.findViewById(R.id.cp_list_item_location_layout)
        var current: TextView = itemView.findViewById(R.id.cp_list_item_location)
    }

}