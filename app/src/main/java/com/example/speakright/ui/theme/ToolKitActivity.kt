package com.example.speakright.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R

class ToolKitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toolkit)

        val btnQuiz = findViewById<Button>(R.id.btnQuiz)
        val btnDictation = findViewById<Button>(R.id.btnDictation)
        val btnPractice = findViewById<Button>(R.id.btnPractice)

        btnQuiz.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        btnDictation.setOnClickListener {
            startActivity(Intent(this, DictationActivity::class.java))
        }

        btnPractice.setOnClickListener {
            startActivity(Intent(this, PracticeActivity::class.java))
        }
    }
}
