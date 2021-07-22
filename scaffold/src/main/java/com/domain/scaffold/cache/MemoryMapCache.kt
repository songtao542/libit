package com.domain.scaffold.cache

class MemoryMapCache : Cache<String, String> {

    private val map = mutableMapOf<String, String?>()

    override suspend fun get(key: String): String? {
        return map[key]
    }

    override suspend fun set(key: String, value: String) {
        map[key] = value
    }

    override suspend fun evict(key: String) {
        map.remove(key)
    }

    override suspend fun evictAll() {
        map.clear()
    }


}