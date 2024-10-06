package com.example.myapplication.di

import android.content.Context
import com.example.myapplication.utils.AppPreferences
import com.example.myapplication.utils.ConnectivityUtil
import org.koin.dsl.module

var appModule= module {
    fun provideConnectivityUtil(context: Context) = ConnectivityUtil(context)

    fun provideAppPreferences(context: Context): AppPreferences {
        return AppPreferences(context)
    }

    single { provideConnectivityUtil(get()) }
    single { provideAppPreferences(get()) }

}
