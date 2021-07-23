package com.scaffold

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.util.Log
import androidx.work.Configuration
import com.bumptech.glide.Glide
import com.scaffold.util.Executors
import com.scaffold.util.Preference
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference

@HiltAndroidApp
class TheApp : Application(), Configuration.Provider {

    companion object {
        const val PREFER_NAME = "store"
        const val DEBUG = false
        const val TAG = "ThemeApp"

        private var mContext: Application? = null

        val context: Context get() = mContext ?: throw IllegalStateException("Impossible to execute this code")

        val applicationContext: Context? get() = mContext

        val APP_LAUNCH_TIME = System.currentTimeMillis()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        mContext = this
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "StoreApplication onCreate()")
        Preference.init(this)
        clearGlideCacheIfNeeded()

        if (DEBUG) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    //.detectLeakedClosableObjects()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.ERROR)
            .build()
    }

    private fun clearGlideCacheIfNeeded() {
        Executors.UI_HELPER_EXECUTOR.execute(CheckVersionTask(this))
    }

    class CheckVersionTask(context: Context) : Runnable {

        private val mContextRef = WeakReference(context)

        override fun run() {
            val ctx = mContextRef.get() ?: return
            val prefer = ctx.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE)
            try {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0)?.let { info ->
                    val versionName = prefer.getString("version_name", "")
                    if (!versionName.equals(info.versionName)) {
                        prefer.edit().putString("version_name", info.versionName).apply()
                        Glide.get(ctx).clearDiskCache()
                        Log.d(TAG, "clear glide disk cache")
                        Executors.MAIN_EXECUTOR.post {
                            Glide.get(ctx).clearMemory()
                            Log.d(TAG, "clear glide memory cache")
                        }
                        return
                    }
                }
            } catch (e: Throwable) {
                Log.w(TAG, "check version error: ", e)
            }
            return
        }
    }

}