package com.scaffold.network.interceptor

import com.scaffold.net.util.MD5.md5
import okhttp3.Interceptor
import okhttp3.Response

class BasicInfoInterceptor(private val provider: BasicInfoProvider) : Interceptor {

    companion object {
        const val APP_KEY = "800041"
        const val APP_SECRET = "85272dc5e8064f9aa4f914491ff67c15"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = provider.token
        val language = provider.language
        val version = provider.versionCode
        val versionName = provider.versionName
        //val location = provider.location
        val requestBuilder = original.newBuilder()
        if (!token.isNullOrEmpty()) {
            val timestamp = System.currentTimeMillis()
            val sign = md5(md5(APP_KEY + APP_SECRET + token) + timestamp)
            requestBuilder.header("appKey", APP_KEY)
            requestBuilder.header("sign", sign)
            requestBuilder.header("token", token)
            requestBuilder.header("timestamp", timestamp.toString())
            requestBuilder.header("imei", provider.imei)
        }
        //requestBuilder.header("language", language)
        //requestBuilder.header("version", version.toString())
        //requestBuilder.header("versionName", versionName)
        /*if (location != null) {
            requestBuilder.header("longitude", location.longitude.toString())
            requestBuilder.header("altitude", location.altitude.toString())
        }*/
        requestBuilder.method(original.method, original.body)
        return chain.proceed(requestBuilder.build())
    }
}

interface BasicInfoProvider {

    val token: String?
    val language: String
    val versionCode: Long
    val versionName: String
    val imei: String
    //val location: Location?

    //fun updateLocation(location: Location? = null)
}