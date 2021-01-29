package com.irun.runker.util.retrofit.livedata

import android.util.Log
import androidx.lifecycle.LiveData
import com.irun.runker.BuildConfig
import com.irun.runker.model.Response
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.CallAdapter.Factory
import retrofit2.Callback
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataWrappedCallAdapterFactory : Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        if (getRawType(returnType) != LiveData::class.java) {
            return null
        }
        // LiveData<LiveDataParameterizedType>
        val liveDataParameterizedType = getParameterUpperBound(0, returnType as ParameterizedType)
        if (liveDataParameterizedType !is ParameterizedType) {
            throw IllegalArgumentException("return type must be parameterized")
        }
        val rawLiveDataParameterizedType = getRawType(liveDataParameterizedType)
        if (rawLiveDataParameterizedType != Response::class.java) {
            throw IllegalArgumentException("return type's parameterized type must be a Response")
        }
        // LiveData<LiveDataParameterizedType<BodyType>>
        val bodyType = getParameterUpperBound(0, liveDataParameterizedType)
        if (BuildConfig.DEBUG) {
            Log.d("LiveDataCallAdapter", "rawLiveDataParameterizedType: $rawLiveDataParameterizedType")
        }
        return LiveDataWrappedCallAdapter<Any>(bodyType)

    }
}

/**
 * A Retrofit adapter that converts the Call into a LiveData of ApiResponse.
 * @param responseType http 请求返回结果类型
 */
class LiveDataWrappedCallAdapter<BODY>(private val responseType: Type) : CallAdapter<BODY, LiveData<Response<BODY>>> {

    override fun responseType() = responseType

    override fun adapt(call: Call<BODY>): LiveData<Response<BODY>> {
        return object : LiveData<Response<BODY>>() {
            private var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<BODY> {
                        override fun onResponse(call: Call<BODY>, response: retrofit2.Response<BODY>) {
                            val body = response.body()
                            if (BuildConfig.DEBUG) {
                                Log.d("LDWrappedCallAdapter", "onResponse body: $body")
                            }
                            postValue(Response(data = body, code = response.code(), message = response.message()))
                        }

                        override fun onFailure(call: Call<BODY>, throwable: Throwable) {
                            if (BuildConfig.DEBUG) {
                                Log.e("LDWrappedCallAdapter", "onFailure: $throwable")
                            }
                            postValue(Response(code = 0, message = throwable.message))
                        }
                    })
                }
            }
        }
    }
}
