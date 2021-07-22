package com.scaffold.network.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName

@Suppress("MemberVisibilityCanBePrivate")
data class Response<T>(
    val data: T? = null,
    val code: Int? = null,
    val message: String? = null,
) {

    companion object {
        fun <T> error(): Response<T> {
            return Response(code = -1)
        }

        fun <T> error(msg: String): Response<T> {
            return Response(code = -1, message = msg)
        }
    }

    val isMessageNotEmpty: Boolean get() = !TextUtils.isEmpty(message)

    val success: Boolean get() = code == 200

    /**
     * 针对返回List的请求才有作用
     */
    val isEmpty: Boolean
        get() {
            var result = true
            if (success) {
                result = (data as? List<*>)?.isEmpty() ?: false
            }
            return result
        }

    /**
     * 针对返回List的请求才有作用
     */
    val isNotEmpty: Boolean get() = !isEmpty
}