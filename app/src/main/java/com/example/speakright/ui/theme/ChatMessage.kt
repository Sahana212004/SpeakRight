package com.example.speakright.ui.theme

data class ChatMessage(val message: String, val sender: Sender)

enum class Sender {
    BOT,
    USER
}
