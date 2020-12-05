package com.liabit.location.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PoiAddress(
        var title: String? = null,
        var distance: Double = 0.0,
        var position: Position? = null,
        var entrancePosition: Position? = null,
        var exitPosition: Position? = null,
        var tel: String? = null,
        var country: String? = null,
        var countryCode: String = "CN",
        var province: String? = null,
        var district: String? = null,
        var city: String? = null,
        var street: String? = null,
        var streetNumber: String? = null,
        var postalCode: String? = null,
        var address: String? = null,
        var type: Int = AddressType.POI_ADDRESS.value
) : Selectable(), Parcelable {

    val latitude: Double
        get() = position?.latitude ?: 0.0

    val longitude: Double
        get() = position?.longitude ?: 0.0

    val formatAddress: String
        get() {
            return if (address != null) {
                address!!
            } else {
                "${province ?: ""}${city ?: ""}${district ?: ""}${street ?: ""}"
            }
        }

    val location: Position
        get() {
            return Position(latitude, longitude)
        }
}

enum class AddressType(val value: Int) {
    ADDRESS(0),
    POI_ADDRESS(1);

    companion object {
        fun from(value: Int): AddressType {
            return when (value) {
                0 -> ADDRESS
                else -> POI_ADDRESS
            }
        }
    }
}