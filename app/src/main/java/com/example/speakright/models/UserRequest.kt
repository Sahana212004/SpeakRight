// UserRequest.kt
package com.example.speakright.models

data class UserRequest(
    val email: String,
    val password: String,
    val first_name: String = "",
    val last_name: String = "",
    val phone: String = ""
)
