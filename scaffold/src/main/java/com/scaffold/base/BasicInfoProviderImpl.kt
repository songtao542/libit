package com.scaffold.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.scaffold.network.interceptor.BasicInfoProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

class BasicInfoProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : BasicInfoProvider {

    companion object {
        const val TAG = "BasicInfoProviderImpl"
    }

    private val mVersionCode: Long
    private val mVersionName: String
    private val mLanguage: String
    //private var mLocation: Location?

    init {
        mVersionCode = getVerCode(context)
        mVersionName = getVerName(context)
        mLanguage = Locale.getDefault().language
        //mLocation = getLastKnownLocation()
    }

    override val versionCode: Long get() = mVersionCode
    override val versionName: String get() = mVersionName
    override val token: String? get() = getApiToken()
    override val language: String get() = mLanguage
    override val imei: String get() = getDeviceImei()
    //override val location: Location? get() = mLocation

    /*override fun updateLocation(location: Location?) {
        if (location == null) {
            val curLocation = getLastKnownLocation()
            if (curLocation != null) {
                mLocation = curLocation
            }
        } else {
            mLocation = location
        }
    }*/

    @SuppressLint("MissingPermission")
    private fun getDeviceImei(): String {
        return try {
            context.getSystemService(TelephonyManager::class.java).imei
        } catch (e: Throwable) {
            DeviceId.getDeviceId(context)
        }
    }

    private fun getApiToken(): String? {
        return ""
    }

    private fun getVerCode(context: Context): Long {
        var verCode = -1L
        try {
            verCode = context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "getVerCode", e)
        }
        return verCode
    }

    private fun getVerName(context: Context): String {
        var verName = ""
        try {
            verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "getVerName", e)
        }
        return verName
    }

    @Suppress("unused")
    private fun getPackageName(context: Context): String {
        return context.packageName
    }

    //@SuppressLint("MissingPermission")
    /*private fun getLastKnownLocation(): Location? {
        //获取定位服务
        val locationManager = context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //获取当前可用的位置控制器
        val list = locationManager.getProviders(true)
        var location: Location? = null
        if (hasLocationPermission()) {
            if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                //网络定位
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            if (list.contains(LocationManager.GPS_PROVIDER)) {
                //GPS定位
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            }
        }
        return location
    }*/

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("versionCode:").append(versionCode).append(", language:").append(language)
        /*if (location == null) {
            sb.append(", location:null")
        } else {
            sb.append(", location:").append(location?.provider).append("(").append(location?.longitude).append(",").append(location?.latitude).append(")")
        }*/
        return sb.toString()
    }
}