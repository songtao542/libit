package com.liabit.third.weibo

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.liabit.third.ThirdUserRepository
import com.liabit.third.model.ApiResult
import com.liabit.third.model.ApiResult.Companion.ERROR_GET_ACCESS_TOKEN
import com.liabit.third.model.ApiResult.Companion.ERROR_GET_USER_INFO
import com.liabit.third.model.WeiboUser
import com.liabit.viewmodel.ApplicationViewModel
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
open class WeiboAuthViewModel @Inject constructor(
    private val repository: ThirdUserRepository,
    private val restApi: WeiboRestApi
) : ApplicationViewModel() {

    private var mAccessToken: Oauth2AccessToken? = null

    private val mWeiboApi by lazy { WeiboApi(application) }

    val liveResult = MutableLiveData<ApiResult<WeiboUser>>()

    /**
     * #### 启动授权登录，授权结果通过 [liveResult] 通知
     * #### 注意：需要 override 发起请求的Activity的 onActivityResult 方法，并在其中调用 [onActivityResult]
     */
    suspend fun authorize(activity: Activity) {
        var accessToken = mAccessToken ?: withContext(Dispatchers.Default) {
            repository.getWeiboAccessToken()
        }
        if (accessToken == null) { //accessToken == null 说明没有授权过
            authorizeInternal(activity)
        } else { //授权未过期
            getUserInfo(accessToken)
        }
    }

    /**
     * 拉起微博客户端（如果没装客户端，会拉起一个显示微博登录网页的Activity，这个属于微博SDK内部动作），获取 [Oauth2AccessToken]
     * 获取到 access token 之后会调用微博 rest api 获取用户信息
     */
    private suspend fun authorizeInternal(activity: Activity) {
        val result = mWeiboApi.authorize(activity)
        val accessToken = result.result
        if (accessToken != null) {
            getUserInfo(accessToken)
        } else {
            liveResult.postValue(ApiResult(ERROR_GET_ACCESS_TOKEN))
        }
    }

    /**
     * 调用微博 rest api 获取用户信息
     */
    private suspend fun getUserInfo(accessToken: Oauth2AccessToken) {
        val weiboUser = restApi.getUserInfo(accessToken.accessToken, accessToken.uid)
        if (weiboUser == null) {
            liveResult.postValue(ApiResult(ERROR_GET_USER_INFO))
        } else {
            liveResult.postValue(ApiResult(result = weiboUser))
        }
    }

    /**
     * 在发起 [authorize] 的 Activity 的 onActivityResult 中调用此方法
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        mWeiboApi.onActivityResult(activity, requestCode, resultCode, data)
    }
}