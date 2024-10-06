package com.example.myapplication.global

import android.app.Application
import android.content.Context
import com.example.myapplication.di.appModule
import com.example.myapplication.di.networkModule
import com.example.myapplication.di.repositoryModule
import com.example.myapplication.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        appContext = applicationContext

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@App)
            modules(
                listOf(
                    appModule,
                    networkModule,
                    viewModelModule,
                    repositoryModule
                )
            )
        }
    }

    companion object {
        lateinit  var appContext: Context
        lateinit var instance: App
    }


}