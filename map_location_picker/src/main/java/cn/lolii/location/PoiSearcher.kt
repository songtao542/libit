package cn.lolii.location

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.lolii.location.model.PoiAddress
import com.amap.api.maps2d.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import javax.inject.Inject

class PoiSearcher @Inject constructor(private val context: Context) {
    fun reverseGeocode(location: Location): LiveData<PoiAddress> {
        val result = MutableLiveData<PoiAddress>()
        if (location.latitude != null && location.longitude != null) {
            val geocoderSearch = GeocodeSearch(context)
            geocoderSearch.setOnGeocodeSearchListener(object : GeocodeSearch.OnGeocodeSearchListener {
                override fun onRegeocodeSearched(regeocodeResult: RegeocodeResult?, p1: Int) {
                    result.postValue(regeocodeResult?.regeocodeAddress?.toPoiAddress())
                }

                override fun onGeocodeSearched(geocodeResult: GeocodeResult?, p1: Int) {
                    //geocode, do nothing
                }
            })

            //var latLng = convertGpsToGCJ02(location.latitude!!, location.longitude!!)
            //使用高德定位之后，国内默认是GCJ02
            val latLng = LatLng(location.latitude!!, location.longitude!!)
            val query = RegeocodeQuery(LatLonPoint(latLng.latitude, latLng.longitude), 200f, GeocodeSearch.AMAP)
            geocoderSearch.getFromLocationAsyn(query)
        }
        return result
    }

    fun search(location: Location, keyword: String): LiveData<List<PoiAddress>> {
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
        //var latLng = convertGpsToGCJ02(location.latitude!!, location.longitude!!)
        val latLng = LatLng(location.latitude, location.longitude)
        poiSearch.bound = PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 5000)
        poiSearch.searchPOIAsyn()
        return result
    }
}
