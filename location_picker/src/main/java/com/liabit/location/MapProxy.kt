package com.liabit.location

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import com.liabit.location.model.PoiAddress
import com.liabit.location.model.Position
import com.liabit.location.util.AMapLocationProvider
import com.amap.api.maps2d.MapView
import io.nlopez.smartlocation.location.LocationProvider

interface MapProxy {
    fun initialize(mapView: View) {
        //default do nothing
    }

    fun addMarker(marker: Marker, zoomLevel: Float? = null)

    fun addMarkers(markers: List<Marker>, zoomLevel: Float? = null)

    fun removeMarker(marker: Marker)

    fun removeMarkers(markers: List<Marker>)

    fun setCenter(location: Position, zoomLevel: Float? = null)

    fun scrollBy(xPixel: Float, yPixel: Float)

    fun onResume()

    fun onPause()

    fun onCreate(savedInstanceState: Bundle?) {
        //default do nothing
    }

    fun onSaveInstanceState(outState: Bundle) {
        //default do nothing
    }

    fun onDestroy() {
        //default do nothing
    }

    fun reverseGeocode(location: Position): LiveData<PoiAddress>

    fun searchPoi(keyword: String, location: Position? = null): LiveData<List<PoiAddress>>

    fun getMapView(): View?
}

object MapLocationFactory {

    private var locationProvider: LocationProvider? = null

    @JvmStatic
    fun getLocationProvider(context: Context): LocationProvider? {
        if (locationProvider == null) {
            locationProvider = AMapLocationProvider(context.applicationContext)
        }
        return locationProvider
    }

    @JvmStatic
    fun create(context: Context): MapProxy {
        return AMapProxy(context.applicationContext).apply {
            initialize(MapView(context))
        }
    }

    @JvmStatic
    fun create(context: Context, mapView: MapView): MapProxy {
        return AMapProxy(context.applicationContext).apply {
            initialize(mapView)
        }
    }

}

