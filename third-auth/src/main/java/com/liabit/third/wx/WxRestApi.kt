package com.liabit.third.wx

import com.google.gson.Gson
import com.liabit.net.interceptor.EnhancedHttpLoggingInterceptor
import com.liabit.third.BuildConfig
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.WxAccessToken
import com.liabit.third.model.WxUser
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WxRestApi {

    companion object {
        @JvmStatic
        fun create(gson: Gson?): WxRestApi {
            val g = gson ?: Gson()
            return Retrofit.Builder()
                .baseUrl(ThirdAppInfo.WX_API_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(getOkHttpClientBuilder().build())
                .build()
                .create(WxRestApi::class.java)
        }

        private fun getOkHttpClientBuilder(): OkHttpClient.Builder {
            val okHttpBuilder = OkHttpClient.Builder()
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = EnhancedHttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(EnhancedHttpLoggingInterceptor.Level.BODY)
                okHttpBuilder.addInterceptor(httpLoggingInterceptor)
            }
            okHttpBuilder.addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain.request()
                val url = request.url.newBuilder()
                    .addQueryParameter("appid", ThirdAppInfo.WX_APP_ID)
                    .addQueryParameter("secret", ThirdAppInfo.WX_APP_SECRET)
                    .build()
                chain.proceed(request.newBuilder().url(url).build())
            })
            return okHttpBuilder
        }
    }


    /**
     * #### 参数         必选       说明
     * #### appid        是        应用唯一标识，在微信开放平台提交应用审核通过后获得
     * #### secret       是        应用密钥 AppSecret，在微信开放平台提交应用审核通过后获得
     * #### code         是        填写第一步获取的 code 参数
     * #### grant_type   是        填 authorization_code
     * ####
     * #### 返回示例:
     * #### {
     * ####     "access_token": "ACCESS_TOKEN",
     * ####     "expires_in": 7200,
     * ####     "refresh_token": "REFRESH_TOKEN",
     * ####     "openid": "OPENID",
     * ####     "scope": "SCOPE",
     * ####     "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * #### }
     * ####
     * #### 参数              说明
     * #### access_token      接口调用凭证
     * #### expires_in        access_token 接口调用凭证超时时间，单位（秒）
     * #### refresh_token     用户刷新 access_token
     * #### openid            授权用户唯一标识
     * #### scope             用户授权的作用域，使用逗号（,）分隔
     * #### unionid           当且仅当该移动应用已获得该用户的 userinfo 授权时，才会出现该字段
     * ####
     * #### 刷新 access_token 有效期
     * #### access_token 是调用授权关系接口的调用凭证，由于 access_token 有效期（目前为 2 个小时）较短，
     * #### 当 access_token 超时后，可以使用 refresh_token 进行刷新，access_token 刷新结果有两种：
     * #### 1. 若access_token已超时，那么进行refresh_token会获取一个新的access_token，新的超时时间；
     * #### 2. 若access_token未超时，那么进行refresh_token不会改变access_token，但超时时间会刷新，相当于续期access_token。
     * #### refresh_token 拥有较长的有效期（30 天），当 refresh_token 失效的后，需要用户重新授权。
     */
    @GET("/sns/oauth2/access_token")
    suspend fun getAccessToken(
        @Query("appid") appid: String,
        @Query("secret") secret: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String
    ): WxAccessToken?


    /**
     * #### 参数              必选       说明
     * #### appid             是        应用唯一标识
     * #### grant_type        是        填 refresh_token
     * #### refresh_token     是        填写通过 access_token 获取到的 refresh_token 参数
     * ####
     * #### 返回示例:
     * #### {
     * ####     "access_token": "ACCESS_TOKEN",
     * ####     "expires_in": 7200,
     * ####     "refresh_token": "REFRESH_TOKEN",
     * ####     "openid": "OPENID",
     * ####     "scope": "SCOPE"
     * #### }
     * ####
     * #### 参数                说明
     * #### access_token        接口调用凭证
     * #### expires_in          access_token 接口调用凭证超时时间，单位（秒）
     * #### refresh_token       用户刷新 access_token
     * #### openid              授权用户唯一标识
     * #### scope               用户授权的作用域，使用逗号（,）分隔
     */
    @GET("sns/oauth2/refresh_token")
    suspend fun refreshToken(
        @Query("appid") appid: String,
        @Query("refresh_token") refreshToken: String,
        @Query("grant_type") grantType: String
    ): WxAccessToken?

    /**
     * #### 参数            必选      说明
     * #### access_token   是        调用凭证
     * #### openid         是        普通用户的标识，对当前开发者帐号唯一
     * #### lang           否        国家地区语言版本，zh_CN 简体，zh_TW 繁体，en 英语，默认为 zh-CN
     * ####
     * #### 返回示例:
     * #### {
     * ####     "openid": "OPENID",
     * ####     "nickname": "NICKNAME",
     * ####     "sex": 1,
     * ####     "province": "PROVINCE",
     * ####     "city": "CITY",
     * ####     "country": "COUNTRY",
     * ####     "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * ####     "privilege": ["PRIVILEGE1", "PRIVILEGE2"],
     * ####     "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * #### }
     * #### 参数           说明
     * #### openid        普通用户的标识，对当前开发者帐号唯一
     * #### nickname      普通用户昵称
     * #### sex           普通用户性别，1 为男性，2 为女性
     * #### province      普通用户个人资料填写的省份
     * #### city          普通用户个人资料填写的城市
     * #### country       国家，如中国为 CN
     * #### headimgurl    用户头像，最后一个数值代表正方形头像大小（有 0、46、64、96、132 数值可选，0 代表 640*640 正方形头像），用户没有头像时该项为空
     * #### privilege     用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
     * #### unionid       用户统一标识。针对一个微信开放平台帐号下的应用，同一用户的 unionid 是唯一的。
     * ####
     * #### api doc: https://developers.weixin.qq.com/doc/oplatform/Mobile_App/WeChat_Login/Authorized_API_call_UnionID.html
     */
    @GET("/sns/userinfo")
    suspend fun getWxUser(
        @Query("access_token") accessToken: String,
        @Query("openid") openid: String,
    ): WxUser?


}