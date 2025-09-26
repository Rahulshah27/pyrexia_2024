package com.example.myapplication.data.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    private const val BASE_URL = "https://script.google.com"

    // Create a single instance of Retrofit
    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val gson = GsonBuilder()
            .create()

        // ✅ Configure OkHttpClient with timeouts
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)   // max time to establish connection
            .readTimeout(60, TimeUnit.SECONDS)     // max time to wait for server response
            .writeTimeout(30, TimeUnit.SECONDS)    // max time to send request body
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    // Create and expose the ApiService
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}