package com.example.speakright.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class UserRequest(val email: String, val password: String)
data class UserResponse(val message: String)

interface ApiService {

    @POST("/signup")
    fun signup(@Body request: UserRequest): Call<UserResponse>

    @POST("/login")
    fun login(@Body request: UserRequest): Call<UserResponse>
}