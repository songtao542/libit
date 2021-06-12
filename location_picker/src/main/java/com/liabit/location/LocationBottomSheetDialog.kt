package com.liabit.location

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.liabit.location.databinding.MapLocationSheetFragmentBinding
import com.liabit.location.databinding.MapLocationSheetItemBinding
import com.liabit.location.model.PoiAddress
import com.liabit.viewbinding.bind

/**
 *
 */
class LocationBottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        private const val POI_SEARCH_RESULT_LIST = "psrl"
        private const val TITLE = "title"

        @JvmStatic
        fun newInstance(title: String, poiResultList: ArrayList<PoiAddress>) = LocationBottomSheetDialog().apply {
            this.arguments = Bundle().apply {
                putParcelableArrayList(POI_SEARCH_RESULT_LIST, poiResultList)
                putString(TITLE, title)
            }
        }
    }

    private var poiResult: List<PoiAddress>? = null
    private var title: String? = null

    private var onItemClickListener: OnItemClickListener? = null
    private var onConfirmClickListener: View.OnClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            poiResult = it.getParcelableArrayList(POI_SEARCH_RESULT_LIST)
            title = it.getString(TITLE, null)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.onItemClickListener = listener
    }

    fun setOnConfirmClickListener(listener: View.OnClickListener?) {
        this.onConfirmClickListener = listener
    }

    private val binding by bind<MapLocationSheetFragmentBinding> { requireView() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_location_sheet_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        binding.list.layoutManager = LinearLayoutManager(requireContext())
        poiResult?.let {
            binding.list.adapter = SheetAdapter(it, onItemClickListener)
        }
        binding.close.setOnClickListener {
            dismiss()
        }
        binding.confirm.setOnClickListener {
            onConfirmClickListener?.onClick(it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.2f)
        }
    }

    class SheetAdapter(
        private val poiResult: List<PoiAddress>,
        private var onItemClickListener: OnItemClickListener? = null
    ) : RecyclerView.Adapter<SheetAdapter.ViewHolder>() {

        private var lastSelected: PoiAddress? = null

        private fun setSelected(address: PoiAddress) {
            lastSelected?.selected = false
            address.selected = true
            lastSelected = address
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = poiResult.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.map_location_sheet_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(poiResult[position])
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
                    onItemClickListener?.onItemClick(view, adapterPosition, it)
                    return@let
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.list.adapter?.notifyDataSetChanged()
        setTitleInternal()
    }

    fun setSearchResult(result: List<PoiAddress>) {
        this.poiResult = result
        binding.list.adapter?.notifyDataSetChanged()
    }

    fun setTitle(title: String) {
        this.title = title
        setTitleInternal()
    }

    private fun setTitleInternal() {
        binding.titleView.text = if (title != null) getString(R.string.ml_search_result_templete, title) else getString(R.string.ml_search_result)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, address: PoiAddress)
    }
}
