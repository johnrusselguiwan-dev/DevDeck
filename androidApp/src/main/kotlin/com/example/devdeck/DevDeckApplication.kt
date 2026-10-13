package com.example.devdeck

import android.app.Application
import com.example.devdeck.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class DevDeckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(this@DevDeckApplication)
                modules(appModule)
            }
        }
    }
}
