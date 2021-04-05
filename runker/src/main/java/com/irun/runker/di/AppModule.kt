package com.irun.runker.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.irun.runker.BuildConfig
import com.irun.runker.database.AppDatabase
import com.irun.runker.database.KeyValueDao
import com.irun.runker.database.SportRecordDao
import com.irun.runker.net.Api
import com.irun.runker.net.ApiService
import com.irun.runker.net.util.TimeoutInterceptor
import com.irun.runker.util.retrofit.DirectCallAdapter
import com.irun.runker.util.retrofit.GsonConverterFactory
import com.irun.runker.util.retrofit.livedata.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideSportRecordDao(database: AppDatabase): SportRecordDao {
        return database.sportRecordDao()
    }

    @Singleton
    @Provides
    fun provideKeyValueDao(database: AppDatabase): KeyValueDao {
        return database.keyValueDao()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Singleton
    @Provides
    fun provideApi(@ApplicationContext context: Context, gson: Gson): Api {
        return ApiService(
            Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .addCallAdapterFactory(DirectCallAdapter())
                .client(getOkHttpClientBuilder(context).build())
                .build()
                .create(Api::class.java)
        )
    }

    private const val DEFAULT_TIMEOUT = 30000L

    private fun getOkHttpClientBuilder(context: Context): OkHttpClient.Builder {
        val okHttpBuilder = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(TimeoutInterceptor())
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(httpLoggingInterceptor)
        }
        return okHttpBuilder
    }
}
