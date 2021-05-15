package com.liabit.third.wx

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.liabit.third.ThirdUserRepository
import com.liabit.third.model.ApiResult
import com.liabit.third.model.WxAccessToken
import com.liabit.third.model.WxUser
import com.liabit.viewmodel.ApplicationViewModel
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import javax.inject.Inject

@HiltViewModel
open class WxAuthViewModel @Inject constructor(
    private val repository: ThirdUserRepository,
    private val wxRestApi: WxRestApi
) : ApplicationViewModel(), IWXAPIEventHandler {

    private val mWechatApi: WxApi by lazy { WxApi(application) }
    private var mAccessToken: WxAccessToken? = null

    private var mEventHandler: WeakReference<IWXAPIEventHandler>? = null

    /**
     * 微信登录结果
     */
    val liveResult = MutableLiveData<ApiResult<WxUser>>()

    /**
     * #### 微信认证，认证结果通过 [liveResult] 通知
     * #### 注意：需要在 AndroidManifest.xml 中注册 [WxEntryActivity], 请求的结果会通过该 Activity 返回
     */
    suspend fun authorize() {
        val accessToken = mAccessToken ?: withContext(Dispatchers.Default) {
            repository.getWxAccessToken()
        }
        val openid = accessToken?.openid
        val token = accessToken?.accessToken
        // 如果 AccessToken == null 或者 Session 过期，则调用 authorize 方法
        if (openid.isNullOrBlank() || token.isNullOrBlank() || !accessToken.isSessionValid()) {
            /**
             *  调用结果会通过 [IWXAPIEventHandler] 回调通知，即 [onReq], [onResp] 方法
             */
            mWechatApi.authorize()
        } else {
            // 如果 AccessToken 有效，则直接请求 WxUser
            getWxUser(openid, token, accessToken.refreshToken)
        }
    }

    /**
     *  authorize 方法结果回调
     *
     * implementation of [IWXAPIEventHandler.onReq]
     */
    override fun onReq(req: BaseReq?) {
        mEventHandler?.get()?.onReq(req)
    }

    /**
     *  authorize 方法结果回调
     *
     *  implementation of [IWXAPIEventHandler.onResp]
     */
    override fun onResp(resp: BaseResp?) {
        mEventHandler?.get()?.onResp(resp)
        if (resp != null && resp.errCode == BaseResp.ErrCode.ERR_OK) {
            //val openId = resp.openId
            val code = (resp as SendAuth.Resp).code
            getAccessTokenAndUserInfo(code)
        }
    }

    /**
     * 通过微信客户端返回的 code 调用微信 rest api 获取 access token
     */
    private fun getAccessTokenAndUserInfo(code: String) {
        launch {
            // 先获取 AccessToken，再根据 AccessToken 获取用户信息
            val grantType = "authorization_code"
            // 异步调用 rest api 获取 access token
            val accessToken = withContext(Dispatchers.Default) {
                wxRestApi.getAccessToken(code, grantType)
            }
            val openid = accessToken?.openid
            val token = accessToken?.accessToken
            if (token.isNullOrBlank() || openid.isNullOrBlank()) {
                liveResult.postValue(ApiResult(code = ApiResult.ERROR_GET_ACCESS_TOKEN))
            } else {
                // 保存 access token
                mAccessToken = accessToken
                repository.setWxAccessToken(accessToken)

                // 通过 access token 获取 WxUser
                getWxUser(openid, token, accessToken.refreshToken)
            }
        }
    }

    /**
     * 请求微信 rest api 获取用户信息
     */
    private suspend fun getWxUser(openid: String, token: String, refreshToken: String?) {
        /**
         * {
         *     "openid": "o4PN51DasTTHB1Ieds6NIC5AM5OA",
         *     "nickname": "songtao",
         *     "sex": 1,
         *     "language": "en",
         *     "city": "",
         *     "province": "",
         *     "country": "",
         *     "headimgurl": "http://thirdwx.qlogo.cn/mmopen/vi_32/7KaRIUuOoUrZDcmvibZNia8kDCsfPiaO2bt5NwgCZaic17hiafVDOneG5SVSE1cniaf5a1Om5ia0x4oD21LNFBMbdd28Q/132",
         *     "privilege": [],
         *     "unionid": "ocGSv5_JyViQROMku0z65_AAg1x8"
         *  }
         */
        // 异步调用微信 rest api 获取 WxUser
        val wxUser = withContext(Dispatchers.Default) {
            wxRestApi.getWxUser(openid, token)
        }
        if (wxUser == null) {
            liveResult.postValue(ApiResult(code = ApiResult.ERROR_GET_USER_INFO))
        } else {
            // 保存 WxUser
            wxUser.accessToken = token
            wxUser.refreshToken = refreshToken
            repository.setWxUser(wxUser)

            liveResult.postValue(ApiResult(result = wxUser))
        }
    }

    /**
     * 在 Activity 的 onCreate 或者 onNewIntent 中调用此方法
     */
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