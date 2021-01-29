package com.irun.runker.util.cache

import android.app.Application
import android.util.Log
import com.appmattus.layercache.Cache
import com.liabit.settings.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KeyValueFileCache constructor(private val application: Application) : Cache<String, String> {

    init {
        AppSettings.init(application)
    }

    override suspend fun evict(key: String) {
        return withContext(Dispatchers.IO) {
            AppSettings.putString(key, "")
        }
    }

    override suspend fun evictAll() {
        return withContext(Dispatchers.IO) {
            Log.d("KeyValueFileCache", "Don't support evict all!")
        }
    }

    override suspend fun get(key: String): String {
        return withContext(Dispatchers.IO) {
            AppSettings.getString(key, "")
        }
    }

    override suspend fun set(key: String, value: String) {
        return withContext(Dispatchers.IO) {
            AppSettings.putString(key, value)
        }
    }

}