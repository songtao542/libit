package com.liabit.third.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class WxPrePayInfo(
    var appid: String? = null,//   "appid": "wxa876e58bf61e9dac",
    var partnerid: String? = null,//"partnerid": "1518670541",
    var prepayid: String? = null,//"prepayid": "wx191853538479007a399f88f21490905448",
    var noncestr: String? = null,//"noncestr": "35ed633d304cd5fbccc01d125fbe6d27",
    var timestamp: String? = null,//  "timestamp" : "1545216834",
    @SerializedName(value = "package") var pack: String? = null,//"package": "Sign=WXPay",
    @SerializedName(value = "paySign") var sign: String? = null//"paySign": "54A7514D0FE5BA1FC687231F7CB02063"
) : Parcelable