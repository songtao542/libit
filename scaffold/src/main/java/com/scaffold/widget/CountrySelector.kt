package com.scaffold.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import com.scaffold.R
import com.scaffold.extension.dip

class CountrySelector(
    private val anchor: View,
    private val listener: ((code: String, name: String) -> Unit)?,
    private val dismissListener: (() -> Unit)? = null
) {
    private val countryCodes: Array<String>
    private val countryNames: Array<String>
    private val context: Context = anchor.context
    private var popupWindow: PopupWindow? = null

    init {
        countryCodes = context.resources.getStringArray(R.array.country_codes)
        countryNames = context.resources.getStringArray(R.array.country_names)
    }

    fun show() {
        val inflater = LayoutInflater.from(context)

        @SuppressLint("InflateParams")
        val view = inflater.inflate(R.layout.widget_country_selector_popup_list, null)
        view.clipToOutline = true
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                view?.let {
                    val radius = context.resources.getDimension(R.dimen.country_popup_window_radius)
                    outline?.setRoundRect(0, 0, it.width, it.height, radius)
                }
            }
        }
        val listView = view.findViewById<ListView>(R.id.list)
        listView.adapter = CountryAdapter(inflater)
        popupWindow = PopupWindow(anchor.context).apply {
            contentView = view
            width = context.resources.getDimensionPixelSize(R.dimen.country_popup_window_width)
            height = context.resources.getDimensionPixelSize(R.dimen.country_popup_window_height)
            setBackgroundDrawable(null)
            isFocusable = true
            showAsDropDown(anchor, -anchor.width / 2 - 6.dip(context), 0 - view.height)
            setOnDismissListener {
                dismissListener?.invoke()
                popupWindow = null
            }
        }
    }

    fun dismiss() {
        if (popupWindow?.isShowing == true) {
            popupWindow?.dismiss()
        }
        popupWindow = null
    }

    /**
     * 自定义适配器类
     */
    private inner class CountryAdapter(private val inflater: LayoutInflater) : BaseAdapter() {
        override fun getCount(): Int {
            return countryCodes.size
        }

        override fun getItem(position: Int): String {
            return countryCodes[position] + " " + countryNames[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: inflater.inflate(R.layout.widget_country_selector_popup_item, parent, false)
            val tv = view.findViewById<View>(R.id.item_text) as TextView
            tv.text = getItem(position)
            view.tag = position.toString()
            view.setBackgroundResource(R.drawable.ic_country_selector_background_popup_middle)
            view.setOnClickListener { v ->
                val pos = v.tag?.toString()?.toIntOrNull() ?: return@setOnClickListener
                listener?.invoke(countryCodes[pos].trim { it <= ' ' }, countryNames[position])
                dismiss()
            }
            return view
        }
    }
}