package com.irun.runker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Author:         songtao
 * CreateDate:     2020/12/4 17:40
 */
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}