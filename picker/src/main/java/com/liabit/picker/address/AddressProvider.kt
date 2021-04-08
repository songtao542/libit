package com.liabit.picker.address

import android.content.Context
import android.os.Parcelable
import android.util.Log
import com.liabit.picker.R
import com.liabit.picker.cascade.Cascade
import kotlinx.android.parcel.Parcelize
import org.json.JSONArray
import java.io.InputStream

/**
 * Author:         songtao
 * CreateDate:     2020/10/9 17:48
 */
object AddressProvider {

    private var provinces: List<Province>? = null

    fun getProvince(context: Context): List<Province> {
        val ps = provinces
        if (!ps.isNullOrEmpty()) {
            return ps
        }
        try {
            val inputStream: InputStream = context.resources.openRawResource(R.raw.city_data)
            val provinceList = ArrayList<Province>()
            val provinceArray = JSONArray(inputStream.bufferedReader().readText())
            for (i in 0 until provinceArray.length()) {
                val provinceObj = provinceArray.getJSONObject(i)
                val cityList = ArrayList<City>()
                val cityArray = provinceObj.getJSONArray("cs")//cities
                for (j in 0 until cityArray.length()) {
                    val cityObj = cityArray.getJSONObject(j)
                    val districtList = ArrayList<District>()
                    val districtArray = cityObj.getJSONArray("ds")//districts
                    for (k in 0 until districtArray.length()) {
                        val districtObj = districtArray.getJSONObject(k)
                        val streetList = ArrayList<Street>()
                        val streetArray = districtObj.getJSONArray("ss")//streets
                        for (l in 0 until streetArray.length()) {
                            val streetObj = streetArray.getJSONObject(l)
                            // c:code, n:name
                            streetList.add(Street(streetObj.getString("c"), streetObj.getString("n")))
                        }
                        districtList.add(District(districtObj.getString("c"), districtObj.getString("n"), streetList))
                    }
                    cityList.add(City(cityObj.getString("c"), cityObj.getString("n"), districtList))
                }
                provinceList.add(Province(provinceObj.getString("c"), provinceObj.getString("n"), cityList))
            }
            provinces = provinceList
            return provinceList
        } catch (e: Throwable) {
            Log.d("AddressProvider", "getProvince error: ", e)
            return emptyList()
        }
    }
}


data class Province(val code: String, val name: String, val cities: List<City>) : Cascade {
    override fun getDisplayName(): String {
        return name
    }

    override fun getChildren(): List<Cascade> {
        return cities
    }
}

data class City(val code: String, val name: String, val districts: List<District>) : Cascade {
    override fun getDisplayName(): String {
        return name
    }

    override fun getChildren(): List<Cascade> {
        return districts
    }
}

data class District(val code: String, val name: String, val streets: List<Street>) : Cascade {
    override fun getDisplayName(): String {
        return name
    }

    override fun getChildren(): List<Cascade> {
        return streets
    }
}

data class Street(val code: String, val name: String) : Cascade {
    override fun getDisplayName(): String {
        return name
    }

    override fun getChildren(): List<Cascade> {
        return emptyList()
    }
}

@Parcelize
data class Address(val provinceCode: String,
                   val provinceName: String,
                   val cityCode: String,
                   val cityName: String,
                   val districtCode: String,
                   val districtName: String) : Parcelable {

    val formatted: String get() = if (provinceName == cityName) "$provinceName$districtName" else "$provinceName$cityName$districtName"
}
