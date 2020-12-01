package com.liabit.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.liabit.location.databinding.MapLocationSheetAddressItemBinding
import com.liabit.location.databinding.MapLocationSheetItemBinding
import com.liabit.location.model.AddressType
import com.liabit.location.model.PoiAddress

class LocationPickerRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClickListener: ((item: PoiAddress) -> Unit)? = null

    var data: List<PoiAddress>? = null
        set(value) {
            value?.let {
                field = it
                checkAndSetSelected()
                notifyDataSetChanged()
            }
        }

    fun setOnItemClickListener(onItemClickListener: ((item: PoiAddress) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

    private var lastSelected: PoiAddress? = null

    fun getSelected(): PoiAddress? {
        return lastSelected
    }

    /**
     * 检查是否存在选中项，如果没有，则选中第一项
     */
    private fun checkAndSetSelected() {
        data?.let { items ->
            if (items.isNotEmpty()) {
                var hasSelected = false
                for (item in items) {
                    if (item.selected) {
                        hasSelected = true
                        break
                    }
                }
                if (!hasSelected) {
                    lastSelected = items[0]
                    lastSelected!!.selected = true
                }
            }
        }
    }

    private fun setSelected(address: PoiAddress) {
        lastSelected?.selected = false
        address.selected = true
        lastSelected = address
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return data!![position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (AddressType.from(viewType)) {
            AddressType.ADDRESS -> AddressViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.map_location_sheet_address_item, parent, false))
            AddressType.POI_ADDRESS -> ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.map_location_sheet_item, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AddressViewHolder) {
            holder.setData(data!![position])
        } else if (holder is ViewHolder) {
            holder.setData(data!![position])
        }
    }

    inner class AddressViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

        private val binding = MapLocationSheetAddressItemBinding.bind(view)
        private var address: PoiAddress? = null

        fun setData(address: PoiAddress) {
            this.address = address
            binding.selectAddress.text = address.address
            binding.checkedAddress.visibility = if (address.selected) View.VISIBLE else View.INVISIBLE
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            address?.let {
                setSelected(it)
                onItemClickListener?.invoke(it)
                return@let
            }
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
        private val binding = MapLocationSheetItemBinding.bind(view)
        private var address: PoiAddress? = null

        fun setData(address: PoiAddress) {
            this.address = address
            binding.title.text = address.title
            binding.address.text = address.formatAddress
            binding.checked.visibility = if (address.selected) View.VISIBLE else View.INVISIBLE
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            address?.let {
                setSelected(it)
                onItemClickListener?.invoke(it)
                return@let
            }
        }
    }
}