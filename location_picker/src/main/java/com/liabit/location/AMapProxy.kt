package com.liabit.location

import android.content.Context
import android.os.Bundle
import android.view.View
import android.graphics.Point
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.liabit.location.model.AddressType
import com.liabit.location.model.PoiAddress
import com.liabit.location.model.Position
import com.amap.api.location.AMapLocation
import com.amap.api.maps2d.CameraUpdateFactory
import com.amap.api.maps2d.CoordinateConverter
import com.amap.api.maps2d.MapView
import com.amap.api.maps2d.model.BitmapDescriptor
import com.amap.api.maps2d.model.BitmapDescriptorFactory
import com.amap.api.maps2d.model.LatLng
import com.amap.api.maps2d.model.MarkerOptions
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.*
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch

class AMapProxy constructor(val context: Context) : MapProxy {

    private val markers = HashMap<Marker, com.amap.api.maps2d.model.Marker>()

    private var mapView: MapView? = null

    override fun initialize(mapView: View) {
        if (mapView !is MapView) {
            throw IllegalArgumentException("AMapProxy need a TextureMapView")
        } else {
            this.mapView = mapView
            this.mapView!!.map?.moveCamera(CameraUpdateFactory.zoomTo(18f))
        }
    }

    override fun getMapView(): View? {
        return mapView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mapView?.onCreate(savedInstanceState)
    }

    override fun onResume() {
        mapView?.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        mapView?.onDestroy()
    }

    override fun addMarker(marker: Marker, zoomLevel: Float?) {
        mapView?.map?.let {
            @Suppress("SENSELESS_COMPARISON")
            if (marker.location.latitude != null && marker.location.longitude != null) {
                var bitmapDescriptor: BitmapDescriptor? = null
                marker.imageResourceId?.let { resId ->
                    bitmapDescriptor = BitmapDescriptorFactory.fromResource(resId)
                }

                if (bitmapDescriptor == null) {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(marker.imageBitmap)
                }
                val markerOption = MarkerOptions().icon(bitmapDescriptor)
                        //.position(convertGpsToGCJ02(marker.location.latitude!!, marker.location.longitude!!))
                        .position(LatLng(marker.location.latitude, marker.location.longitude))
                        .draggable(false)
                markers[marker] = it.addMarker(markerOption)
                if (marker.center) {
                    setCenter(marker.location, zoomLevel)
                } else if (zoomLevel != null) {
                    mapView?.map?.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
                }
            }
        }
    }

    @Suppress("unused")
    private fun convertGpsToGCJ02(latitude: Double, longitude: Double): LatLng {
        var converter = CoordinateConverter()
        converter = converter.from(CoordinateConverter.CoordType.GPS)
        converter.coord(LatLng(latitude, longitude))
        return converter.convert()
    }

    override fun removeMarker(marker: Marker) {
        val mapMarker = markers[marker]
        mapMarker?.destroy()
    }

    override fun addMarkers(markers: List<Marker>, zoomLevel: Float?) {
        for (marker in markers) {
            addMarker(marker, zoomLevel)
        }
    }

    override fun removeMarkers(markers: List<Marker>) {
        for (marker in markers) {
            removeMarker(marker)
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun setCenter(location: Position, zoomLevel: Float?) {
        if (location.latitude != null && location.longitude != null) {
            //var latLng = convertGpsToGCJ02(location.latitude!!, location.longitude!!)
            //使用高德定位之后，国内默认是GCJ02
            val latLng = LatLng(location.latitude, location.longitude)
            if (zoomLevel != null) {
                mapView?.map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
            } else {
                mapView?.map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    override fun scrollBy(xPixel: Float, yPixel: Float) {
        mapView?.map?.animateCamera(CameraUpdateFactory.scrollBy(xPixel, yPixel))
    }

    @Suppress("SENSELESS_COMPARISON")
    override fun reverseGeocode(location: Position): LiveData<PoiAddress> {
        val result = MutableLiveData<PoiAddress>()
        if (location.latitude != null && location.longitude != null) {
            val geocoderSearch = GeocodeSearch(context)
            geocoderSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
                override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult?, p1: Int) {
                    val address = regeocodeResult?.regeocodeAddress?.toPoiAddress()
                    address?.position = location
                    address?.let {
                        result.postValue(address)
                    }
                }

                override fun onGeocodeSearched(geocodeResult: GeocodeResult?, p1: Int) {
                    //geocode, do nothing
                }
            })

            //var latLng = convertGpsToGCJ02(location.latitude!!, location.longitude!!)
            //使用高德定位之后，国内默认是GCJ02
            val latLng = LatLng(location.latitude, location.longitude)
            val query = RegeocodeQuery(LatLonPoint(latLng.latitude, latLng.longitude), 200f, GeocodeSearch.AMAP)
            geocoderSearch.getFromLocationAsyn(query)
        }
        return result
    }

    override fun searchPoi(keyword: String, location: Position?): LiveData<List<PoiAddress>> {
        val result = MutableLiveData<List<PoiAddress>>()
        val poiSearchQuery = PoiSearch.Query(keyword, "")
        val poiSearch = PoiSearch(context, poiSearchQuery)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(pageResult: PoiResult?, errorCode: Int) {
                pageResult?.let {
                    if (it.pois.size > 0) {
                        val pois = ArrayList<PoiAddress>()
                        for (poiItem in it.pois) {
                            pois.add(poiItem.toPoiAddress())
                        }
                        result.postValue(pois)
                    }
                }
            }

            override fun onPoiItemSearched(poiItem: PoiItem, errorCode: Int) {
            }
        })
        location?.let {
            //var latLng = convertGpsToGCJ02(location.latitude!!, location.longitude!!)
            val latLng = LatLng(location.latitude, location.longitude)
            poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 5000)
        }
        poiSearch.searchPOIAsyn()
        return result
    }

}

@Suppress("unused")
fun AMapLocation.description(): String {
    return "{latitude:$latitude, longitude:$longitude, address:$address, country:$country, province$province, city:$city, " +
            "district$district, street:$street, streetNumber:$streetNum, description:$description}"
}


fun RegeocodeAddress.toPoiAddress(): PoiAddress {
    return PoiAddress(
            country = country,
            province = province,
            district = district,
            city = city,
            street = streetNumber?.street,
            streetNumber = streetNumber?.number,
            address = formatAddress
    )
}

fun PoiItem.toPoiAddress(): PoiAddress {
    return PoiAddress(
            title = title,
            distance = distance.toDouble(),
            position = if (latLonPoint != null) Position(latLonPoint.latitude, latLonPoint.longitude) else null,
            entrancePosition = if (enter != null) Position(enter.latitude, enter.longitude) else null,
            exitPosition = if (exit != null) Position(exit.latitude, exit.longitude) else null,
            tel = tel,
            province = provinceName,
            city = cityName,
            street = snippet,
            postalCode = postcode)
}

fun AMapLocation.toPoiAddress(): PoiAddress {
    return PoiAddress(
            title = poiName,
            district = district,
            position = Position(latitude, longitude),
            province = province,
            city = city,
            street = street,
            address = address ?: (province.value() + city.value() + district.value()
                    + street.value() + streetNum.value() + poiName.value()),
            type = AddressType.ADDRESS.value)
}

fun String?.value(): String {
    return this ?: ""
}

fun MapView.getCenter(): LatLng {
    val left = left
    val top = top
    val right = right
    val bottom = bottom
    val x = (x + (right - left) / 2)
    val y = (y + (bottom - top) / 2)
    val projection = map.projection
    return projection.fromScreenLocation(Point(x.toInt(), y.toInt()))
}
