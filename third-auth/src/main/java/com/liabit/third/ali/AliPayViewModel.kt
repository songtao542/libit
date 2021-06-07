package com.liabit.third.ali

import android.app.Activity
import com.liabit.viewmodel.ApplicationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class AliPayViewModel @Inject constructor() : ApplicationViewModel() {

    /**
     * @param orderInfo 该字段通过调用服务端接口生成，因为放在本地生成不安全，且生成该字段时需要商户id,订单id等信息
     */
    suspend fun pay(activity: Activity, orderInfo: String): Int {
        return AliApi.pay(activity, orderInfo)
    }

    /**
     * @param sign 该字段通过调用服务端接口生成，服务端会与阿里服务端通信生成该字段
     */
    suspend fun authorize(activity: Activity, sign: String): Int {
        return AliApi.auth(activity, sign)
    }

}