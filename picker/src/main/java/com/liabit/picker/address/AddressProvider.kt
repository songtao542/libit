package com.liabit.picker.address

import android.content.Context
import com.liabit.picker.R
import com.liabit.picker.cascade.Cascade
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
        val inputStream: InputStream = context.resources.openRawResource(R.raw.city_data)
        val provinceList = ArrayList<Province>()
        val provinceArray = JSONArray(inputStream.bufferedReader().readText())
        for (i in 0 until provinceArray.length()) {
            val provinceObj = provinceArray.getJSONObject(i)
            val cityList = ArrayList<City>()
            val cityArray = provinceObj.getJSONArray("cities")
            for (j in 0 until cityArray.length()) {
                val cityObj = cityArray.getJSONObject(j)
                val districtList = ArrayList<District>()
                val districtArray = cityObj.getJSONArray("districts")
                for (k in 0 until districtArray.length()) {
                    val districtObj = districtArray.getJSONObject(k)
                    val streetList = ArrayList<Street>()
                    val streetArray = districtObj.getJSONArray("streets")
                    for (l in 0 until streetArray.length()) {
                        val streetObj = streetArray.getJSONObject(l)
                        streetList.add(Street(streetObj.getString("code"), streetObj.getString("name")))
                    }
                    districtList.add(District(districtObj.getString("code"), districtObj.getString("name"), streetList))
                }
                cityList.add(City(cityObj.getString("code"), cityObj.getString("name"), districtList))
            }
            provinceList.add(Province(provinceObj.getString("code"), provinceObj.getString("name"), cityList))
        }
        provinces = provinceList
        return provinceList
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

data class Address(val provinceCode: String,
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
