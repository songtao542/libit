package com.liabit.integratepicker

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.liabit.citypicker.City
import com.liabit.citypicker.CityPickerFragment
import com.liabit.citypicker.OnRequestLocationListener
import com.liabit.extension.toStringArray
import com.liabit.listpicker.IPicker
import com.liabit.listpicker.OnResultListener
import com.liabit.numberpicker.PickerFragment
import com.liabit.picker.address.Address
import java.util.*

@Suppress("unused")
object Picker {

    @JvmStatic
    fun pick(
        activity: FragmentActivity?,
        title: String,
        value: Int,
        minValue: Int,
        maxValue: Int,
        handler: ((value: Int, _: Int) -> Unit),
    ) {
        activity?.supportFragmentManager?.let {
            PickerFragment.Builder()
                .setTitle(title)
                .setColumn(minValue, maxValue)
                .setOnIndexChangeListener(handler)
                .setValue(value1 = value)
                .show(it)
        }
    }

    /**
     * 第二列取第一列的子集作为自己的数据
     * @param column2SubOfColumn1Type 0:取前半部分; 大于 0: 取后半部分
     */
    @JvmStatic
    fun <T> pick(
        activity: FragmentActivity?,
        title: String,
        value1: Int,
        column1: Array<T>,
        column2SubOfColumn1Type: Int,
        handler: ((value1: Int, value2: Int) -> Unit),
    ) {
        activity?.supportFragmentManager?.let {
            PickerFragment.Builder()
                .setTitle(title)
                .setColumn(column1Values = column1.toStringArray())
                .setOnIndexChangeListener(handler)
                .setColumn2SubOfColumn1(column2SubOfColumn1Type)
                .setValue(value1 = value1)
                .show(it)
        }
    }

    @JvmStatic
    fun <T> pick(
        activity: FragmentActivity?,
        title: String,
        value: Int,
        column: Array<T>,
        handler: ((value: Int) -> Unit),
    ) {
        activity?.supportFragmentManager?.let {
            PickerFragment.Builder()
                .setTitle(title)
                .setColumn(column1Values = column.toStringArray())
                .setOnIndexChangeListener { v1, _ ->
                    handler.invoke(v1)
                }
                .setValue(value1 = value)
                .show(it)
        }
    }

    @JvmStatic
    fun <T1, T2> pick(
        activity: FragmentActivity?,
        title: String,
        value1: Int,
        column1: Array<T1>,
        value2: Int,
        column2: Array<T2>,
        handler: ((value1: Int, value2: Int) -> Unit),
    ) {
        activity?.supportFragmentManager?.let {
            PickerFragment.Builder()
                .setTitle(title)
                .setColumn(column1Values = column1.toStringArray(), column2Values = column2.toStringArray())
                .setOnIndexChangeListener(handler)
                .setValue(value1 = value1, value2 = value2)
                .show(it)
        }
    }

    @JvmStatic
    fun pick(
        activity: FragmentActivity?,
        title: String,
        value: Int,
        provider: ((picker: PickerFragment) -> Unit),
        handler: ((value: Int) -> Unit)? = null,
        valueHandler: ((value: CharSequence) -> Unit)? = null,
    ) {
        pick(activity, title, value, 0, provider, { v1, _ ->
            handler?.invoke(v1)
        }) { v1, _ ->
            valueHandler?.invoke(v1)
        }
    }

