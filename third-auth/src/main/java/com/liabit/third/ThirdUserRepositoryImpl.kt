package com.liabit.third

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.liabit.third.model.WxAccessToken
import com.liabit.third.model.WxUser
import com.sina.weibo.sdk.auth.Oauth2AccessToken

/**
 * SharedPreferences impl
 */
class ThirdUserRepositoryImpl(context: Context) : ThirdUserRepository {

    companion object {
        private const val TAG = "ThirdUserRepositoryImpl"
    }

    private val mContext: Context = context.applicationContext

    private val mPreference by lazy { mContext.getSharedPreferences("third", Context.MODE_PRIVATE) }

    private val mGson by lazy { Gson() }

    override suspend fun getWxAccessToken(): WxAccessToken? {
        return try {
            val wxToken = mPreference.getString("wxt", null)
            mGson.fromJson(wxToken, WxAccessToken::class.java)
        } catch (e: Throwable) {
            Log.d(TAG, "getWxAccessToken error: ", e)
            null
        }
    }

    override suspend fun setWxAccessToken(accessToken: WxAccessToken) {
        mPreference.edit {
            putString("wxt", mGson.toJson(accessToken))
        }
    }

    override suspend fun getWxUser(accessToken: String, openid: String): WxUser? {
        return try {
            val wxUser = mPreference.getString("wxu", null)
            mGson.fromJson(wxUser, WxUser::class.java)
        } catch (e: Throwable) {
            Log.d(TAG, "getWxUser error: ", e)
            null
        }
    }

    override suspend fun setWxUser(wxUser: WxUser) {
        mPreference.edit {
            putString("wxu", mGson.toJson(wxUser))
        }
    }

    override suspend fun getWeiboAccessToken(): Oauth2AccessToken? {
        return try {
            val weiboToken = mPreference.getString("wbt", null)
            mGson.fromJson(weiboToken, Oauth2AccessToken::class.java)
        } catch (e: Throwable) {
            Log.d(TAG, "getWeiboAccessToken error: ", e)
            null
        }
    }
}