package com.example.speakright.ui.theme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.card.MaterialCardView

class PracticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_practice)

        // Animate title
        val tvTitle = findViewById<TextView>(R.id.tvPracticeTitle)
        tvTitle.animate().alpha(1f).translationY(0f).setDuration(600).start()

        // Find cards
        val cardPronunciation = findViewById<MaterialCardView>(R.id.cardPronunciation)
        val cardSpeaking = findViewById<MaterialCardView>(R.id.cardSpeaking)
        val cardGrammar = findViewById<MaterialCardView>(R.id.cardGrammar)

        // Animate cards
        animateCard(cardPronunciation, 0)
        animateCard(cardSpeaking, 100)
        animateCard(cardGrammar, 200)

        // ✅ Corrected Click listeners
        cardPronunciation.setOnClickListener {
            val intent = Intent(this, PronunciationPracticeActivity::class.java)
            startActivity(intent)
        }

        cardSpeaking.setOnClickListener {
            val intent = Intent(this, SpeakingPracticeActivity::class.java)
            startActivity(intent)
        }

        cardGrammar.setOnClickListener {
            val intent = Intent(this, GrammarPracticeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun animateCard(card: View, delay: Long) {
        card.alpha = 0f
        card.translationY = 50f
        card.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(delay).start()
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
