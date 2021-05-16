package com.liabit.third.weibo

import com.google.gson.Gson
import com.liabit.net.interceptor.EnhancedHttpLoggingInterceptor
import com.liabit.third.BuildConfig
import com.liabit.third.ThirdAppInfo
import com.liabit.third.model.WeiboAccessToken
import com.liabit.third.model.WeiboAccessTokenInfo
import com.liabit.third.model.WeiboUser
import okhttp3.OkHttpClient.Builder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeiboRestApi {

    companion object {
        @JvmStatic
        fun create(gson: Gson?): WeiboRestApi {
            val g = gson ?: Gson()
            return Retrofit.Builder()
                .baseUrl(ThirdAppInfo.WEIBO_API_URL)
                .addConverterFactory(GsonConverterFactory.create(g))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //.addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(getOkHttpClientBuilder().build())
                .build()
                .create(WeiboRestApi::class.java)
        }

        private fun getOkHttpClientBuilder(): Builder {
            val okHttpBuilder = Builder()
            if (BuildConfig.DEBUG) {
                val httpLoggingInterceptor = EnhancedHttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(EnhancedHttpLoggingInterceptor.Level.BODY)
                okHttpBuilder.addInterceptor(httpLoggingInterceptor)
            }
            return okHttpBuilder
        }
    }

    /**
     * #### 参数名          必选          类型及范围    说明
     * #### access_token   true         string       采用OAuth授权方式为必填参数，OAuth授权后获得。
     * #### uid            false        int64        需要查询的用户ID。
     * #### screen_name    false        string       需要查询的用户昵称。
     * ####
     * #### 注意事项
     * #### 参数 uid 与 screen_name 二者必选其一，且只能选其一；
     * #### 接口升级后，对未授权本应用的uid，将无法获取其个人简介、认证原因、粉丝数、关注数、微博数及最近一条微博内容。
     * ####
     * #### api doc: https://open.weibo.com/wiki/2/users/show
     * #### https://api.weibo.com/2/users/show.json
     */
    @GET("/2/users/show.json")
    suspend fun getUserInfo(
        @Query("access_token") accessToken: String,
        @Query("uid") uid: String
    ): WeiboUser?

    /**
     * #### 参数名           必选     类型及范围
     * #### client_id       true    string     申请应用时分配的AppKey。
     * #### client_secret   true    string     申请应用时分配的AppSecret。
     * #### grant_type      true    string     请求的类型，填写authorization_code
     * ####
     * #### 返回示例:
     * #### {
     * ####     "access_token": "ACCESS_TOKEN",
     * ####     "expires_in": 1234,
     * ####     "remind_in":"798114",
     * ####     "uid":"12341234"
     * #### }
     * ####
     * #### 返回值字段        字段类型       字段说明
     * #### access_token    string        用户授权的唯一票据，用于调用微博的开放接口，同时也是第三方应用验证微博用户登录的唯一票据，第三方应用应该用该票据和自己应用内的用户建立唯一影射关系，来识别登录状态，不能使用本返回值里的UID字段来做登录识别。
     * #### expires_in      string        access_token的生命周期，单位是秒数。
     * #### remind_in       string        access_token的生命周期（该参数即将废弃，开发者请使用expires_in）。
     * #### uid             string        授权用户的UID，本字段只是为了方便开发者，减少一次user/show接口调用而返回的，第三方应用不能用此字段作为用户登录状态的识别，只有access_token才是用户授权的唯一票据。
     * ####
     * #### api doc: https://open.weibo.com/wiki/Oauth2/access_token
     * #### https://api.weibo.com/oauth2/access_token
     */
    @GET("/oauth2/access_token")
    suspend fun getAccessToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String
    ): WeiboAccessToken

    /**
     * #### 参数名          必选          类型及范围     说明
     * #### client_id      true         string        申请应用时分配的AppKey。
     * #### redirect_uri   true         string        授权回调地址，站外应用需与设置的回调地址一致，站内应用需填写canvas page的地址。
     * #### scope          false        string        申请scope权限所需参数，可一次申请多个scope权限，用逗号分隔。使用文档
     * #### state          false        string        用于保持请求和回调的状态，在回调时，会在Query Parameter中回传该参数。开发者可以用这个参数验证请求有效性，也可以记录用户请求授权页前的位置。这个参数可用于防止跨站请求伪造（CSRF）攻击
     * #### display        false        string        授权页面的终端类型，取值见下面的说明。
     * #### forcelogin     false        boolean       是否强制用户重新登录，true：是，false：否。默认false。
     * #### language       false        string        授权页语言，缺省为中文简体版，en为英文版。英文版测试中，开发者任何意见可反馈至 @微博API
     * ####
     * #### display说明：
     * #### 参数取值        类型说明
     * #### default        默认的授权页面，适用于web浏览器。
     * #### mobile         移动终端的授权页面，适用于支持html5的手机。注：使用此版授权页请用 https://open.weibo.cn/oauth2/authorize 授权接口
     * #### wap            wap版授权页面，适用于非智能手机。
     * #### client         客户端版本授权页面，适用于PC桌面应用。
     * #### apponweibo     默认的站内应用授权页，授权后不返回access_token，只刷新站内应用父框架。
     * ####
     * #### 返回值字段      字段类型       字段说明
     * #### code          string        用于第二步调用oauth2/access_token接口，获取授权后的access token。
     * #### state         string        如果传递参数，会回传该参数。
     * ####
     * #### 示例
     * #### 请求
     * #### https://api.weibo.com/oauth2/authorize?client_id=123050457758183&redirect_uri=http://www.example.com/response&response_type=code
     * ####
     * #### 同意授权后会重定向
     * #### http://www.example.com/response&code=CODE
     * ####
     * #### api doc: https://open.weibo.com/wiki/Oauth2/authorize
     * #### https://api.weibo.com/oauth2/authorize
     */
    @GET("/oauth2/authorize")
    suspend fun authorize(
        clientId: String,
        clientSecret: String,
        grantType: String
    ): WeiboAccessToken

    /**
     * #### 参数名          必选         类型及范围
     * #### access_token   true        string       采用OAuth授权方式为必填参数，OAuth授权后获得。
     * ####
     * #### 返回值字段     字段类型       字段说明
     * #### uid          string        授权用户的uid。
     * #### appkey       string        access_token所属的应用appkey。
     * #### scope        string        用户授权的scope权限。
     * #### create_at    string        access_token的创建时间，从1970年到创建时间的秒数。
     * #### expire_in    string        access_token的剩余时间，单位是秒数，如果返回的时间是负数，代表授权已经过期。
     * ####
     * #### api doc: https://open.weibo.com/wiki/Oauth2/get_token_info
     * #### https://api.weibo.com/oauth2/get_token_info
     */
    @GET("/oauth2/authorize")
    suspend fun getTokenInfo(
        @Query("access_token") accessToken: String,
    ): WeiboAccessTokenInfo
}