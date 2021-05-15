package com.liabit.third.weibo

import com.google.gson.Gson
import com.liabit.net.interceptor.EnhancedHttpLoggingInterceptor
import com.liabit.third.BuildConfig
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.WeiboUser
import okhttp3.OkHttpClient.Builder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeiboRestApi {

    companion object {
        @JvmStatic
        fun create(gson: Gson?): WeiboRestApi {
            val g = gson ?: Gson()
            return Retrofit.Builder()
                .baseUrl(ThirdAppInfo.WEIBO_API_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(getOkHttpClientBuilder().build())
                .build()
                .create(WeiboRestApi::class.java)
        }

        private fun getOkHttpClientBuilder(): Builder {
            val okHttpBuilder = Builder()
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = EnhancedHttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(EnhancedHttpLoggingInterceptor.Level.BODY)
                okHttpBuilder.addInterceptor(httpLoggingInterceptor)
            }
            return okHttpBuilder
        }
    }

    /**
     * @param accessToken 通过 [WeiboApi.authorize] 方法获取
     * @param uid 通过 [WeiboApi.authorize] 方法获取
     */
    @GET("2/users/show.json")
    suspend fun getUserInfo(
        @Query("access_token") accessToken: String,
        @Query("uid") uid: String
    ): WeiboUser?

}