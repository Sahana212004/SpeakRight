package com.example.speakright.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LearnedWordDao {
    @Insert
    suspend fun insertWord(word: LearnedWord)

    @Query("SELECT * FROM LearnedWord")
    suspend fun getAllWords(): List<LearnedWord>
}
