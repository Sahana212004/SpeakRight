package com.example.speakright.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.143:5000/"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)  // ⏳ wait up to 60 sec to connect
        .readTimeout(60, TimeUnit.SECONDS)     // ⏳ wait up to 60 sec for server response
        .writeTimeout(60, TimeUnit.SECONDS)    // ⏳ wait up to 60 sec for sending data
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
