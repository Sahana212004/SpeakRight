package com.example.speakright.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LearnedWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val meaning: String
)
