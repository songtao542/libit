package com.liabit.third.model

import android.os.Parcelable
import org.json.JSONObject
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * {
 *      "ret": 0,
 *      "openid": "73C9975B7201C4D8418C6B96FCD8522D",
 *      "access_token": "179A354608B0741924C1E32E280D9DB8",
 *      "pay_token": "F85D0B541E21790223F3A4A4E0CFA92D",
 *      "expires_in": 7776000,
 *      "code": "",
 *      "proxy_code": "",
 *      "proxy_expires_in": 0,
 *      "pf": "desktop_m_qq-10000144-android-2002-",
 *      "pfkey": "c04c9fd8f7bd1271591a649a7a0fbfeb",
 *      "msg": "",
 *      "login_cost": 24,
 *      "query_authority_cost": -1977983589,
 *      "authority_cost": 0,
 *      "expires_time": 1627194860320
 * }
 */
@Parcelize
data class QQAccessToken(
    var openid: String,
    @SerializedName("access_token") var accessToken: String,
    @SerializedName("expires_in") var expiresIn: Int,
    @SerializedName("authority_cost") var authorityCost: Int? = null,
    var code: String? = null,
    @SerializedName("expires_time") var expiresTime: Long? = null,
    @SerializedName("login_cost") var loginCost: Int? = null,
    var msg: String? = null,
    @SerializedName("pay_token") var payToken: String? = null,
    var pf: String? = null,
    var pfkey: String? = null,
    @SerializedName("proxy_code") var proxyCode: String? = null,
    @SerializedName("proxy_expires_in") var proxyExpiresIn: Int? = null,
    @SerializedName("query_authority_cost") var queryAuthorityCost: Int? = null,
    var ret: Int? = null
) : Parcelable {
    companion object {
        @Throws
        fun from(jsonObject: JSONObject): QQAccessToken {
            try {
                val openid = jsonObject.getString("openid")
                val token = jsonObject.getString("access_token")
                val expiresIn = jsonObject.getInt("expires_in")
                val accessToken = QQAccessToken(openid, token, expiresIn)
                accessToken.authorityCost = jsonObject.getInt("authority_cost")
                accessToken.code = jsonObject.getString("code")
                accessToken.expiresTime = jsonObject.getLong("expires_time")
                accessToken.loginCost = jsonObject.getInt("login_cost")
                accessToken.msg = jsonObject.getString("msg")
                accessToken.payToken = jsonObject.getString("pay_token")
                accessToken.pf = jsonObject.getString("pf")
                accessToken.pfkey = jsonObject.getString("pfkey")
                accessToken.proxyCode = jsonObject.getString("proxy_code")
                accessToken.proxyExpiresIn = jsonObject.getInt("proxy_expires_in")
                accessToken.queryAuthorityCost = jsonObject.getInt("query_authority_cost")
                accessToken.ret = jsonObject.getInt("ret")
                return accessToken
            } catch (e: Throwable) {
                throw e
            }
        }
    }
}
