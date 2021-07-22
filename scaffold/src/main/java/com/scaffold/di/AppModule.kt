package com.scaffold.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.scaffold.base.BasicInfoProviderImpl
import com.scaffold.cache.CacheRepository
import com.scaffold.cache.CacheRepositoryImpl
import com.scaffold.cache.KeyValueDatabaseCache
import com.scaffold.database.AppDatabase
import com.scaffold.database.KeyValueDao
import com.scaffold.network.Api
import com.scaffold.network.interceptor.BasicInfoProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


/**
 * Author:         songtao
 * CreateDate:     2020/12/4 18:04
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

//    @Singleton
//    @Provides
//    fun providePreferences(@ApplicationContext context: Context): DataStore<Preferences> {
//        return context.createPreferencesDataStore(name = "settings")
//    }

    @Singleton
    @Provides
    fun provideKeyValueDao(database: AppDatabase): KeyValueDao {
        return database.keyValueDao()
    }

    @Singleton
    @Provides
    fun provideKeyValueDatabaseCache(keyValueDao: KeyValueDao): KeyValueDatabaseCache {
        return KeyValueDatabaseCache(keyValueDao)
    }

    @Singleton
    @Provides
    fun provideCacheRepository(gson: Gson, keyValueDao: KeyValueDao): CacheRepository {
        return CacheRepositoryImpl(gson, keyValueDao)
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
        return GsonBuilder().setDateFormat("YYYY-MM-dd HH:mm:ss").create();
    }

    @Singleton
    @Provides
    fun provideBasicInfoProvider(@ApplicationContext context: Context): BasicInfoProvider {
        return BasicInfoProviderImpl(context)
    }

    @Singleton
    @Provides
    fun provideApi(@ApplicationContext context: Context, gson: Gson, basicInfoProvider: BasicInfoProvider): Api {
        return Api.create(context, basicInfoProvider, gson)
    }

}
