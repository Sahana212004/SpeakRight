package com.example.speakright.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

data class AnalysisResponse(
    val recognized_text: String,
    val pronunciation_score: Double,
    val fluency_score: Double,
    val grammar_score: Double,
    val total_score: Double,
    val feedback: String
)

interface ApiService {
    @Multipart
    @POST("/analyze")
    fun analyzeSpeech(
        @Part audio: MultipartBody.Part,
        @Part("text") text: RequestBody
    ): Call<AnalysisResponse>
}
