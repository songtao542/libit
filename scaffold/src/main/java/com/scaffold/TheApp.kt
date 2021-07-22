package com.scaffold

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.os.StrictMode
import android.util.Log
import androidx.work.Configuration
import com.bumptech.glide.Glide
import com.scaffold.BuildConfig
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

        val context: Context get() = com.scaffold.TheApp.Companion.mContext ?: throw IllegalStateException("Impossible to execute this code")

        val applicationContext: Context? get() = com.scaffold.TheApp.Companion.mContext

        val APP_LAUNCH_TIME = System.currentTimeMillis()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        com.scaffold.TheApp.Companion.mContext = this
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(com.scaffold.TheApp.Companion.TAG, "StoreApplication onCreate()")
        Preference.init(this)
        clearGlideCacheIfNeeded()

        if (com.scaffold.TheApp.Companion.DEBUG) {
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
        com.scaffold.TheApp.CheckVersionTask(this).execute()
    }

    class CheckVersionTask(context: Context) : AsyncTask<Void?, Void?, Boolean>() {

        private val mContextRef = WeakReference(context)

        override fun doInBackground(vararg params: Void?): Boolean {
            val ctx = mContextRef.get() ?: return false
            val prefer = ctx.getSharedPreferences(com.scaffold.TheApp.Companion.PREFER_NAME, Context.MODE_PRIVATE)
            try {
                ctx.packageManager.getPackageInfo(ctx.packageName, 0)?.let { info ->
                    val versionName = prefer.getString("version_name", "")
                    if (!versionName.equals(info.versionName)) {
                        prefer.edit().putString("version_name", info.versionName).apply()
                        Glide.get(ctx).clearDiskCache()
                        Log.d(com.scaffold.TheApp.Companion.TAG, "clear glide disk cache")
                        return true
                    }
                }
            } catch (e: Throwable) {
                Log.w(com.scaffold.TheApp.Companion.TAG, "check version error: ", e)
            }
            return false
        }

        override fun onPostExecute(result: Boolean?) {
            if (result == true) {
                val ctx = mContextRef.get() ?: return
                Glide.get(ctx).clearMemory()
                Log.d(com.scaffold.TheApp.Companion.TAG, "clear glide memory cache")
            }
        }
    }


}