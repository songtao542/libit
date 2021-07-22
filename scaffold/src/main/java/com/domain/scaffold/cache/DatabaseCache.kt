package com.domain.scaffold.cache

import com.domain.scaffold.database.KeyValueDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.domain.scaffold.database.put

class KeyValueDatabaseCache @Inject constructor(private val keyValueDao: KeyValueDao) : Cache<String, String> {

    override suspend fun evict(key: String) {
        return withContext(Dispatchers.IO) {
            keyValueDao.remove(key)
        }
    }

    override suspend fun evictAll() {
        return withContext(Dispatchers.IO) {
            keyValueDao.removeAll()
        }
    }

    override suspend fun get(key: String): String {
        return withContext(Dispatchers.IO) {
            keyValueDao.get(key)?.value ?: ""
        }
    }

    override suspend fun set(key: String, value: String) {
        return withContext(Dispatchers.IO) {
            keyValueDao.put(key, value)
        }
    }
}