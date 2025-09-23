package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class PronunciationPracticeActivity : AppCompatActivity() {

    private val REQUEST_CODE_SPEECH = 100
    private lateinit var tvQuestion: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var fabMic: FloatingActionButton
    private lateinit var btnNext: MaterialButton

    private val questions = listOf(
        "Hello", "Good morning", "Thank you", "Please", "Excuse me",
        "Yes", "No", "How are you", "I am fine", "Good night",
        "Welcome", "Sorry", "See you", "Congratulations", "Happy birthday",
        "I love you", "Help", "Where is the bathroom", "What is your name", "Nice to meet you"
    )

    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pronunciation_practice) // Make sure XML matches

        tvQuestion = findViewById(R.id.tvQuestion)
        progressBar = findViewById(R.id.progressBar)
        fabMic = findViewById(R.id.fabMic)
        btnNext = findViewById(R.id.btnNext)

        progressBar.max = questions.size
        showQuestion()

        fabMic.setOnClickListener { startSpeechRecognition() }

        btnNext.setOnClickListener {
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                showQuestion()
                btnNext.isEnabled = false
            } else {
                Toast.makeText(this, "🎉 Practice Completed!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showQuestion() {
        tvQuestion.text = questions[currentQuestionIndex]
        progressBar.progress = currentQuestionIndex
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH)
        } catch (e: Exception) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SPEECH && resultCode == Activity.RESULT_OK) {
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val userAnswer = matches[0].lowercase(Locale.getDefault())
                val correctAnswer = questions[currentQuestionIndex].lowercase(Locale.getDefault())

                if (userAnswer == correctAnswer) {
                    Toast.makeText(this, "✅ Correct!", Toast.LENGTH_SHORT).show()
                    btnNext.isEnabled = true
                } else {
                    Toast.makeText(this, "❌ Try Again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
