package com.liabit.third.qq

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.liabit.third.model.ApiResult
import com.liabit.third.model.ApiResult.Companion.ERROR_GET_ACCESS_TOKEN
import com.liabit.third.model.ApiResult.Companion.ERROR_GET_USER_INFO
import com.liabit.third.model.QQAccessToken
import com.liabit.third.model.QQUser
import com.liabit.viewmodel.ApplicationViewModel
import com.tencent.connect.UserInfo
import com.tencent.tauth.IUiListener
import com.tencent.tauth.UiError
import dagger.hilt.android.lifecycle.HiltViewModel
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
open class QQAuthViewModel @Inject constructor() : ApplicationViewModel() {

    private val mQQApi by lazy { QQApi(application) }

    val liveResult = MutableLiveData<ApiResult<QQUser>>()

    private val mUiListener = object : IUiListener {
        override fun onComplete(response: Any?) {
            val jsonObject = response as? JSONObject
            if (jsonObject == null) {
                liveResult.postValue(ApiResult(ERROR_GET_ACCESS_TOKEN))
                return
            }
            try {
                val accessToken = QQAccessToken.from(jsonObject)
                mQQApi.setAccessToken(accessToken.accessToken, accessToken.expiresIn.toString())
                mQQApi.openId = accessToken.openid
                getUserInfo(accessToken.openid, accessToken.accessToken)
            } catch (e: Throwable) {
                liveResult.postValue(ApiResult(ERROR_GET_ACCESS_TOKEN))
            }
        }

        override fun onCancel() {
            liveResult.postValue(ApiResult(canceled = true))
        }

        override fun onError(error: UiError?) {
            liveResult.postValue(ApiResult.error(error))
        }

        override fun onWarning(code: Int) {
        }
    }

    /**
     * #### 启动QQ授权登录，授权结果通过 [liveResult] 通知
     * #### 注意：需要 override 发起请求的Activity的 onActivityResult 方法，并在其中调用 [onActivityResult]
     */
    suspend fun authorize(activity: Activity) {
        val result = mQQApi.login(activity)
        val accessToken = result.result
        if (accessToken != null) {
            getUserInfo(accessToken.openid, accessToken.accessToken)
        }
    }

    private fun getUserInfo(openId: String, accessToken: String) {
        val userInfo = UserInfo(application, mQQApi.qqToken)
        userInfo.getUserInfo(object : IUiListener {
            override fun onComplete(info: Any?) {
                try {
                    val jsonObject = info as? JSONObject
                    if (jsonObject == null) {
                        liveResult.postValue(ApiResult(ERROR_GET_USER_INFO))
                        return
                    }
                    val qqUser = QQUser.from(openId, accessToken, jsonObject)
                    liveResult.postValue(ApiResult(result = qqUser))
                } catch (e: Throwable) {
                    liveResult.postValue(ApiResult(ERROR_GET_USER_INFO))
                }
            }

            override fun onCancel() {
                liveResult.postValue(ApiResult(canceled = true))
            }

            override fun onError(e: UiError?) {
                liveResult.postValue(ApiResult.error(e))
            }

            override fun onWarning(code: Int) {
            }
        })
    }

    /**
     * 在发起 [authorize] 的 Activity 的 onActivityResult 中调用此方法
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mQQApi.onActivityResult(requestCode, resultCode, data, mUiListener)
    }
}

