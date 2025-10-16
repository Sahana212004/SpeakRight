package com.example.speakright.network

import com.example.speakright.models.UserRequest
import com.example.speakright.models.UserResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/signup")
    fun signup(@Body request: UserRequest): Call<UserResponse>

    @POST("/login")
    fun login(@Body request: UserRequest): Call<UserResponse>
}

object RetrofitClient {

    private const val BASE_URL = "http://172.16.36.143:5000" // Android emulator → Flask on PC

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
