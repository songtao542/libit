package com.liabit.test

import android.app.Application
import com.liabit.settings.AppSettings

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        AppSettings.init(this)
    }
}