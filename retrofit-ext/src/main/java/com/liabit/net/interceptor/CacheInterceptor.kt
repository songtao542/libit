package com.liabit.net.interceptor

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * 需求：在有网的情况下，正常进行网络请求，然后把响应缓存到本地；在无网的情况下，从本地拿到缓存，返回给调用方。
 *
 * 限制：不能改变服务器的API，服务器的API没有Cache-Control字段。
 *
 * 思路：利用OkHttp的拦截器实现。
 *
 * OkHttp请求过程：OkHttp的缓存机制（CacheInterceptor）会自动判断我们提交的 Request 中的 Cache-Control 头：
 *     如果是 only-if-cache（FORCE_CACHE），则只能从缓存中获取，不能进行网络请求，如果获取缓存失败，则返回一个504的错误响应码；
 *     如果是 no-cache 则只从网络中获取。
 *
 * OkHttp响应过程：当正常的网络请求返回之后，CacheInterceptor 会自动判断 Response 的 Cache-Control头：
 *     如果是 only-if-cache，则会缓存到本地；
 *     如果是 no-cache 则不缓存。
 *
 *        Request                                 Response
 *           |                                       ∧
 *           | Cache-Control:only-if-cached          |
 *           | read data from cache                  |
 *           |                                       |
 *           ∨                                       |
 *    -----------------------------------------------------
 *    |                     CacheInterceptor              |
 *    -----------------------------------------------------
 *           |                                       ∧
 *           |                                       |
 *           |                                       | Cache-Control:only-if-cached
 *           |                                       | save data to cache
 *           ∨                                       |
 *      网络（NetWork） ------------------------>  Response
 *
 *  POST, PATCH, PUT, DELETE, MOVE 请求不会被缓存，所以如果要缓存数据请使用 GET 请求
 *  详见： [okhttp3.internal.cache.CacheInterceptor] 和 [okhttp3.internal.httpHttpMethod] 的 invalidatesCache() 方法
 *  对服务器的返回结果添加缓存头
 */
class ResponseCacheInterceptor constructor() : Interceptor {

    private var mCacheControl = CacheControl.FORCE_CACHE.toString()

    @Suppress("unused")
    constructor(maxAge: Int) : this() {
        mCacheControl = CacheControl.Builder()
            .onlyIfCached()
            .maxAge(maxAge, TimeUnit.SECONDS)
            .build()
            .toString()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        return response.newBuilder()
            .removeHeader("Pragma") //移除影响
            .removeHeader("Cache-Control") //移除影响
            .addHeader("Cache-Control", mCacheControl)
            .build()
    }
}

/**
 * 在没有网络的情况下添加 Cache_Control 头，直接从缓存中读取数据
 */
class RequestCacheInterceptor(context: Context) : Interceptor {

    private val mContext: Context = context.applicationContext

    private val mConnectivityManager by lazy { mContext.getSystemService(ConnectivityManager::class.java) }

    private var mCacheControl = CacheControl.FORCE_CACHE

    @Suppress("unused")
    constructor(context: Context, maxAge: Int, maxStale: Int) : this(context) {
        mCacheControl = CacheControl.Builder()
            .onlyIfCached()
            .maxAge(maxAge, TimeUnit.SECONDS)
            .maxStale(maxStale, TimeUnit.SECONDS)
            .build()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (!isNetAvailable()) {
            request = request.newBuilder()
                .cacheControl(mCacheControl)
                .build()
        }
        return chain.proceed(request)
    }

    private fun isNetAvailable(): Boolean {
        return mConnectivityManager.activeNetwork != null
    }

}