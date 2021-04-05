package com.irun.runker.util.retrofit

import android.util.Log
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 * Author:         songtao
 * CreateDate:     2020/12/10 19:46
 */
class DirectCallAdapter : CallAdapter.Factory() {

    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *> {

        val responseType: Type = if (returnType is WildcardType) returnType.upperBounds[0] else returnType

        return object : CallAdapter<Any, Any?> {

            override fun responseType() = responseType

            override fun adapt(call: Call<Any>): Any? {
                // 可以在这里判断接口数据格式
                try {
                    val r = call.execute().body()
                    Log.d("DirectCallAdapter", "result: $r")
                    return r
                } catch (e: Throwable) {
                    Log.d("DirectCallAdapter", "error: ", e)
                }
                return null
            }
        }
    }
}