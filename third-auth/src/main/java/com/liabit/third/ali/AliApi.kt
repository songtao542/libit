package com.liabit.third.ali

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import com.alipay.sdk.app.AuthTask
import com.alipay.sdk.app.PayTask
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object AliApi {

    private const val TAG = "AliApi"

    @JvmStatic
    suspend fun pay(activity: Activity, orderInfo: String) = suspendCoroutine<Int> {
        try {
            var status = 0
            val alipay = PayTask(activity)
            val result = alipay.payV2(orderInfo, true)
            val payResult = PayResult(result)

            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            //val resultInfo = payResult.result// 同步返回需要验证的信息
            val resultStatus = payResult.resultStatus
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                status = 9000
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                Log.d(TAG, "支付失败, payResult: $payResult")
            }
            it.resume(status)
        } catch (e: Throwable) {
            Log.d(TAG, "支付失败, error: ", e)
            it.resume(0)
        }
    }

    @JvmStatic
    suspend fun auth(activity: Activity, sign: String) = suspendCoroutine<Int> {
        try {
            var status = 0
            //var authCode: String? = null
            // 构造AuthTask 对象
            val authTask = AuthTask(activity)
            // 调用授权接口，获取授权结果
            val result = authTask.authV2(sign, true)
            val authResult = AuthResult(result, true)
            val resultStatus = authResult.resultStatus
            // 判断resultStatus 为 9000 且result_code为 200 则代表授权成功，具体状态码代表含义可参考授权接口文档
            if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.resultCode, "200")) {
                // 获取alipay_open_id，调支付时作为参数extern_token 的 value 传入，则支付账户为该授权账户
                status = 9000
                //authCode = authResult.authCode
            } else {
                // 其他状态值则为授权失败
                Log.d(TAG, "授权失败, authResult: $authResult")
            }
            it.resume(status)
        } catch (e: Throwable) {
            Log.d(TAG, "授权失败, error: ", e)
            it.resume(0)
        }
    }
}

class PayResult(rawResult: Map<String, String>) {
    var resultStatus: String? = null
    var result: String? = null
    var memo: String? = null

    init {
        for (key in rawResult.keys) {
            when {
                TextUtils.equals(key, "resultStatus") -> resultStatus = rawResult[key]
                TextUtils.equals(key, "result") -> result = rawResult[key]
                TextUtils.equals(key, "memo") -> memo = rawResult[key]
            }
        }
    }

    override fun toString(): String {
        return "resultStatus={$resultStatus};memo={$memo};result={$result}"
    }
}

class AuthResult(rawResult: Map<String, String>, removeBrackets: Boolean) {

    private var result: String? = null
    var alipayOpenId: String? = null
    var resultStatus: String? = null
    var resultCode: String? = null
    var authCode: String? = null
    var memo: String? = null

    init {
        for (key in rawResult.keys) {
            when {
                TextUtils.equals(key, "resultStatus") -> resultStatus = rawResult[key]
                TextUtils.equals(key, "result") -> result = rawResult[key]
                TextUtils.equals(key, "memo") -> memo = rawResult[key]
            }
        }
        result?.let { result ->
            val resultValue = result.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (value in resultValue) {
                when {
                    value.startsWith("alipay_open_id") -> alipayOpenId = removeBrackets(getValue("alipay_open_id=", value), removeBrackets)
                    value.startsWith("auth_code") -> authCode = removeBrackets(getValue("auth_code=", value), removeBrackets)
                    value.startsWith("result_code") -> resultCode = removeBrackets(getValue("result_code=", value), removeBrackets)
                }
            }
        }
    }

    private fun removeBrackets(origin: String, remove: Boolean): String {
        var str = origin
        if (remove) {
            if (!TextUtils.isEmpty(str)) {
                if (str.startsWith("\"")) {
                    str = str.replaceFirst("\"".toRegex(), "")
                }
                if (str.endsWith("\"")) {
                    str = str.substring(0, str.length - 1)
                }
            }
        }
        return str
    }

    private fun getValue(header: String, data: String): String {
        return data.substring(header.length, data.length)
    }

    override fun toString(): String {
        return "authCode={$authCode}; resultStatus={$resultStatus}; memo={$memo}; result={$result}"
    }
}