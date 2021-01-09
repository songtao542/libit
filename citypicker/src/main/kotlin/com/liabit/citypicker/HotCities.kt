package com.liabit.citypicker

import com.liabit.listpicker.model.HotItem

class HotCities(private val section: String, private val cities: List<City>) : HotItem<City> {

    private var mChecked: Boolean = false

    override fun getHotItems(): List<City> {
        return cities
    }

    override fun getItemTitle(): String {
        return ""
    }

    override fun getItemSubtitle(): String {
        return ""
    }

    override fun setItemChecked(checked: Boolean) {
        mChecked = checked
    }

    override fun isItemChecked(): Boolean {
        return mChecked
    }

    override fun getItemSection(): String {
        return section
    }
}