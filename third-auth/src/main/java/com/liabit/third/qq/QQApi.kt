package com.liabit.third.qq

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.annotation.DrawableRes
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.ApiResult
import com.liabit.third.model.ApiResult.Companion.ERROR_GET_ACCESS_TOKEN
import com.liabit.third.model.QQAccessToken
import com.tencent.connect.auth.QQToken
import com.tencent.connect.share.QQShare
import com.tencent.connect.share.QzoneShare
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 *  请在 AndroidManifest.xml 添加
 *  <activity
 *      android:name="com.tencent.tauth.AuthActivity"
 *      android:launchMode="singleTask"
 *      android:noHistory="true"
 *      android:screenOrientation="portrait">
 *      <intent-filter>
 *          <action android:name="android.intent.action.VIEW" />
 *          <category android:name="android.intent.category.DEFAULT" />
 *          <category android:name="android.intent.category.BROWSABLE" />
 *          <data android:scheme="1107903594" />
 *      </intent-filter>
 *  </activity>
 *
 *  <activity
 *      android:name="com.tencent.connect.common.AssistActivity"
 *      android:configChanges="orientation|keyboardHidden"
 *      android:screenOrientation="behind"
 *      android:theme="@android:style/Theme.Translucent.NoTitleBar" />
 */
class QQApi constructor(context: Context) {

    private val mContext = context.applicationContext

    private val mTencent by lazy { Tencent.createInstance(ThirdAppInfo.TENCENT_APP_ID, mContext) }

    /**
     * #### 启动QQ授权
     * #### 注意：需要在 AndroidManifest.xml 中注册 [com.tencent.tauth.AuthActivity] 和 [com.tencent.connect.common.AssistActivity]
     */
    suspend fun login(activity: Activity) = suspendCoroutine<ApiResult<QQAccessToken>> {
        try {
            mTencent.login(activity, "all", object : IUiListener {
                override fun onComplete(result: Any?) {
                    val jsonObject = result as? JSONObject
                    if (jsonObject == null) {
                        it.resume(ApiResult(ERROR_GET_ACCESS_TOKEN))
                        return
                    }
                    try {
                        val accessToken = QQAccessToken.from(jsonObject)
                        setAccessToken(accessToken.accessToken, accessToken.expiresIn.toString())
                        openId = accessToken.openid
                        it.resume(ApiResult(result = accessToken))
                    } catch (e: Throwable) {
                        it.resume(ApiResult.error(e))
                    }
                }

                override fun onError(error: UiError?) {
                    it.resume(ApiResult.error(error))
                }

                override fun onCancel() {
                    it.resume(ApiResult(canceled = true))
                }

                override fun onWarning(code: Int) {
                }
            })
        } catch (e: Throwable) {
            it.resume(ApiResult.error(e))
        }
    }

    fun setAccessToken(accessToken: String, expiresIn: String) {
        try {
            mTencent.setAccessToken(accessToken, expiresIn)
        } catch (e: Throwable) {
            Log.d("QQApi", "setAccessToken error: ", e)
        }
    }

    var openId: String?
        get() = mTencent.openId
        set(value) {
            mTencent.openId = value
        }

    val qqToken: QQToken? get() = mTencent.qqToken

    suspend fun shareUrl(
        activity: Activity,
        title: String,
        summary: String,
        url: String
    ): ApiResult<Any> {
        val shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT
        val params = Bundle()
        val flag = QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN or QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE
        params.putString(QQShare.SHARE_TO_QQ_TITLE, title)
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url)
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary)
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, getApplicationLabel())
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType)
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, flag)
        return shareToQQ(activity, params)
    }

    /**
     * 回调转同步
     */
    private suspend fun shareToQQ(activity: Activity, params: Bundle) = suspendCoroutine<ApiResult<Any>> {
        mTencent.shareToQQ(activity, params, object : IUiListener {
            override fun onComplete(result: Any?) {
                it.resume(ApiResult(result = result))
            }

            override fun onError(error: UiError?) {
                it.resume(ApiResult.error(error))
            }

            override fun onCancel() {
                it.resume(ApiResult(canceled = true))
            }

            override fun onWarning(code: Int) {
            }
        })
    }

    suspend fun shareQzoneUrl(
        activity: Activity,
        title: String,
        summary: String,
        urlToShare: String,
        @DrawableRes iconToShare: Int? = null
    ): ApiResult<Any> {
        val shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT
        val params = Bundle()
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType)
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title)
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary)
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, urlToShare)
        val imagePathList = ArrayList<String>()
        val iconPath = withContext(Dispatchers.IO) {
            saveLauncherBitmap(iconToShare)
        }
        if (!iconPath.isNullOrBlank()) {
            imagePathList.add(iconPath)
        }
        if (imagePathList.isNotEmpty()) {
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imagePathList)
        }
        return shareToQzone(activity, params)
    }

    /**
     * 回调转同步
     */
    private suspend fun shareToQzone(activity: Activity, params: Bundle) = suspendCoroutine<ApiResult<Any>> {
        mTencent.shareToQzone(activity, params, object : IUiListener {
            override fun onComplete(result: Any?) {
                it.resume(ApiResult(result = result))
            }

            override fun onError(error: UiError?) {
                it.resume(ApiResult.error(error))
            }

            override fun onCancel() {
                it.resume(ApiResult(canceled = true))
            }

            override fun onWarning(code: Int) {
            }
        })
    }

    private fun saveLauncherBitmap(@DrawableRes iconToShare: Int? = null): String? {
        var out: OutputStream? = null
        try {
            val file = File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ic_launcher.jpg")
            if (!file.exists()) {
                file.createNewFile()
            }
            out = FileOutputStream(file)
            val bitmap = if (iconToShare != null) {
                BitmapFactory.decodeResource(mContext.resources, iconToShare)
            } else {
                getApplicationIcon()
            }
            bitmap?.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            return file.absolutePath
        } catch (e: Throwable) {
            Log.e("QQApi", "save bitmap error: ", e)
        } finally {
            try {
                out?.close()
            } catch (e: Throwable) {
            }
        }
        return null
    }

    private fun getApplicationLabel(): String {
        val packageManager = mContext.applicationContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(mContext.packageName, 0)
        return packageManager.getApplicationLabel(applicationInfo).toString()
    }

    private fun getApplicationIcon(): Bitmap? {
        try {
            val packageManager = mContext.applicationContext.packageManager
            val applicationInfo = packageManager.getApplicationInfo(mContext.packageName, 0)
            val icon = packageManager.getApplicationIcon(applicationInfo)
            return (icon as? BitmapDrawable)?.bitmap
        } catch (e: Exception) {
        }
        return null
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, listener: IUiListener) {
        Tencent.onActivityResultData(requestCode, resultCode, data, listener)
    }
}