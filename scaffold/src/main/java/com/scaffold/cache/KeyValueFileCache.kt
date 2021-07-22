package com.scaffold.cache

import android.util.Log
import androidx.datastore.core.DataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences
import javax.inject.Inject

class KeyValueFileCache @Inject constructor(private val dataStore: DataStore<Preferences>) : Cache<String, String> {

    override suspend fun evict(key: String) {
        return withContext(Dispatchers.IO) {
            dataStore.data.firstOrNull()?.remove(key)
        }
    }

    override suspend fun evictAll() {
        return withContext(Dispatchers.IO) {
            Log.d("KeyValueFileCache", "Don't support evict all!")
        }
    }

    override suspend fun get(key: String): String {
        return withContext(Dispatchers.IO) {
            dataStore.data.firstOrNull()?.get(key, "") ?: ""
        }
    }

    override suspend fun set(key: String, value: String) {
        return withContext(Dispatchers.IO) {
            dataStore.data.firstOrNull()?.put(key, value)
        }
    }

}