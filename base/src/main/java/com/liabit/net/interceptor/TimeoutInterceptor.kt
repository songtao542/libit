package com.liabit.net.interceptor

import android.util.Log
import com.liabit.base.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


/**
 * 使用方法：
 * 1：@Headers("Timeout: 60000")
 * 2：@Headers("Connect-Timeout: 60000", "Read-Timeout: 60000", "Write-Timeout: 60000")
 */
class TimeoutInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        if (BuildConfig.DEBUG) {
            Log.d("TimeoutInterceptor", "original connectTimeout=$connectTimeout")
            Log.d("TimeoutInterceptor", "original readTimeout=$readTimeout")
            Log.d("TimeoutInterceptor", "original writeTimeout=$writeTimeout")
        }

        //@Headers("Timeout: 60000")
        val timeout = request.header("Timeout")?.trim()?.toIntOrNull()
        if (timeout != null) {
            connectTimeout = timeout
            readTimeout = timeout
            writeTimeout = timeout
        } else {
            // @Headers("Connect-Timeout: 60000", "Read-Timeout: 60000", "Write-Timeout: 60000")
            val connectTime = request.header("Connect-Timeout")?.trim()?.toIntOrNull()
            val readTime = request.header("Read-Timeout")?.trim()?.toIntOrNull()
            val writeTime = request.header("Write-Timeout")?.trim()?.toIntOrNull()
            if (connectTime != null) {
                connectTimeout = connectTime
            }
            if (readTime != null) {
                readTimeout = readTime
            }
            if (writeTime != null) {
                writeTimeout = writeTime
            }
        }
        if (BuildConfig.DEBUG) {
            Log.d("TimeoutInterceptor", "config connectTimeout=$connectTimeout")
            Log.d("TimeoutInterceptor", "config readTimeout=$readTimeout")
            Log.d("TimeoutInterceptor", "config writeTimeout=$writeTimeout")
        }
        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request)
    }
}