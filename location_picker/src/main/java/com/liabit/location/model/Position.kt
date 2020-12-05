package com.liabit.location.model

import android.os.Parcelable
import com.amap.api.maps2d.model.LatLng
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Position(
        val latitude: Double,
        val longitude: Double) : Parcelable {

    constructor(location: android.location.Location) : this(location.latitude, location.longitude)

    constructor(latLng: LatLng) : this(latLng.latitude, latLng.longitude)

}