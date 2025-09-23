package com.example.speakright.ui.theme

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.button.MaterialButton
import java.util.*

class SpeakingPracticeActivity : AppCompatActivity() {

    private lateinit var tvTopic: TextView
    private lateinit var tvTimer: TextView
    private lateinit var btnNextTopic: MaterialButton

    private val topics = listOf(
        "Describe your favorite hobby",
        "Talk about a memorable trip",
        "Describe your dream job",
        "Discuss your favorite book or movie",
        "Explain a recent achievement",
        "Talk about a person who inspires you",
        "Describe a challenge you overcame",
        "Discuss your goals for the next 5 years",
        "Describe your hometown",
        "Talk about a skill you want to learn"
    )

    private var currentTopicIndex = 0
    private var countDownTimer: CountDownTimer? = null
    private val speakingTimeMillis: Long = 180000 // 3 minutes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speaking_practice)

        tvTopic = findViewById(R.id.tvTopic)
        tvTimer = findViewById(R.id.tvTimer)
        btnNextTopic = findViewById(R.id.btnNextTopic)

        btnNextTopic.setOnClickListener {
            startSpeakingSession()
        }
    }

    private fun startSpeakingSession() {
        // Show next topic
        tvTopic.alpha = 0f
        tvTopic.text = topics[currentTopicIndex]

        // Fade-in animation
        val animator = ObjectAnimator.ofFloat(tvTopic, "alpha", 0f, 1f)
        animator.duration = 800
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()

        // Start timer
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(speakingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000
                tvTimer.text = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                currentTopicIndex = (currentTopicIndex + 1) % topics.size
                startSpeakingSession() // Automatically show next topic
            }
        }.start()

        btnNextTopic.text = "Next Topic"
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
