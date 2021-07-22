package com.scaffold.cache

import android.util.Log
import com.scaffold.BuildConfig
import com.scaffold.database.KeyValueDao
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.LinkedHashMap

class CacheRepositoryImpl(
    private val gson: Gson,
    private val keyValueDao: KeyValueDao
) : SerialActionQueue(keyValueDao), CacheRepository {

    companion object {
        private const val TAG = "CacheRepositoryImpl"
    }

    private val mCache = LinkedHashMap<String, Any>()

    private val mReadLock = Object()

    /**
     * 直接从内存中获取
     * @return 内存中已存在的值
     */
    @Suppress("UNCHECKED_CAST")
    override fun <Value> get(key: String): Value? {
        return mCache[key] as? Value
    }

    /**
     * 先从内存中获取，如果内存中没有，再从数据库获取
     * @return 缓存的值
     */
    @Suppress("UNCHECKED_CAST")
    override suspend fun <Value> get(key: String, clazz: Class<Value>): Value? {
        return mCache[key] as? Value ?: withContext(Dispatchers.IO) {
            synchronized(mReadLock) {
                val value = mCache[key] as? Value
                if (value != null) {
                    return@withContext value
                }
                val cache = keyValueDao.get(key)?.value ?: return@withContext null
                return@withContext gson.fromJson(cache, clazz)?.also {
                    mCache[key] = it
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "$key:$it")
                    }
                }
            }
        }
    }

    override fun <Value> set(key: String, value: Value?) {
        if (value != null) {
            mCache[key] = value
        } else {
            mCache.remove(key)
        }
        enqueue(key, if (value != null) gson.toJson(value) else null)
    }

    override fun remove(key: String) {
        mCache.remove(key)
        delete(key)
    }

}