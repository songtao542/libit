package com.domain.scaffold.network.interceptor

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.domain.scaffold.autoclear.broadcast
import okhttp3.Interceptor
import okhttp3.Response
import java.nio.charset.Charset

class LoginStateInterceptor(context: Context, private val gson: Gson) : Interceptor {

    private val mContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val response = chain.proceed(originRequest)
        val source = response.body?.source()
        source?.request(Long.MAX_VALUE)
        source?.buffer?.clone()?.readString(Charset.forName("UTF-8"))?.let { result ->
            try {
                val jsonObject = gson.fromJson(result, JsonObject::class.java)
                val status = jsonObject.get("status").asString
                // status == "LOGIN" 说明 sid 过期，需要重新登录
                if (status == "LOGIN") {
                    mContext.broadcast("login").send()
                }
            } catch (e: Exception) {
                Log.d("LoginStateInterceptor", "error:${e.message}", e)
            }
            return@let
        }
        return response
    }
}