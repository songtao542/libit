package com.liabit.third.wx

import com.google.gson.Gson
import com.liabit.net.interceptor.EnhancedHttpLoggingInterceptor
import com.liabit.third.BuildConfig
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.WxAccessToken
import com.liabit.third.model.WxUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WxRestApi {

    companion object {
        @JvmStatic
        fun create(gson: Gson?): WxRestApi {
            val g = gson ?: Gson()
            return Retrofit.Builder()
                .baseUrl(ThirdAppInfo.WECHAT_API_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(getOkHttpClientBuilder().build())
                .build()
                .create(WxRestApi::class.java)
        }

        private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            val okHttpBuilder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = EnhancedHttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(EnhancedHttpLoggingInterceptor.Level.BODY)
                okHttpBuilder.addInterceptor(httpLoggingInterceptor)
            }
            okHttpBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val url = request.url.newBuilder()
                    .addQueryParameter("appid", ThirdAppInfo.WECHAT_APP_ID)
                    .addQueryParameter("secret", ThirdAppInfo.WECHAT_APP_SECRET)
                    .build()
                chain.proceed(request.newBuilder().url(url).build())
            })
            return okHttpBuilder
        }
    }


    /**
     * @param code 通过 [WxApi.authorize] 获取
     */
    @GET("sns/oauth2/access_token")
    suspend fun getAccessToken(
        @Query("code") code: String,
        @Query("grant_type") grantType: String
    ): WxAccessToken?

    @GET("sns/oauth2/refresh_token")
    suspend fun refreshToken(
        @Query("refresh_token") refreshToken: String,
        @Query("grant_type") grantType: String
    ): WxAccessToken?

    @GET("/sns/userinfo")
    suspend fun getWxUser(
        @Query("openid") openid: String,
        @Query("access_token") accessToken: String
    ): WxUser?


}