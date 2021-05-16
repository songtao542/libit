package com.liabit.third

interface WeiboInfoProvider {
    val appKey: String
    val appSecret: String

    fun getApiUrl(): String {
        return Weibo.BASE_URL
    }

    fun getRedirectUrl(): String {
        return Weibo.REDIRECT_URL
    }

    fun getScope(): String {
        return Weibo.SCOPE
    }
}

interface WechatInfoProvider {
    val appId: String
    val appSecret: String

    fun getApiUrl(): String {
        return Wechat.BASE_URL
    }
}

interface TencentInfoProvider {
    val appId: String
    val appKey: String
}

interface ThirdAppInfoProvider {
    val weiboAppKey: String
    val weiboAppSecret: String

    fun getWeiboApiUrl(): String {
        return Weibo.BASE_URL
    }

    fun getWeiboRedirectUrl(): String {
        return Weibo.REDIRECT_URL
    }

    fun getWeiboScope(): String {
        return Weibo.SCOPE
    }

    val wechatAppId: String
    val wechatAppSecret: String

    fun getWechatApiUrl(): String {
        return Wechat.BASE_URL
    }

    val tencentAppId: String
    val tencentAppKey: String
}

object ThirdAppInfo {

    private var mTcProvider: TencentInfoProvider? = null
    private var mWbProvider: WeiboInfoProvider? = null
    private var mWxProvider: WechatInfoProvider? = null
    private var mInfoProvider: ThirdAppInfoProvider? = null

    fun setTencentInfoProvider(provider: TencentInfoProvider) {
        mTcProvider = provider
    }

    fun setWeiboInfoProvider(provider: WeiboInfoProvider) {
        mWbProvider = provider
    }

    fun setWechatInfoProvider(provider: WechatInfoProvider) {
        mWxProvider = provider
    }

    fun setThirdAppInfoProvider(provider: ThirdAppInfoProvider) {
        mInfoProvider = provider
    }

    val WEIBO_APP_KEY: String get() = mWbProvider?.appKey ?: mInfoProvider?.weiboAppKey ?: ""
    val WEIBO_APP_SECRET: String get() = mWbProvider?.appSecret ?: mInfoProvider?.weiboAppSecret ?: ""
    val WEIBO_SCOPE: String get() = mWbProvider?.getScope() ?: mInfoProvider?.getWeiboScope() ?: Weibo.SCOPE
    val WEIBO_API_URL: String get() = mWbProvider?.getApiUrl() ?: mInfoProvider?.getWeiboApiUrl() ?: Weibo.BASE_URL
    val WEIBO_REDIRECT_URL: String get() = mWbProvider?.getRedirectUrl() ?: mInfoProvider?.getWeiboRedirectUrl() ?: Weibo.REDIRECT_URL

    val WX_APP_ID: String get() = mWxProvider?.appId ?: mInfoProvider?.wechatAppId ?: ""
    val WX_APP_SECRET: String get() = mWxProvider?.appSecret ?: mInfoProvider?.wechatAppSecret ?: ""
    val WX_API_URL: String get() = mWxProvider?.getApiUrl() ?: mInfoProvider?.getWechatApiUrl() ?: Wechat.BASE_URL

    val TENCENT_APP_ID: String get() = mTcProvider?.appId ?: mInfoProvider?.tencentAppId ?: ""
    val TENCENT_APP_KEY: String get() = mTcProvider?.appKey ?: mInfoProvider?.tencentAppKey ?: ""

}

