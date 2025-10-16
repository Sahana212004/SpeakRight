package com.example.speakright

data class User(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val profilePic: ByteArray?
)
