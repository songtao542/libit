//package com.liabit.third
//
////https://openauth.alipaydev.com/oauth2/appToAppAuth.htm?app_id=2016092400585139&redirect_uri=https://api.chunmi69.com/auth/alipay/getUserInfo?state=用户token
//object Tencent {
//    const val APP_ID = "1107903594"
//    const val APP_KEY = "Jzgzv5j0ro5RWVUP"
//}
//
object Wechat {
    /**
     * doc: https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html
     */
    const val BASE_URL = "https://api.weixin.qq.com"
}

object Weibo {
    const val BASE_URL = "https://api.weibo.com"
    const val REDIRECT_URL = "http://www.weibo.com"
    const val SCOPE =
        "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write"
}
