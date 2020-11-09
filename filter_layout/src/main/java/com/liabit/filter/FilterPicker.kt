package com.liabit.filter;

import android.content.Context
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
interface FilterPicker {

    fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: OnDateSelectListener? = null)

    fun pickAddress(context: Context, address: PickerAddress?, listener: OnAddressSelectListener? = null)

    fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: OnNumberSelectListener? = null)

    interface OnDateSelectListener {
        fun onDateSelect(date: Date)
    }

    interface OnAddressSelectListener {
        fun onAddressSelect(address: PickerAddress)
    }

    interface OnNumberSelectListener {
        fun onNumberSelect(number: Int)
    }

    data class PickerAddress(
            val provinceCode: String,
            val provinceName: String,
            val cityCode: String,
            val cityName: String,
            val districtCode: String,
            val districtName: String) {

        val formatted: String
            get() {
                return if (provinceName == cityName) "$provinceName$districtName" else "$provinceName$cityName$districtName"
            }
    }

}
