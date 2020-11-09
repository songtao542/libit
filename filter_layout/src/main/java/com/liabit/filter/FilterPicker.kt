package com.liabit.filter;

import android.content.Context
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
interface FilterPicker {

    fun pickDate(context: Context, listener: OnDateSelectListener? = null)

    fun pickDate(context: Context, yearOffset: Int, listener: OnDateSelectListener? = null)

    fun pickDate(context: Context, forwardYearOffset: Int, backwardYearOffset: Int, listener: OnDateSelectListener? = null)

    fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: OnDateSelectListener? = null)

    fun pickAddress(context: Context, address: Any?, listener: OnAddressSelectListener? = null)

    fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: OnNumberSelectListener? = null)

    interface OnDateSelectListener {
        fun onDateSelect(date: Date)
    }

    interface OnAddressSelectListener {
        fun onAddressSelect(address: Address)
    }

    interface OnNumberSelectListener {
        fun onNumberSelect(number: Int)
    }

    data class Address(val provinceCode: String,
                       val provinceName: String,
                       val cityCode: String,
                       val cityName: String,
                       val districtCode: String,
                       val districtName: String) {
        val formattedAddress: String
            get() {
                return if (provinceName == cityName) {
                    "$provinceName$districtName"
                } else {
                    "$provinceName$cityName$districtName"
                }
            }
    }

}
