package com.scaffold.cache

import com.scaffold.di.EntryPoints

interface CacheRepository {

    companion object {
        fun getInstance(): CacheRepository {
            // dagger 会保证单例性
            return EntryPoints.get(CacheRepository::class.java)
        }
    }

    fun <Value> get(key: String): Value?
    suspend fun <Value> get(key: String, clazz: Class<Value>): Value?
    fun <Value> set(key: String, value: Value?)
    fun remove(key: String)
}