package com.liabit.third

import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.liabit.third.model.WxAccessToken
import com.liabit.third.model.WxUser

/**
 *  // add the code into you AppModule
 *  @Singleton
 *  @Provides
 *  fun provideWeiboRestApi(gson: Gson): WeiboRestApi {
 *      return WeiboRestApi.create(gson)
 *  }
 *
 *  @Singleton
 *  @Provides
 *  fun provideWxRestApi(gson: Gson): WxRestApi {
 *      return WxRestApi.create(gson)
 *  }
 *
 *  @Singleton
 *  @Provides
 *  fun provideThirdUserRepositoryImpl(@ApplicationContext context: Context): ThirdUserRepository {
 *      return ThirdUserRepositoryImpl(context)
 *  }
 */
interface ThirdUserRepository {

    suspend fun getWxAccessToken(): WxAccessToken?
    suspend fun setWxAccessToken(accessToken: WxAccessToken)

    suspend fun getWxUser(accessToken: String, openid: String): WxUser?
    suspend fun setWxUser(wxUser: WxUser)

    suspend fun getWeiboAccessToken(): Oauth2AccessToken?

}