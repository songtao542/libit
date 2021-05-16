package com.liabit.third.weibo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.ApiResult
import com.sina.weibo.sdk.api.*
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.common.UiError
import com.sina.weibo.sdk.openapi.IWBAPI
import com.sina.weibo.sdk.openapi.WBAPIFactory
import com.sina.weibo.sdk.share.WbShareCallback
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 *  请在 AndroidManifest.xml 添加
 *  <activity
 *      android:name="com.sina.weibo.sdk.web.WeiboSdkWebActivity"
 *      android:configChanges="keyboardHidden|orientation"
 *      android:exported="false"
 *      android:screenOrientation="portrait"
 *      android:windowSoftInputMode="adjustResize"
 *      tools:node="replace" />
 */
class WeiboApi constructor(context: Context) {
    private val mAuthApiMap by lazy { WeakHashMap<Activity, IWBAPI>() }
    private val mShareApiMap by lazy { WeakHashMap<Activity, ShareApiAndCallback>() }
    private val mAuthInfo = AuthInfo(context.applicationContext, ThirdAppInfo.WEIBO_APP_KEY, ThirdAppInfo.WEIBO_REDIRECT_URL, ThirdAppInfo.WEIBO_SCOPE)

    /**
     * #### 拉起微博客户端（如果没装客户端，会拉起一个显示微博登录网页的Activity，这个属于微博SDK内部动作），获取 [Oauth2AccessToken]
     * #### 注意：请在 Activity 的 onActivityResult 方法中调用 [onActivityResult]
     */
    suspend fun authorize(activity: Activity): ApiResult<Oauth2AccessToken> {
        val wbApi = WBAPIFactory.createWBAPI(activity)
        wbApi.registerApp(activity, mAuthInfo)
        mAuthApiMap[activity] = wbApi
        return authorize(wbApi)
    }

    /**
     * 回调转同步
     */
    private suspend fun authorize(wbApi: IWBAPI) = suspendCoroutine<ApiResult<Oauth2AccessToken>> {
        wbApi.authorize(object : WbAuthListener {
            override fun onComplete(result: Oauth2AccessToken?) {
                it.resume(ApiResult(result = result))
            }

            override fun onError(error: UiError?) {
                it.resume(ApiResult.error(error))
            }

            override fun onCancel() {
                it.resume(ApiResult(canceled = true))
            }
        })
    }

    /**
     * 微博客户端（或显示微博登录网页的Activity）的返回结果需要通过该方法进行处理，
     * 所以必须在发起授权的Activity的 onActivityResult 方法中调用该方法
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        //SSO 授权回调 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        val authApi = mAuthApiMap[activity]
        if (authApi != null) {
            authApi.authorizeCallback(requestCode, resultCode, data)
            mAuthApiMap.remove(activity)
        }
        val shareApi = mShareApiMap[activity]
        if (shareApi != null) {
            shareApi.api.doResultIntent(data, shareApi.callback)
            mShareApiMap.remove(activity)
        }
    }

    inner class ShareApiAndCallback(val api: IWBAPI, val callback: WbShareCallback?) : WbShareCallback {
        override fun onComplete() {
            callback?.onComplete()
        }

        override fun onError(error: UiError?) {
            callback?.onError(error)
        }

        override fun onCancel() {
            callback?.onCancel()
        }
    }

    /**
     * 分享URL，只通过客户端分享，如果没装客户端，则无法分享
     */
    fun share(
        activity: Activity,
        title: String,
        description: String,
        url: String,
        text: String,
        @DrawableRes iconToShare: Int,
        listener: WbShareCallback? = null
    ): Boolean {
        if (!isWeiboInstalled(activity)) {
            return false
        }
        val wbApi = WBAPIFactory.createWBAPI(activity)
        wbApi.registerApp(activity, mAuthInfo)
        mShareApiMap[activity] = ShareApiAndCallback(wbApi, listener)
        val bitmap = BitmapFactory.decodeResource(activity.resources, iconToShare)
        val weiboMessage = WeiboMultiMessage()
        weiboMessage.mediaObject = WebpageObject().apply {
            this.identify = UUID.randomUUID().toString()
            this.title = title
            this.description = description
            this.thumbData = bitmapToByteArray(bitmap)
            this.actionUrl = url
            this.defaultText = text
        }
        weiboMessage.textObject = TextObject().apply {
            this.text = text
            this.title = title
            this.actionUrl = url
        }
        weiboMessage.imageObject = ImageObject().apply {
            this.imageData = bitmapToByteArray(bitmap)
        }
        wbApi.shareMessage(weiboMessage, true)
        return true
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray? {
        try {
            ByteArrayOutputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                return it.toByteArray()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun isWeiboInstalled(context: Context): Boolean {
        val packageManager = context.packageManager
        val installedList = packageManager.getInstalledPackages(0)
        for (app in installedList) {
            val packageName = app.packageName
            if (packageName == "com.sina.weibo") {
                return true
            }
        }
        return false
    }

}