package com.domain.scaffold.network

import android.content.Context
import com.domain.scaffold.library.BuildConfig
import com.domain.scaffold.net.interceptor.*
import com.domain.scaffold.net.retrofit.GsonConverterFactory
import com.domain.scaffold.network.interceptor.BasicInfoInterceptor
import com.domain.scaffold.network.interceptor.BasicInfoProvider
import com.domain.scaffold.network.model.Response
import com.domain.scaffold.network.retrofit.LiveDataCallAdapterFactory
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Author:         songtao
 * CreateDate:     2020/12/9 18:57
 */
interface Api : ApiService {

    companion object {
        private const val DEBUG_BASE_URL = "https://www.baidu.com"

        //private const val BASE_URL = "https://www.baidu.com"
        private const val BASE_URL = "http://172.28.21.243:10083"

        private const val DEFAULT_TIMEOUT = 30000L


        private val createErrorBodyMethod = { gson: Gson, e: Throwable ->
            gson.toJson(Response<Any>(null, 600, e.message))//  """{"status": 200,"message": "${e.message}","data": null}"""
        }

        fun create(context: Context, basicInfoProvider: BasicInfoProvider, gson: Gson? = null): Api {
            val g = gson ?: Gson()
            return Retrofit.Builder()
                .baseUrl(DEBUG_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(g, createErrorBodyMethod))
                //.addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                //.addCallAdapterFactory(DirectCallAdapter())
                .client(createOkHttpClient(context, g, basicInfoProvider))
                .build()
                .create(Api::class.java)

        }

        private fun createOkHttpClient(context: Context, gson: Gson, basicInfoProvider: BasicInfoProvider): OkHttpClient {
            val httpCacheDirectory = File(context.cacheDir, "okhttp")
            val cacheSize = 10 * 1024 * 1024L // 10 MiB
            val cache = Cache(httpCacheDirectory, cacheSize)

            val okHttpBuilder = OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(ErrorHandleInterceptor(gson, createErrorBodyMethod))
                .addInterceptor(BasicInfoInterceptor(basicInfoProvider))

            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = EnhancedHttpLoggingInterceptor()
                httpLoggingInterceptor.level = EnhancedHttpLoggingInterceptor.Level.BODY
                okHttpBuilder.addInterceptor(httpLoggingInterceptor)
            }

            okHttpBuilder
                .addInterceptor(TimeoutInterceptor())
                //.addInterceptor(LoginStateInterceptor(context, gson))
                .addInterceptor(RequestCacheInterceptor(context))
                .addNetworkInterceptor(ResponseCacheInterceptor())

            return okHttpBuilder.build()
        }
    }

}