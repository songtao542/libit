package com.scaffold.ui.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.scaffold.base.BaseCompatActivity
import com.scaffold.ui.MainActivity
import com.scaffold.TheApp
import com.scaffold.util.Preference
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用开屏界面
 */
@AndroidEntryPoint
class StartActivity : BaseCompatActivity() {
    companion object {
        private const val AD_DURATION = (24 * 60 * 60 * 1000).toLong() // 两次广告显示的时间间隔

        fun start(context: Context) {
            val intent = Intent(context, StartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldShowAd()) {
            if (savedInstanceState == null) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(android.R.id.content, StartFragment.newInstance())
                    .commitNow()
            }
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        finish()
    }

    private fun shouldShowAd(): Boolean {
        val curTime = System.currentTimeMillis()
        if (curTime - com.scaffold.TheApp.APP_LAUNCH_TIME < 500) {
            // 第一次启动app
            return true
        }
        // 两次广告时间间隔
        val lastDisplayTime = Preference.getLong("ad_read_time", 0)
        return curTime - lastDisplayTime > AD_DURATION
    }

}