package com.liabit.net.interceptor

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class ErrorHandleInterceptor(
    private val gson: Gson,
    private val errorResponseBodyProvider: (gson: Gson, exception: Throwable) -> String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val request = chain.request()
            val res = chain.proceed(request)
            if (!res.isSuccessful) {
                throw RuntimeException("server: ${res.message}")
            }
            return res
            //return chain.proceed(request)
        } catch (e: Exception) {
            val message = e.message ?: "none"
            val unknownError = errorResponseBodyProvider.invoke(gson, e)
            val body = unknownError.toResponseBody("text/plain".toMediaType())
            return Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .message(message)
                .body(body)
                .code(200)
                .build()
        }
    }
}