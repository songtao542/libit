package com.liabit.third.weibo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.ApiResult
import com.sina.weibo.sdk.WbSdk
import com.sina.weibo.sdk.api.ImageObject
import com.sina.weibo.sdk.api.TextObject
import com.sina.weibo.sdk.api.WebpageObject
import com.sina.weibo.sdk.api.WeiboMultiMessage
import com.sina.weibo.sdk.auth.AuthInfo
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WbAuthListener
import com.sina.weibo.sdk.auth.WbConnectErrorMessage
import com.sina.weibo.sdk.auth.sso.SsoHandler
import com.sina.weibo.sdk.share.WbShareHandler
import com.sina.weibo.sdk.utils.Utility
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

    private val mSsoHandlerMap = WeakHashMap<Activity, SsoHandler>()

    init {
        val applicationContext = context.applicationContext
        WbSdk.install(applicationContext, AuthInfo(applicationContext, ThirdAppInfo.WEIBO_APP_KEY, ThirdAppInfo.WEIBO_REDIRECT_URL, ThirdAppInfo.WEIBO_SCOPE))
    }

    /**
     * #### 拉起微博客户端（如果没装客户端，会拉起一个显示微博登录网页的Activity，这个属于微博SDK内部动作），获取 [Oauth2AccessToken]
     * #### 注意：请在 [activity] 的 onActivityResult 方法中调用 [onActivityResult]
     * #### 注意：需要在 AndroidManifest.xml 中注册 [com.sina.weibo.sdk.web.WeiboSdkWebActivity]
     */
    suspend fun authorize(activity: Activity): ApiResult<Oauth2AccessToken> {
        val ssoHandler = SsoHandler(activity)
        mSsoHandlerMap[activity] = ssoHandler
        return authorize(ssoHandler)
    }

    /**
     * 回调转同步
     */
    private suspend fun authorize(ssoHandler: SsoHandler) = suspendCoroutine<ApiResult<Oauth2AccessToken>> {
        ssoHandler.authorize(object : WbAuthListener {
            override fun onSuccess(result: Oauth2AccessToken?) {
                it.resume(ApiResult(result = result))
            }

            override fun cancel() {
                it.resume(ApiResult(canceled = true))
            }

            override fun onFailure(error: WbConnectErrorMessage?) {
                it.resume(ApiResult.error(error))
            }
        })
    }

    /**
     * 微博客户端（或显示微博登录网页的Activity）的返回结果需要通过该方法进行处理，
     * 所以必须在发起授权的Activity的 onActivityResult 方法中调用该方法
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        //SSO 授权回调 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        mSsoHandlerMap[activity]?.authorizeCallBack(requestCode, resultCode, data)
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
        @DrawableRes iconToShare: Int
    ): Boolean {
        if (!isWeiboInstalled(activity)) {
            return false
        }
        val shareHandler = WbShareHandler(activity).apply {
            registerApp()
            setProgressColor(0xffffff)
        }
        val bitmap = BitmapFactory.decodeResource(activity.resources, iconToShare)
        val weiboMessage = WeiboMultiMessage()
        weiboMessage.mediaObject = WebpageObject().apply {
            this.identify = Utility.generateGUID()
            this.title = title
            this.description = description
            this.setThumbImage(bitmap)
            this.actionUrl = url
            this.defaultText = text
        }
        weiboMessage.textObject = TextObject().apply {
            this.text = text
            this.title = title
            this.actionUrl = url
        }
        weiboMessage.imageObject = ImageObject().apply {
            this.setImageObject(bitmap)
        }
        shareHandler.shareMessage(weiboMessage, true)
        return true
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