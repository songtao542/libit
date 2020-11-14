package com.liabit.test.filtertest

import android.content.Context
import com.liabit.filter.IPicker
import com.liabit.picker.Picker
import com.liabit.picker.address.Address
import java.util.*

/**
 * Author:         songtao
 * CreateDate:     2020/11/9 10:56
 */
class FilterPicker : IPicker {

    companion object {

        @JvmStatic
        val instance: FilterPicker by lazy { FilterPicker() }

        @JvmStatic
        private fun filterAddressToPickerAddress(address: com.liabit.filter.Address?): Address? {
            if (address == null) return null
            return Address(address.provinceCode,
                    address.provinceName,
                    address.cityCode,
                    address.cityName,
                    address.districtCode,
                    address.districtName)
        }

        @JvmStatic
        private fun pickerAddressToFilterAddress(address: Address): com.liabit.filter.Address {
            return com.liabit.filter.Address(address.provinceCode,
                    address.provinceName,
                    address.cityCode,
                    address.cityName,
                    address.districtCode,
                    address.districtName)
        }
    }

    override fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: IPicker.OnDateSelectListener?) {
        Picker.pickDate(context, currentDate, startDate, endDate) { listener?.onDateSelect(it) }
    }

    override fun pickAddress(context: Context, address: com.liabit.filter.Address?, listener: IPicker.OnAddressSelectListener?) {
        Picker.pickAddress(context, filterAddressToPickerAddress(address)) { listener?.onAddressSelect(pickerAddressToFilterAddress(it)) }
    }

    override fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: IPicker.OnNumberSelectListener?) {
        Picker.pickNumber(context, number, from, to) { listener?.onNumberSelect(it) }
    }

}