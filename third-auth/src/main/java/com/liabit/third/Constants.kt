package com.liabit.third

object Wechat {
    /**
     * doc: https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Development_Guide.html
     */
    const val BASE_URL = "https://api.weixin.qq.com"
}

object Weibo {
    /**
     * doc: https://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     */
    const val BASE_URL = "https://api.weibo.com"
    const val REDIRECT_URL = "http://www.weibo.com"
    const val SCOPE =
        "email,direct_messages_read,direct_messages_write,friendships_groups_read,friendships_groups_write,statuses_to_me_read,follow_app_official_microblog,invitation_write"
}
