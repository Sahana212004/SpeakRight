package com.example.speakright.ui.theme

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        val ratingBar = findViewById<RatingBar>(R.id.ratingBar)
        val etFeedback = findViewById<EditText>(R.id.etFeedback)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val rating = ratingBar.rating
            val feedbackText = etFeedback.text.toString().trim()

            if (feedbackText.isEmpty()) {
                Toast.makeText(this, "Please write your feedback!", Toast.LENGTH_SHORT).show()
            } else {
                // You can add code here to send feedback to a server or database
                Toast.makeText(this, "Thank you for your feedback! ⭐ $rating", Toast.LENGTH_LONG).show()
                etFeedback.text.clear()
                ratingBar.rating = 0f
            }
        }
    }
}
