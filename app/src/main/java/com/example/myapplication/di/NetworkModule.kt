package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.BuildConfig
import com.example.myapplication.data.network.ApiService
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val TIMEOUT_IN_SECS = 100
private const val BASE_URL = "https://script.google.com/macros/s/AKfycbzNfaCO0OTKchnnqXg7wKeq3KooFs605S9yMQHKwNHE0Rti-9X4hr6Z2um46MQJw3eF/exec"

val networkModule = module {

    fun provideApiServiceV2(
        okHttpClient: OkHttpClient, rxJava2CallAdapterFactory: RxJava2CallAdapterFactory, gson: Gson
    ): ApiService? {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(rxJava2CallAdapterFactory).client(okHttpClient).build()
            .create(ApiService::class.java)
    }

    fun provideOkHttpClient(
        cookieJar: CookieJar,
        loggingInterceptor: HttpLoggingInterceptor,
        headerAuthorizationInterceptor: Interceptor,
        cache: Cache?
    ): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.addInterceptor(headerAuthorizationInterceptor)
        httpClient.connectTimeout(
                TIMEOUT_IN_SECS.toLong(), TimeUnit.SECONDS
        )
        httpClient.readTimeout(
            TIMEOUT_IN_SECS.toLong(), TimeUnit.SECONDS
        )
        httpClient.cookieJar(cookieJar)
        httpClient.cache(cache)

        if (BuildConfig.DEBUG) {
            httpClient.addInterceptor(loggingInterceptor)
        }
        return httpClient.build()
    }

    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }


    fun provideCache(context: Application): Cache {
        val cacheSize = 5 * 1024 * 1024 // 5 MB
        val cacheDir = context.cacheDir
        return Cache(cacheDir, cacheSize.toLong())
    }

    single { provideCache(get()) }
    single { provideLoggingInterceptor() }
    single { provideOkHttpClient(get(), get(), get(), get()) }
    single { provideApiServiceV2(get(), get(), get()) }

}