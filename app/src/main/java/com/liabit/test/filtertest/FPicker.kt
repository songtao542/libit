package com.liabit.test.filtertest

import android.content.Context
import com.liabit.filter.FilterPicker
import com.liabit.picker.Picker
import com.liabit.picker.address.Address
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/11/9 10:56
 */
class FPicker : FilterPicker {

    companion object {

        @JvmStatic
        val instance: FPicker by lazy { FPicker() }

        @JvmStatic
        private fun filterAddressToPickerAddress(address: FilterPicker.Address?): Address? {
            if (address == null) return null
            return Address(address.provinceCode,
                    address.provinceName,
                    address.cityCode,
                    address.cityName,
                    address.districtCode,
                    address.districtName)
        }

        @JvmStatic
        private fun pickerAddressToFilterAddress(address: Address): FilterPicker.Address {
            return FilterPicker.Address(address.provinceCode,
                    address.provinceName,
                    address.cityCode,
                    address.cityName,
                    address.districtCode,
                    address.districtName)
        }
    }

    override fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: FilterPicker.OnDateSelectListener?) {
        Picker.pickDate(context, currentDate, startDate, endDate) { listener?.onDateSelect(it) }
    }

    override fun pickAddress(context: Context, address: FilterPicker.Address?, listener: FilterPicker.OnAddressSelectListener?) {
        Picker.pickAddress(context, filterAddressToPickerAddress(address)) { listener?.onAddressSelect(pickerAddressToFilterAddress(it)) }
    }

    override fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: FilterPicker.OnNumberSelectListener?) {
        Picker.pickNumber(context, number, from, to) { listener?.onNumberSelect(it) }
    }

}