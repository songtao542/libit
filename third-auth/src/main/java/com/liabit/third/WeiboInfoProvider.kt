package com.liabit.third

interface WeiboInfoProvider {
    val appKey: String
    val appSecret: String
    val apiUrl: String
    val redirectUrl: String
    val scope: String
}

interface WechatInfoProvider {
    val appId: String
    val appSecret: String
    val apiUrl: String
}

interface TencentInfoProvider {
    val appId: String
    val appKey: String
}

interface ThirdAppInfoProvider {
    val weiboAppKey: String
    val weiboAppSecret: String
    val weiboApiUrl: String
    val weiboRedirectUrl: String
    val weiboScope: String

    val wechatAppId: String
    val wechatAppSecret: String
    val wechatApiUrl: String

    val tencentAppId: String
    val tencentAppKey: String
}

object ThirdAppInfo {

    private var mTencentInfoProvider: TencentInfoProvider? = null
    private var mWeiboInfoProvider: WeiboInfoProvider? = null
    private var mWechatInfoProvider: WechatInfoProvider? = null
    private var mAppInfoProvider: ThirdAppInfoProvider? = null

    fun setTencentInfoProvider(provider: TencentInfoProvider) {
        mTencentInfoProvider = provider
    }

    fun setWeiboInfoProvider(provider: WeiboInfoProvider) {
        mWeiboInfoProvider = provider
    }

    fun setWechatInfoProvider(provider: WechatInfoProvider) {
        mWechatInfoProvider = provider
    }

    fun setThirdAppInfoProvider(provider: ThirdAppInfoProvider) {
        mAppInfoProvider = provider
    }

    val WEIBO_APP_KEY: String get() = mWeiboInfoProvider?.appKey ?: mAppInfoProvider?.weiboAppKey ?: ""
    val WEIBO_APP_SECRET: String get() = mWeiboInfoProvider?.appSecret ?: mAppInfoProvider?.weiboAppSecret ?: ""
    val WEIBO_API_URL: String get() = mWeiboInfoProvider?.apiUrl ?: mAppInfoProvider?.weiboApiUrl ?: ""
    val WEIBO_REDIRECT_URL: String get() = mWeiboInfoProvider?.redirectUrl ?: mAppInfoProvider?.weiboRedirectUrl ?: ""
    val WEIBO_SCOPE: String get() = mWeiboInfoProvider?.scope ?: mAppInfoProvider?.weiboScope ?: ""

    val WECHAT_APP_ID: String get() = mWechatInfoProvider?.appId ?: mAppInfoProvider?.wechatAppId ?: ""
    val WECHAT_APP_SECRET: String get() = mWechatInfoProvider?.appSecret ?: mAppInfoProvider?.wechatAppSecret ?: ""
    val WECHAT_API_URL: String get() = mWechatInfoProvider?.apiUrl ?: mAppInfoProvider?.wechatApiUrl ?: ""

    val TENCENT_APP_ID: String get() = mTencentInfoProvider?.appId ?: mAppInfoProvider?.tencentAppId ?: ""
    val TENCENT_APP_KEY: String get() = mTencentInfoProvider?.appKey ?: mAppInfoProvider?.tencentAppKey ?: ""

}

