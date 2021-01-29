package com.irun.runker.net

import androidx.lifecycle.LiveData
import com.irun.runker.model.Joke
import com.irun.runker.model.Response
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * Author:         songtao
 * CreateDate:     2020/12/9 18:57
 */
interface Api {

    companion object {
        const val BASE_URL = "http://api.apishop.net"
    }

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("common/joke/getJokesByRandom")
    fun getJokeObservable(@Query("apiKey") apiKey: String, @Query("pageSize") pageSize: Int): Single<Response<List<Joke>>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("common/joke/getJokesByRandom")
    fun getJokeLiveData(@Query("apiKey") apiKey: String, @Query("pageSize") pageSize: Int): LiveData<Response<List<Joke>>>

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET("common/joke/getJokesByRandom")
    fun getJoke(@Query("apiKey") apiKey: String, @Query("pageSize") pageSize: Int): Response<List<Joke>>

}