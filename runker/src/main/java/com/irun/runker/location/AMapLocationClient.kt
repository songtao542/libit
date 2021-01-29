package com.irun.runker.location

import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener

class AMapLocationClient(context: Context) : LocationClient<AMapLocation> {

    companion object {
        private const val TAG = "AMapLocationClient"
        const val INTERVAL = 4000L
    }

    private val mLocationClient = com.amap.api.location.AMapLocationClient(context.applicationContext)
    private val mLocationOption = AMapLocationClientOption()

    private var mLocationListener: LocationListener<AMapLocation>? = null

    private val mAMapLocationListener = AMapLocationListener {
        if (it.errorCode == 0) {
            mLocationListener?.onLocationChanged(it)
        } else {
            Log.d(TAG, "location error: ${it.errorCode}: ${it.errorInfo}")
        }
    }

    init {
        //设置定位属性
        mLocationOption.locationPurpose = AMapLocationClientOption.AMapLocationPurpose.Sport
        //可选, 设置定位模式, 可选的模式有高精度、仅设备、仅网络. 默认为高精度模式
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //可选, 设置是否gps优先, 只在高精度模式下有效. 默认关闭
        mLocationOption.isGpsFirst = false
        //可选, 设置网络请求超时时间. 默认为30秒. 在仅设备模式下无效
        mLocationOption.httpTimeOut = 30000
        //可选, 设置定位间隔, 默认为2秒
        mLocationOption.interval = INTERVAL
        //可选, 设置是否返回逆地理地址信息. 默认是true
        mLocationOption.isNeedAddress = false
        //可选, 设置是否单次定位. 默认是false
        mLocationOption.isOnceLocation = false
        // 可选, 设置是否等待wifi刷新, 默认为false.如果设置为true, 会自动变为单次定位, 持续定位时不要使用
        mLocationOption.isOnceLocationLatest = false
        //可选,  设置网络请求的协议. 可选HTTP或者HTTPS. 默认为HTTP
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP)
        //可选, 设置是否使用传感器. 默认是false
        mLocationOption.isSensorEnable = false
        //可选, 设置是否开启wifi扫描. 默认为true, 如果设置为false会同时停止主动刷新, 停止以后完全依赖于系统刷新, 定位位置可能存在误差
        mLocationOption.isWifiScan = true
        //可选, 设置是否使用缓存定位, 默认为true
        mLocationOption.isLocationCacheEnable = true
        //可选, 设置逆地理信息的语言, 默认值为默认语言（根据所在地区选择语言）
        mLocationOption.geoLanguage = AMapLocationClientOption.GeoLanguage.ZH
        mLocationClient.setLocationOption(mLocationOption)
        // 设置定位监听
        mLocationClient.setLocationListener(mAMapLocationListener)
    }

    override fun setLocationListener(listener: LocationListener<AMapLocation>) {
        mLocationListener = listener
    }

    override fun getLastKnownLocation(): AMapLocation? {
        return mLocationClient.lastKnownLocation
    }

    /**
     *  开始定位
     */
    override fun startLocation() {
        mLocationClient.startLocation()
    }

    override fun stopLocation() {
        mLocationClient.stopLocation()
    }

    override fun close() {
        mLocationClient.stopLocation()
        mLocationClient.onDestroy()
    }

}