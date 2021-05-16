package com.liabit.third.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeiboAccessTokenInfo(
    var uid: String? = null,//  授权用户的uid
    var appkey: String? = null,//  access_token所属的应用appkey
    var scope: String? = null,//  用户授权的scope权限
    @SerializedName("create_at") var createAt: String? = null,//  access_token的创建时间，从1970年到创建时间的秒数
    @SerializedName("expire_in") var expireIn: String? = null,//  access_token的剩余时间，单位是秒数，如果返回的时间是负数，代表授权已经过期
    var expiresTime: Long = 0
) : Parcelable