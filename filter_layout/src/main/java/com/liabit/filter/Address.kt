package com.liabit.filter

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Author:         songtao
 * CreateDate:     2020/11/14 16:42
 */
@Parcelize
data class Address(
        val provinceCode: String,
        val provinceName: String,
        val cityCode: String,
        val cityName: String,
        val districtCode: String,
        val districtName: String) : Parcelable {

    val formatted: String
        get() {
            return if (provinceName == cityName) "$provinceName$districtName" else "$provinceName$cityName$districtName"
        }
}