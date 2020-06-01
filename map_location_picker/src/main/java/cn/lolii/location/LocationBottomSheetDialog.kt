package cn.lolii.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.lolii.location.model.PoiAddress
import cn.lolii.map_location_picker.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_location_sheet.*
import kotlinx.android.synthetic.main.fragment_location_sheet_item.view.*

/**
 *
 */
class LocationBottomSheetDialog : BottomSheetDialogFragment() {

    private var poiResult: List<PoiAddress>? = null
    private var title: String? = null

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            poiResult = it.getParcelableArrayList(Constants.Extra.LIST)
            title = it.getString(Constants.Extra.TITLE, null)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_location_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isCancelable = false
        list.layoutManager = LinearLayoutManager(requireContext())
        poiResult?.let {
            list.adapter = SheetAdapter(it, onItemClickListener)
        }
        close.setOnClickListener {
            dismiss()
        }
    }

    class SheetAdapter(private val poiResult: List<PoiAddress>,
                       private var onItemClickListener: OnItemClickListener? = null) : RecyclerView.Adapter<SheetAdapter.ViewHolder>() {

        private var currentChecked: PoiAddress? = null
        private var currentCheckedPosition: Int = -1

        override fun getItemCount(): Int = poiResult.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.fragment_location_sheet_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.setData(position, poiResult[position])
        }

        inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
            fun setData(position: Int, address: PoiAddress) {
                view.title.text = address.title
                view.address.text = address.formatAddress
                view.checked.isChecked = address == currentChecked
                view.setOnClickListener {
                    currentChecked = address
                    if (currentCheckedPosition != -1) {
                        notifyItemChanged(currentCheckedPosition)
                    }
                    currentCheckedPosition = position
                    view.checked.isChecked = true
                    onItemClickListener?.onItemClick(view, position, address)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        list.adapter?.notifyDataSetChanged()
        setTitleInternal()
    }

    fun setSearchResult(result: List<PoiAddress>) {
        this.poiResult = result
        list.adapter?.notifyDataSetChanged()
    }

    fun setTitle(title: String) {
        this.title = title
        setTitleInternal()
    }

    private fun setTitleInternal() {
        if (titleView != null) {
            titleView.text = if (title != null) getString(R.string.search_result_templete, title) else getString(R.string.search_result)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(arguments: Bundle? = null) = LocationBottomSheetDialog().apply {
            this.arguments = arguments
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int, address: PoiAddress)
    }
}
