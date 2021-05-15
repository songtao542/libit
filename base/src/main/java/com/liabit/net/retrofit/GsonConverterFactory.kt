package com.liabit.net.retrofit

import android.util.Log
import androidx.annotation.IntDef
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.OutputStreamWriter
import java.io.Writer
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

/**
 * A [converter][Converter.Factory] which uses Gson for JSON.
 *
 * Because Gson is so flexible in the types it supports, this converter assumes that it can handle
 * all types. If you are mixing JSON serialization with something else (such as protocol buffers),
 * you must [add this instance][Retrofit.Builder.addConverterFactory]
 * last to allow the other converters a chance to see their types.
 */
class GsonConverterFactory private constructor(
    private val type: Int,
    private val gson: Gson,
    private val errorJson: String
) : Converter.Factory() {

    companion object {

        const val JSON = 0
        const val FROM = 1

        /**
         * Create an instance using {@code gson} for conversion. Encoding to JSON and
         * decoding from JSON (when no charset is specified by a header) will use UTF-8.
         */
        fun create(@ContentType type: Int, gson: Gson, errorJson: String): GsonConverterFactory {
            return GsonConverterFactory(type, gson, errorJson)
        }

        fun create(gson: Gson = Gson(), errorJson: String): GsonConverterFactory {
            return create(JSON, gson, errorJson)
        }

        fun create(errorJson: String): GsonConverterFactory {
            return create(FROM, Gson(), errorJson)
        }
    }

    @IntDef(FROM, JSON)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ContentType

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonResponseBodyConverter(gson, adapter, errorJson)
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<Annotation>,
        methodAnnotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody> {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return GsonRequestBodyConverter(this.type, gson, adapter)
    }
}

/**
 * 请求数据转换器
 */
internal class GsonRequestBodyConverter<T>(
    @GsonConverterFactory.ContentType private val type: Int,
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<T, RequestBody> {

    companion object {
        // 发送 application/json 类型数据
        private val MEDIA_TYPE_JSON = "application/json; charset=UTF-8".toMediaTypeOrNull()

        // 发送 application/x-www-form-urlencoded 表单数据
        private val MEDIA_TYPE_FORM = "application/x-www-form-urlencoded; charset=UTF-8".toMediaTypeOrNull()
        private val UTF_8 = StandardCharsets.UTF_8
    }

    override fun convert(value: T): RequestBody {
        val buffer = Buffer()
        val writer: Writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val mediaType = if (type == GsonConverterFactory.JSON) {
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            MEDIA_TYPE_JSON
        } else {
            writer.write(value.toString())
            writer.close()
            MEDIA_TYPE_FORM
        }
        return buffer.readByteString().toRequestBody(mediaType)
    }
}

/**
 * 请求结果转换器
 */
internal class GsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>,
    private val errorJson: String
) : Converter<ResponseBody, T> {

    override fun convert(value: ResponseBody): T {
        return try {
            val jsonReader = gson.newJsonReader(value.charStream())
            return adapter.read(jsonReader)
        } catch (e: Throwable) {
            Log.e("GsonConverter", "error: ", e)
            adapter.fromJson(errorJson)
        } finally {
            try {
                value.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}