package com.domain.scaffold.network.retrofit

import android.util.Log
import androidx.lifecycle.LiveData
import com.domain.scaffold.library.BuildConfig
import com.domain.scaffold.network.model.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import retrofit2.Callback
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataCallAdapterFactory : Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        // LiveData<LiveDataParameterizedType>
        val liveDataParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (liveDataParameterizedType !is ParameterizedType) {
            throw IllegalArgumentException("return type must be parameterized")
        }
        // LiveData<Response<*>>, 限制 LiveDataParameterizedType 必须为 Response
        val rawLiveDataParameterizedType = getRawType(liveDataParameterizedType)
        if (rawLiveDataParameterizedType != Response::class.java) {
            throw IllegalArgumentException("return type's parameterized type must be a Response")
        }
        if (BuildConfig.DEBUG) {
            Log.d("LiveDataCallAdapter", "rawLiveDataParameterizedType: $rawLiveDataParameterizedType")
        }
        return LiveDataCallAdapter(rawLiveDataParameterizedType)
    }
}

/**
 * A Retrofit adapter that converts the Call into a LiveData of Response.
 * @param responseType The response type
 */
class LiveDataCallAdapter(private val responseType: Type) : CallAdapter<Response<*>, LiveData<Response<*>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<Response<*>>): LiveData<Response<*>> {
        return object : LiveData<Response<*>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<Response<*>> {
                        override fun onResponse(call: Call<Response<*>>, response: retrofit2.Response<Response<*>>) {
                            val body = response.body()
                            if (BuildConfig.DEBUG) {
                                Log.d("LiveDataCallAdapter", "onResponse body: $body")
                            }
                            postValue(body)
                        }

                        override fun onFailure(call: Call<Response<*>>, throwable: Throwable) {
                            if (BuildConfig.DEBUG) {
                                Log.e("LiveDataCallAdapter", "onFailure: $throwable")
                            }
                            postValue(Response<Any>(code = -1, message = throwable.message))
                        }
                    })
                }
            }
        }
    }
}

