package com.liabit.third.model

import com.sina.weibo.sdk.auth.WbConnectErrorMessage
import com.sina.weibo.sdk.exception.WeiboException
import com.tencent.tauth.UiError

data class ApiResult<T>(
    val result: T? = null,
    val error: Error? = null,
    val canceled: Boolean = false
) {

    constructor(code: Int) : this(null, Error(code = code), false)

    val success: Boolean get() = result != null

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        const val ERROR_GET_ACCESS_TOKEN = 1
        const val ERROR_GET_USER_INFO = 2
        const val ERROR_UNKNOWN = 3

        fun <T> error(error: UiError?): ApiResult<T> {
            return ApiResult(error = error?.toError() ?: Error(code = ERROR_UNKNOWN))
        }

        fun <T> error(error: WbConnectErrorMessage?): ApiResult<T> {
            return ApiResult(error = error?.toError() ?: Error(code = ERROR_UNKNOWN))
        }

        fun <T> error(error: WeiboException?): ApiResult<T> {
            return ApiResult(error = error?.toError() ?: Error(code = ERROR_UNKNOWN))
        }

        fun <T> error(error: Throwable?): ApiResult<T> {
            return ApiResult(error = Error(code = ERROR_UNKNOWN, message = error?.message))
        }
    }
}

data class Error(
    val code: Int? = null,
    val message: String? = null
)

private fun UiError.toError(): Error {
    return Error(code = errorCode, message = errorMessage)
}

private fun WbConnectErrorMessage.toError(): Error {
    return Error(code = errorCode?.toIntOrNull(), message = errorMessage)
}

private fun WeiboException.toError(): Error {
    return Error(code = ApiResult.ERROR_GET_ACCESS_TOKEN, message = message)
}