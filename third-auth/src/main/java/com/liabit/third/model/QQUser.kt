package com.liabit.third.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
data class QQUser(
    val openId: String,
    val thirdToken: String,
    val plate: String,
    val nickName: String,
    val headPic: String,
    val sex: Int,
    val province: String,
    val city: String,
    val year: Int
) : Parcelable {
    companion object {
        @Throws
        fun from(openId: String, accessToken: String, jsonObject: JSONObject): QQUser {
            try {
                val plate = "0" //0 qq 1 wx 3 wb
                val nickName = jsonObject.getString("nickname")
                val headPic = jsonObject.getString("figureurl_qq_2") ?: jsonObject.getString("figureurl_qq_1")
                val sex = when (jsonObject.getString("gender")) {
                    "男" -> 1
                    "女" -> 0
                    else -> -1
                }
                val province = jsonObject.getString("province")
                val city = jsonObject.getString("city")
                val year = jsonObject.getString("year").toInt()
                return QQUser(
                    openId,
                    accessToken,
                    plate,
                    nickName,
                    headPic,
                    sex,
                    province,
                    city,
                    year
                )
            } catch (e: Throwable) {
                throw  e
            }
        }
    }
}