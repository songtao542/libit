package com.scaffold.network.interceptor

import android.util.Log
import com.scaffold.net.StringRequestBody
import com.scaffold.net.util.MD5.md5
import com.scaffold.network.BuildConfig
import okhttp3.*
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList

private val UTF8 = Charset.forName("UTF-8")
private const val APP_SECRET_KEY = "appSecret"
private const val APP_SECRET_VALUE = "2018-11-20 13:14:00"
private const val TAG = "Interceptor"

private fun getQueryParameters(httpUrl: HttpUrl): TreeMap<String, String> {
    val names = httpUrl.queryParameterNames
    val params = TreeMap<String, String>()
    val iterator = names.iterator()
    while (iterator.hasNext()) {
        val name = iterator.next()
        val value = httpUrl.queryParameter(name)
        if (!value.isNullOrBlank()) {
            params[name] = value
        }
    }
    return params
}

private fun getParameters(requestBody: RequestBody?): TreeMap<String, String> {
    when (requestBody) {
        is FormBody -> {
            val params = TreeMap<String, String>()
            for (i in 0 until requestBody.size) {
                val value = requestBody.encodedValue(i)
                if (!value.isNullOrBlank()) {
                    params[requestBody.encodedName(i)] = value
                }
            }
            return params
        }
        is MultipartBody -> {
            val parts = requestBody.parts
            val params = TreeMap<String, String>()
            for (part in parts) {
                val body = part.body
                if (body is StringRequestBody) {
                    params[body.name] = body.value
                }
            }
            return params
        }
        else -> return TreeMap()
    }
}

/**
 * 参数签名拦截器
 */
open class SignInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val request = chain.request()
            when (request.method) {
                "GET" -> chain.proceed(interceptGet(request))
                "POST" -> chain.proceed(interceptPost(request))
                else -> chain.proceed(request)
            }
        } catch (e: Exception) {
            Log.e("SignInterceptor", "error:${e.message}", e)
            chain.proceed(chain.request())
        }
    }

    private fun interceptGet(request: Request): Request {
        val httpUrl = request.url
        val timestamp = System.currentTimeMillis().toString()
        val params = getQueryParameters(httpUrl).apply {
            put(APP_SECRET_KEY, APP_SECRET_VALUE)
            put("timestamp", timestamp)
        }
        val newUrlBuilder = httpUrl.newBuilder()
            .addQueryParameter("timestamp", timestamp)
            .addQueryParameter("sign", getSignString(params))
        return request.newBuilder().url(newUrlBuilder.build()).build()
    }

    private fun interceptPost(request: Request): Request {
        when (val body = request.body) {
            is FormBody -> {
                val bodyBuilder = FormBody.Builder(UTF8)
                val timestamp = System.currentTimeMillis().toString()
                val params = getParameters(body).apply {
                    put(APP_SECRET_KEY, APP_SECRET_VALUE)
                    put("timestamp", timestamp)
                }
                params["sign"] = getSignString(params)
                for ((name, value) in params) {
                    if (APP_SECRET_KEY == name) {
                        continue
                    }
                    bodyBuilder.addEncoded(name, value)
                }
                return request.newBuilder().post(bodyBuilder.build()).build()
            }
            is MultipartBody -> {
                val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                val parts = ArrayList<MultipartBody.Part>(body.parts)
                val timestamp = System.currentTimeMillis().toString()
                val params = getParameters(body)
                params.apply {
                    put(APP_SECRET_KEY, APP_SECRET_VALUE)
                    put("timestamp", timestamp)
                }
                parts.add(MultipartBody.Part.createFormData("timestamp", timestamp))
                parts.add(MultipartBody.Part.createFormData("sign", getSignString(params)))
                for (part in parts) {
                    bodyBuilder.addPart(part)
                }
                return request.newBuilder().post(bodyBuilder.build()).build()
            }
            else -> return request
        }
    }

    private fun getSignString(params: TreeMap<String, String>): String {
        val paramString = StringBuilder()
        val sorted = params.toSortedMap { o1, o2 -> o1.compareTo(o2) }
        for ((name, value) in sorted) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "to sign param:($name=${URLDecoder.decode(value, UTF8.name())})")
            }
            paramString.append(URLDecoder.decode(value, UTF8.name()))
        }
        return md5(paramString.toString())
    }
}

/**
 * 移除参数中的无效参数
 */
class NullOrEmptyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val request = chain.request()
            when (request.method) {
                "GET" -> chain.proceed(interceptGet(request))
                "POST" -> chain.proceed(interceptPost(request))
                else -> chain.proceed(request)
            }
        } catch (e: Exception) {
            Log.e("NullOrEmptyInterceptor", "error:${e.message}", e)
            chain.proceed(chain.request())
        }
    }

    private fun interceptGet(request: Request): Request {
        val httpUrl = request.url
        val names = httpUrl.queryParameterNames
        val urlBuilder = httpUrl.newBuilder()
        for (name in names) {
            val value = httpUrl.queryParameter(name)
            if (!value.isNullOrBlank()) {
                urlBuilder.removeAllQueryParameters(name)
            }
        }
        return request.newBuilder().url(urlBuilder.build()).build()
    }

    private fun interceptPost(request: Request): Request {
        when (val body = request.body) {
            is FormBody -> {
                val bodyBuilder = FormBody.Builder(UTF8)
                val params = getParameters(body)
                for ((name, value) in params) {
                    bodyBuilder.addEncoded(name, value)
                }
                return request.newBuilder().post(bodyBuilder.build()).build()
            }
            is MultipartBody -> {
                val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
                val parts = body.parts
                val iterator = parts.iterator()
                //移除掉 FormBody，保留其他类型 Body，比如文件类型
                while (iterator.hasNext()) {
                    val part = iterator.next()
                    val partBody = part.body
                    if (partBody is StringRequestBody && partBody.value.isBlank()) {
                        continue
                    }
                    bodyBuilder.addPart(part)
                }
                return request.newBuilder().post(bodyBuilder.build()).build()
            }
            else -> return request
        }
    }
}