package com.example.speakright.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.speakright.R
import com.google.android.material.card.MaterialCardView

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Play welcome Lottie animation
        findViewById<LottieAnimationView>(R.id.lottieWelcome).playAnimation()
        findViewById<TextView>(R.id.tvWelcome).startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        )

        // Initialize cards
        val cardHome = findViewById<MaterialCardView>(R.id.cardHome)
        val cardLevels = findViewById<MaterialCardView>(R.id.cardLevels)
        val cardFeedback = findViewById<MaterialCardView>(R.id.cardFeedback)
        val cardProfile = findViewById<MaterialCardView>(R.id.cardProfile)

        listOf(cardHome, cardLevels, cardFeedback, cardProfile).forEach { card ->
            card.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_up))
        }

        cardHome.setOnClickListener {
            Toast.makeText(this, "Opening Tool Box...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PracticeActivity::class.java))
        }

        cardLevels.setOnClickListener {
            Toast.makeText(this, "Opening Levels...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LevelsActivity::class.java))
        }

        cardFeedback.setOnClickListener {
            Toast.makeText(this, "Opening Feedback...", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, FeedbackActivity::class.java))
        }

        cardProfile.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val email = sharedPref.getString("email", null)
            if (email != null) {
                val intent = Intent(this, UserDetailsActivity::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No logged-in user found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
