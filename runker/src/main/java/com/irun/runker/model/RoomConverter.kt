package com.irun.runker.model

import androidx.room.TypeConverter
import com.amap.api.maps.model.LatLng

/**
 * Author:         songtao
 * CreateDate:     2020/12/7 18:06
 */
class RoomConverter {

    @TypeConverter
    fun stringToLatLng(value: String): LatLng {
        val array: Array<String> = value.split(",").toTypedArray()
        return LatLng(array[0].toDouble(), array[1].toDouble())
    }

    @TypeConverter
    fun latLngToString(latLng: LatLng): String {
        val locString = StringBuffer()
        locString.append(latLng.latitude).append(",")
        locString.append(latLng.longitude)
        return locString.toString()
    }

    @TypeConverter
    fun stringToLatLngList(value: String): ArrayList<LatLng> {
        val array: Array<String> = value.split(";").toTypedArray()
        val latLngList = ArrayList<LatLng>()
        for (latLngStr in array) {
            latLngList.add(stringToLatLng(latLngStr))
        }
        return latLngList
    }

    @TypeConverter
    fun latLngListToString(latLngList: ArrayList<LatLng>): String {
        val latLngListString = StringBuffer()
        for (i in latLngList.indices) {
            val latLng = latLngList[i]
            if (i != latLngList.size - 1) {
                latLngListString.append(latLngToString(latLng)).append(";")
            } else {
                latLngListString.append(latLngToString(latLng)).append(";")
            }
        }
        return latLngListString.toString()
    }

}