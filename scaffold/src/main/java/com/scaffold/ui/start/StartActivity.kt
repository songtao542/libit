package com.scaffold.ui.start

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.scaffold.TheApp
import com.scaffold.base.BaseCompatActivity
import com.scaffold.ui.MainActivity
import com.scaffold.util.Preference
import dagger.hilt.android.AndroidEntryPoint

/**
 * 应用开屏界面
 */
@AndroidEntryPoint
class StartActivity : BaseCompatActivity() {
    companion object {
        // 两次Splash显示的时间间隔 24小时
        private const val SPLASH_DURATION = 24 * 60 * 60 * 1000L

        fun start(context: Context) {
            val intent = Intent(context, StartActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (shouldShowSplash()) {
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

    private fun shouldShowSplash(): Boolean {
        val curTime = System.currentTimeMillis()
        val show = if (curTime - TheApp.APP_LAUNCH_TIME < 500L) {
            // 第一次启动app
            true
        } else {
            // 两次Splash时间间隔
            val lastDisplayTime = Preference.getLong("splash_time", 0L)
            curTime - lastDisplayTime > SPLASH_DURATION
        }
        if (show) {
            Preference.putLong("splash_time", curTime)
        }
        return show
    }


}