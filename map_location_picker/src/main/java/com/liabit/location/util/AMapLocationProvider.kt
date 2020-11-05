package com.liabit.location.util

import android.content.Context
import android.location.Location
import androidx.core.app.ActivityCompat
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import io.nlopez.smartlocation.OnLocationUpdatedListener
import io.nlopez.smartlocation.location.LocationProvider
import io.nlopez.smartlocation.location.LocationStore
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.utils.Logger

private const val LOCATIONMANAGERPROVIDER_ID = "ALMP"

class AMapLocationProvider(val context: Context) : LocationProvider, AMapLocationListener {

    private var locationManager: AMapLocationClient? = null
    private var listener: OnLocationUpdatedListener? = null
    private val locationStore: LocationStore = LocationStore(context)
    private var logger: Logger? = null


    override fun init(context: Context?, logger: Logger?) {
        if (locationManager == null) {
            locationManager = AMapLocationClient(context)
        }
        //设置定位监听
        locationManager!!.setLocationListener(this)
        this.logger = logger
    }

    override fun start(listener: OnLocationUpdatedListener?, params: LocationParams, singleUpdate: Boolean) {
        this.listener = listener
        if (listener == null) {
            logger?.d("Listener is null, you sure about this?", emptyArray<String>())
        }
        if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            logger?.i("Permission check failed. Please handle it in your app before setting up location", emptyArray<String>())
            return
        }

        val locationOption = getClientOption(params)
        locationOption.isOnceLocation = singleUpdate
        //设置定位参数
        locationManager?.let {
            it.setLocationOption(locationOption)
            /**
             * 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
             * 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
             * 在定位结束后，在合适的生命周期调用onDestroy()方法
             * 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
             */
            it.startLocation()
        }

    }

    fun onDestroy() {
        locationManager?.onDestroy()
    }

    override fun stop() {
        if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") == 0 || ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") == 0) {
            locationManager?.stopLocation()
        }
    }

    override fun getLastLocation(): Location? {
        locationManager?.let {
            if (ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_FINE_LOCATION") != 0 && ActivityCompat.checkSelfPermission(context, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
                return null
            }

            val location = it.lastKnownLocation
            if (location != null) {
                return location
            }
        }
        return locationStore.get(LOCATIONMANAGERPROVIDER_ID)
    }

    private fun getClientOption(params: LocationParams): AMapLocationClientOption {
        val accuracy: LocationAccuracy = params.accuracy
        val locationOption = AMapLocationClientOption()
        when (accuracy) {
            LocationAccuracy.HIGH -> {
                //设置定位模式:Hight_Accuracy为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                //设置定位间隔,单位毫秒
                locationOption.interval = 500
            }
            LocationAccuracy.MEDIUM -> {
                //locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Device_Sensors
                //Device_Sensors模式经常无法定位，所以改用Hight_Accuracy
                locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
                locationOption.interval = 2500
            }
            LocationAccuracy.LOW, LocationAccuracy.LOWEST -> {
                locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving
                locationOption.interval = 5000
            }
        }
        return locationOption
    }


    override fun onLocationChanged(location: AMapLocation?) {
        location?.let {
            logger?.d("onLocationChanged", arrayOf(location))
            listener?.onLocationUpdated(location)

            locationStore.let {
                logger?.d("Stored in SharedPreferences", emptyArray<String>())
                locationStore.put(LOCATIONMANAGERPROVIDER_ID, location)
            }
        }
    }


}
