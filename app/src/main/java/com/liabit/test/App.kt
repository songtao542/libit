package com.liabit.test

import android.app.Application
import com.liabit.settings.AppSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppSettings.init(this)
    }
}