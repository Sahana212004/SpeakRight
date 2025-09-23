package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import java.util.Locale

class LevelDetailActivity : AppCompatActivity() {

    private lateinit var tvLevelTitle: TextView
    private lateinit var tvStreak: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var tvSpeechResult: TextView
    private lateinit var btnMic: ImageButton

    private val REQUEST_CODE_SPEECH_INPUT = 100
    private val sampleQuestions = listOf(
        "The quick brown fox jumps over the lazy dog.",
        "She sells seashells by the seashore.",
        "I love practicing English every day.",
        "Artificial Intelligence is the future.",
        "Can you say this sentence clearly?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_detail)

        tvLevelTitle = findViewById(R.id.tvLevelTitle)
        tvStreak = findViewById(R.id.tvStreakDetail)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvSpeechResult = findViewById(R.id.tvSpeechResult)
        btnMic = findViewById(R.id.btnMic)

        // ✅ Get Level Name from Intent
        val levelName = intent.getStringExtra("LEVEL_NAME")
        tvLevelTitle.text = "🎯 $levelName"

        // ✅ Load Streak from SharedPreferences
        val prefs = getSharedPreferences("streakPrefs", MODE_PRIVATE)
        val streak = prefs.getInt("streakCount", 0)
        tvStreak.text = "🔥 Streak: $streak"

        // ✅ Pick a random question
        tvQuestion.text = sampleQuestions.random()

        // ✅ Start speech recognition when mic is clicked
        btnMic.setOnClickListener { startSpeechToText() }
    }

    private fun startSpeechToText() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")

            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!result.isNullOrEmpty()) {
                tvSpeechResult.text = "🗣 You said: ${result[0]}"
            }
        }
    }
}
