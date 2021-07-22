package com.domain.scaffold.di

import com.domain.scaffold.network.Api
import com.domain.scaffold.TheApp
import com.google.gson.Gson
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface EntityEntryPoint {
    fun getApi(): Api
    fun getGson(): Gson
}

object EntryPoints {

    @JvmStatic
    fun <T> get(clazz: Class<T>): T {
        val ctx = TheApp.context.applicationContext
        val entityEntryPoint = EntryPointAccessors.fromApplication(ctx, EntityEntryPoint::class.java)
        @Suppress("UNCHECKED_CAST")
        return when (clazz) {
            Api::class.java -> entityEntryPoint.getApi() as T
            Gson::class.java -> entityEntryPoint.getGson() as T
            else -> throw IllegalStateException("Not find entry point for $clazz")
        }
    }

}
