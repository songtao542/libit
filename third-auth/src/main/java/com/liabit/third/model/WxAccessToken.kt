package com.liabit.third.model

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WxAccessToken(
    @SerializedName("access_token") var accessToken: String? = null,//  "access_token":"ACCESS_TOKEN",
    @SerializedName("expires_in") var expiresIn: Int = 0,// "expires_in":7200,
    @SerializedName("refresh_token") var refreshToken: String? = null,//  "refresh_token":"REFRESH_TOKEN",
    var openid: String? = null,//  "openid":"OPENID",
    var scope: String? = null,//  "scope":"SCOPE",
    var unionid: String? = null, // "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL"
    var expiresTime: Long = 0
) : Parcelable {

    init {
        expiresTime = System.currentTimeMillis() + expiresIn
    }

    fun isSessionValid(): Boolean {
        return !TextUtils.isEmpty(accessToken) && System.currentTimeMillis() < expiresTime
    }
}