    @JvmStatic
    fun pick(
        activity: FragmentActivity?,
        title: String,
        value1: Int,
        value2: Int = 0,
        provider: ((picker: PickerFragment) -> Unit),
        handler: ((value1: Int, value2: Int) -> Unit)? = null,
        valueHandler: ((value1: CharSequence, value2: CharSequence) -> Unit)? = null,
    ) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            val builder = PickerFragment.Builder()
                .setTitle(title)
                .setValue(value1 = value1, value2 = value2)
            handler?.let { builder.setOnIndexChangeListener(it) }
            valueHandler?.let { builder.setOnValueListener(it) }
            val pickerFragment = builder.show(fragmentManager)
            provider.invoke(pickerFragment)
        }
    }

    @JvmStatic
    fun pickCity(
        activity: FragmentActivity?,
        multipleMode: Boolean,
        handler: ((cities: List<City>) -> Unit),
    ) {
        if (activity == null) return
        CityPickerFragment.Builder()
            .setFragmentManager(activity.supportFragmentManager)
            .setMultipleMode(multipleMode)
            .setDefaultHotCitiesEnabled(true)
            .setLocatedCityEnable(true)
            .setUseDefaultCities(true)
            .setOnResultListener(object : OnResultListener<City> {
                override fun onResult(data: List<City>) {
                    handler.invoke(data)
                }
            })
            .show()
    }

    @JvmStatic
    fun pickCity(
        activity: FragmentActivity?,
        provider: ((picker: IPicker<City>) -> Unit),
        handler: ((cities: List<City>) -> Unit),
    ) {
        pickCity(activity, true, provider, handler)
    }

    @JvmStatic
    fun pickCity(
        activity: FragmentActivity?,
        multipleMode: Boolean,
        provider: ((picker: IPicker<City>) -> Unit),
        handler: ((cities: List<City>) -> Unit),
    ) {
        if (activity == null) return
        CityPickerFragment.Builder()
            .setFragmentManager(activity.supportFragmentManager)
            .setMultipleMode(multipleMode)
            .setDefaultHotCitiesEnabled(false)
            .setLocatedCityEnable(false)
            .setUseDefaultCities(false)
            .setOnRequestLocationListener(object : OnRequestLocationListener {
                override fun requestLocation(picker: IPicker<City>) {
                    provider.invoke(picker)
                }
            })
            .setOnResultListener(object : OnResultListener<City> {
                override fun onResult(data: List<City>) {
                    handler.invoke(data)
                }
            })
            .show()
    }

    const val REQUEST_CODE_PICK_PHOTO = com.liabit.photopicker.Picker.REQUEST_CODE_PICK_PHOTO

    @JvmStatic
    fun pickPhoto(
        fragment: Fragment? = null,
        max: Int = 1,
        crop: Boolean = false,
        requestCode: Int = REQUEST_CODE_PICK_PHOTO,
    ) {
        com.liabit.photopicker.Picker.pickPhoto(fragment, max, crop, requestCode)
    }

    @JvmStatic
    fun pickPhoto(
        activity: Activity? = null,
        max: Int = 1,
        crop: Boolean = false,
        requestCode: Int = REQUEST_CODE_PICK_PHOTO,
    ) {
        com.liabit.photopicker.Picker.pickPhoto(activity, max, crop, requestCode)
    }

    @JvmStatic
    fun pickDate(
        activity: Activity? = null,
        handler: ((year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit),
    ) {
        if (activity == null) {
            return
        }
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            activity,
            { _, year, monthOfYear, dayOfMonth ->
                handler.invoke(year, monthOfYear, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    @JvmStatic
    fun pickTime(context: Context, listener: ((hourOfDay: Int, minute: Int) -> Unit)? = null) {
        com.liabit.picker.Picker.pickTime(context, Date(), null, null, listener)
    }

    @JvmStatic
    fun pickTime(context: Context, currentTime: Date, startTime: Date?, endTime: Date?, listener: ((hourOfDay: Int, minute: Int) -> Unit)? = null) {
        com.liabit.picker.Picker.pickTime(context, currentTime, startTime, endTime, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, listener: ((date: Date) -> Unit)? = null) {
        com.liabit.picker.Picker.pickDate(context, 0, 20, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, yearOffset: Int, listener: ((date: Date) -> Unit)? = null) {
        com.liabit.picker.Picker.pickDate(context, yearOffset, yearOffset, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, forwardYearOffset: Int, backwardYearOffset: Int, listener: ((date: Date) -> Unit)? = null) {
        com.liabit.picker.Picker.pickDate(context, forwardYearOffset, backwardYearOffset, listener)
    }

    @JvmStatic
    fun pickDate(context: Context, currentDate: Date, startDate: Date, endDate: Date, listener: ((date: Date) -> Unit)? = null) {
        com.liabit.picker.Picker.pickDate(context, currentDate, startDate, endDate, listener)
    }

    @JvmStatic
    fun pickAddress(context: Context, address: Address?, listener: ((address: Address) -> Unit)? = null) {
        com.liabit.picker.Picker.pickAddress(context, address, listener)
    }

    @JvmStatic
    fun pickNumber(context: Context, number: Int?, from: Int, to: Int, listener: ((number: Int) -> Unit)? = null) {
        com.liabit.picker.Picker.pickNumber(context, number, from, to, listener)
    }


}
