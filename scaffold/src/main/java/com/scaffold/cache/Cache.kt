package com.scaffold.cache

interface Cache<Key : Any, Value : Any> {
    /**
     * Return the value associated with the key or null if not present
     */
    suspend fun get(key: Key): Value?

    /**
     * Save the value against the key
     */
    suspend fun set(key: Key, value: Value)

    /**
     * Remove the data associated with the key
     */
    suspend fun evict(key: Key)

    /**
     * Remove the data associated with all keys
     */
    suspend fun evictAll()
}