package com.liabit.third.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WxUser(
    var accessToken: String? = null,
    var refreshToken: String? = null,
    var openid: String? = null,// "openid":"o4PN51DasTTHB1Ieds6NIC5AM5OA",
    var nickname: String? = null,//"nickname":"songtao",
    var sex: Int = -1,//"sex":1,
    var language: String? = null,//"language":"en",
    var city: String? = null,//"city":"",
    var province: String? = null,//"province":"",
    var country: String? = null,//"country":"",
    var headimgurl: String? = null,//"headimgurl":"http://thirdwx.qlogo.cn/mmopen/vi_32/7KaRIUuOoUrZDcmvibZNia8kDCsfPiaO2bt5NwgCZaic17hiafVDOneG5SVSE1cniaf5a1Om5ia0x4oD21LNFBMbdd28Q/132",
    //"privilege":[],
    var unionid: String? = null //"unionid":"ocGSv5_JyViQROMku0z65_AAg1x8"
) : Parcelable