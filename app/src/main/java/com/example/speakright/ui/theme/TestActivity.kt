package com.example.speakright

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class TestActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var btnMic: ImageButton
    private val REQUEST_CODE_SPEECH_INPUT = 100

    private val questions = listOf(
        "What is Artificial Intelligence?",
        "Name one effect of Climate Change.",
        "Who won the last FIFA World Cup?"
    )
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        tvQuestion = findViewById(R.id.tvQuestion)
        btnMic = findViewById(R.id.btnMic)

        // Load first question
        tvQuestion.text = questions[currentQuestionIndex]

        // Mic button click
        btnMic.setOnClickListener {
            startSpeechToText()
        }
    }

    private fun startSpeechToText() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your answer...")

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = result?.get(0) ?: ""
            Toast.makeText(this, "You said: $spokenText", Toast.LENGTH_LONG).show()

            // 👉 Move to next question
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                tvQuestion.text = questions[currentQuestionIndex]
            } else {
                tvQuestion.text = "🎉 Test Finished!"
            }
        }
    }
}
