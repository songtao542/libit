package com.liabit.integratepicker

import android.app.DatePickerDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.liabit.citypicker.CityPickerFragment
import com.liabit.citypicker.adapter.CityPicker
import com.liabit.citypicker.model.City
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import com.zhihu.matisse.filter.GifSizeFilter
import java.util.*

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
            val pickerFragment = PickerFragment.newInstance(title, minValue, maxValue)
            pickerFragment.setOnResultListener(handler)
            pickerFragment.value1 = value
            pickerFragment.show(it, "picker-mm")
        }
    }

    /**
     * 第二列取第一列的子集作为自己的数据
     * @param column2SubOfColumn1Type 0:取前半部分; 大于 0: 取后半部分
     */
    @JvmStatic
    fun <T1> pick(
            activity: FragmentActivity?,
            title: String,
            value1: Int,
            column1: Array<T1>,
            column2SubOfColumn1Type: Int,
            handler: ((value1: Int, value2: Int) -> Unit),
    ) {
        activity?.supportFragmentManager?.let {
            val pickerFragment = PickerFragment.newInstance(title, column1.toStringArray())
            pickerFragment.setOnResultListener(handler)
            pickerFragment.setColumn2SubOfColumn1(column2SubOfColumn1Type)
            pickerFragment.value1 = value1
            pickerFragment.show(it, "picker-c")
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
            val pickerFragment = PickerFragment.newInstance(title, column1 = column1.toStringArray(), column2 = column2.toStringArray())
            pickerFragment.setOnResultListener(handler)
            pickerFragment.value1 = value1
            pickerFragment.value2 = value2
            pickerFragment.show(it, "picker-cc")
        }
    }

    @JvmStatic
    fun pick(
            activity: FragmentActivity?,
            title: String,
            value1: Int,
            provider: ((picker: PickerFragment) -> Unit),
            handler: ((value1: Int, value2: Int) -> Unit)? = null,
            valueHandler: ((value1: String, value2: String) -> Unit)? = null,
    ) {
        pick(activity, title, value1, 0, provider, handler, valueHandler)
    }

    @JvmStatic
    fun pick(
            activity: FragmentActivity?,
            title: String,
            value1: Int,
            value2: Int = 0,
            provider: ((picker: PickerFragment) -> Unit),
            handler: ((value1: Int, value2: Int) -> Unit)? = null,
            valueHandler: ((value1: String, value2: String) -> Unit)? = null,
    ) {
        activity?.supportFragmentManager?.let { fragmentManager ->
            val pickerFragment = PickerFragment.newInstance(title)
            handler?.let { pickerFragment.setOnResultListener(it) }
            valueHandler?.let { pickerFragment.setOnValueListener(it) }
            pickerFragment.value1 = value1
            pickerFragment.value2 = value2
            pickerFragment.show(fragmentManager, "picker-vv")
            provider.invoke(pickerFragment)
        }
    }

    @JvmStatic
    fun pickCity(
            activity: FragmentActivity?,
            multipleMode: Boolean,
            handler: ((cities: List<City>) -> Unit),
    ) {
        CityPickerFragment.Builder()
                .fragmentManager(activity?.supportFragmentManager)
                .animationStyle(R.style.DefaultCityPickerAnimation)
                .multipleMode(multipleMode)
                .enableHotCities(false)
                .enableLocation(false)
                .useDefaultCities(true)
                .resultListener {
                    handler.invoke(it)
                }
                .show()
    }

    @JvmStatic
    fun pickCity(
            activity: FragmentActivity?,
            provider: ((picker: CityPicker) -> Unit),
            handler: ((cities: List<City>) -> Unit),
    ) {
        pickCity(activity, true, provider, handler)
    }

    @JvmStatic
    fun pickCity(
            activity: FragmentActivity?,
            multipleMode: Boolean,
            provider: ((picker: CityPicker) -> Unit),
            handler: ((cities: List<City>) -> Unit),
    ) {
        CityPickerFragment.Builder()
                .fragmentManager(activity?.supportFragmentManager)
                .animationStyle(R.style.DefaultCityPickerAnimation)
                .multipleMode(multipleMode)
                .enableHotCities(false)
                .enableLocation(false)
                .useDefaultCities(false)
                .requestCitiesListener { cityPicker ->
                    provider.invoke(cityPicker)
                }
                .resultListener {
                    handler.invoke(it)
                }
                .show()
    }

    @JvmStatic
    val REQUEST_CODE_CHOOSE = 23

    @JvmStatic
    fun pickPhoto(
            fragment: Fragment? = null,
            max: Int = 1,
            crop: Boolean = false,
            requestCode: Int = REQUEST_CODE_CHOOSE,
    ) {
        if (fragment == null) {
            return
        }
        val matisse = Matisse.from(fragment)
        val countable = max > 1
        val picker = matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Zhihu)
                .countable(countable) //max == 1，则 countable = false
                .addFilter(GifSizeFilter(200, 200, 5 * Filter.K * Filter.K))
                .maxSelectable(max)
                .spanCount(4)
                .originalEnable(true)
                .maxOriginalSize(10)
                .capture(true)
                .imageEngine(GlideEngine())
        if (crop) {
            picker.crop(1f, 1f)
        } else {
            picker.crop(false)
        }
        picker.forResult(requestCode)
    }


    @JvmStatic
    fun pickPhoto(
            activity: FragmentActivity? = null,
            max: Int = 1,
            crop: Boolean = false,
            requestCode: Int = REQUEST_CODE_CHOOSE,
    ) {
        if (activity == null) {
            return
        }
        val matisse = Matisse.from(activity)
        val countable = max > 1
        val picker = matisse.choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Zhihu)
                .countable(countable) //max == 1，则 countable = false
                .addFilter(GifSizeFilter(200, 200, 5 * Filter.K * Filter.K))
                .maxSelectable(max)
                .spanCount(4)
                .originalEnable(true)
                .maxOriginalSize(10)
                .capture(true)
                .imageEngine(GlideEngine())
        if (crop) {
            picker.crop(1f, 1f)
        } else {
            picker.crop(false)
        }
        picker.forResult(requestCode)
    }

    @JvmStatic
    fun pickDate(
            activity: FragmentActivity? = null,
            handler: ((year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit),
    ) {
        if (activity == null) {
            return
        }
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(activity,
                { _, year, monthOfYear, dayOfMonth ->
                    handler.invoke(year, monthOfYear, dayOfMonth)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

}