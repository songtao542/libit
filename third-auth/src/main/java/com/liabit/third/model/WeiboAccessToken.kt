package com.liabit.third.model

import android.os.Parcelable
import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeiboAccessToken(
    @SerializedName("access_token") var accessToken: String? = null,//  "access_token":"ACCESS_TOKEN",
    @SerializedName("expires_in") var expiresIn: Int = 0,// "expires_in":7200,
    @SerializedName("remind_in") var remindIn: String? = null,//  "refresh_token":"REFRESH_TOKEN",
    @SerializedName("uid") var uid: String? = null,//  "refresh_token":"REFRESH_TOKEN",
    var expiresTime: Long = 0
) : Parcelable {

    init {
        expiresTime = System.currentTimeMillis() + expiresIn
    }

    fun isSessionValid(): Boolean {
        return !TextUtils.isEmpty(accessToken) && System.currentTimeMillis() < expiresTime
    }
}