package com.scaffold.network

import com.scaffold.network.model.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.QueryMap

interface ApiService {

    /**
     * ## 查询我的收藏列表
     * ```
     * userId    String    是    用户Id
     * ```
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("api/collect/list")
    suspend fun getCollection(@QueryMap params: Map<String, @JvmSuppressWildcards Any>): Response<List<String>>

    /**
     * ## 查询服务器时间
     */
    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("api/system/time")
    suspend fun getTime(): Response<Long>

}