package com.liabit.third.wx

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.liabit.third.model.WxPrePayInfo
import com.liabit.viewmodel.ApplicationViewModel
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
open class WxPayViewModel @Inject constructor() : ApplicationViewModel(), IWXAPIEventHandler {

    private val mWechatApi: WxApi by lazy { WxApi(application) }

    private var mEventHandler: WeakReference<IWXAPIEventHandler>? = null

    val livePayResult = MutableLiveData<Boolean>()

    /**
     * 预支付订单信息，需要服务器端调用微信服务端相关api生成
     * App ---> 服务端 --> 微信服务端
     *  ∧         |∧         |
     *  |         ||         |
     *  ╰┈┈┈┈┈┈┈┈┈╯╰┈┈┈┈┈┈┈┈┈╯
     *
     *  #### 拉起微信客户端支付，支付结果通过 [IWXAPIEventHandler] 通知，即 [onReq], [onResp]
     *
     *  #### 支付的结果通过 [livePayResult] 通知
     *
     *  #### 注意：需要在 AndroidManifest.xml 中注册 [WxPayEntryActivity], 请求的结果会通过该 Activity 返回
     */
    private fun payByWechat(prePayInfo: WxPrePayInfo) {
        mWechatApi.pay(prePayInfo)
    }

    /**
     * implementation of [IWXAPIEventHandler.onReq]
     */
    override fun onReq(req: BaseReq) {
        mEventHandler?.get()?.onReq(req)
    }

    /**
     * implementation of [IWXAPIEventHandler.onResp]
     */
    override fun onResp(resp: BaseResp?) {
        mEventHandler?.get()?.onResp(resp)
        if (resp != null && resp.type == ConstantsAPI.COMMAND_PAY_BY_WX && resp.errCode == 0) {
            livePayResult.value = true
        }
    }

    fun handleIntent(intent: Intent, eventHandler: IWXAPIEventHandler? = null): Boolean {
        return try {
            mEventHandler = WeakReference(eventHandler)
            mWechatApi.handleIntent(intent, this)
        } catch (e: Throwable) {
            mEventHandler?.clear()
            mEventHandler = null
            false
        }
    }
}
