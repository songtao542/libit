package com.irun.runker.util.cache

import android.app.Application
import com.appmattus.layercache.Cache
import com.irun.runker.database.AppDatabase
import com.irun.runker.database.KeyValueDao
import com.irun.runker.database.put
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KeyValueDatabaseCache constructor(application: Application) : Cache<String, String> {
    private val keyValeDao: KeyValueDao = AppDatabase.getInstance(application).keyValueDao()

    override suspend fun evict(key: String) {
        return withContext(Dispatchers.IO) {
            keyValeDao.remove(key)
        }
    }

    override suspend fun evictAll() {
        return withContext(Dispatchers.IO) {
            keyValeDao.removeAll()
        }
    }

    override suspend fun get(key: String): String {
        return withContext(Dispatchers.IO) {
            keyValeDao.get(key)?.value ?: ""
        }
    }

    override suspend fun set(key: String, value: String) {
        return withContext(Dispatchers.IO) {
            keyValeDao.put(key, value)
        }
    }
}