package com.liabit.filter;

import android.content.Context
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/9/18 18:31
 */
interface IPicker {

    fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: OnDateSelectListener? = null)

    fun pickAddress(context: Context, address: Address?, listener: OnAddressSelectListener? = null)

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
}
