package com.example.speakright

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.ui.theme.DashboardActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val logoImage = findViewById<ImageView>(R.id.logoImage)

        // 🔹 Logo Animation (fade-in + zoom effect)
        logoImage.alpha = 0f
        logoImage.scaleX = 0.5f
        logoImage.scaleY = 0.5f
        logoImage.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1000)
            .setStartDelay(300)
            .start()

        // 🔹 Reusable function for button animation (click effect)
        fun animateButton(button: Button) {
            button.setOnClickListener {
                button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).withEndAction {
                    button.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }.start()

                // Perform actual action after animation
                when (button.id) {
                    R.id.btnSignIn -> {
                        val email = emailEditText.text.toString().trim()
                        val password = passwordEditText.text.toString().trim()
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this, com.example.speakright.ui.theme.ProfileActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    R.id.btnLogin -> {
                        val email = emailEditText.text.toString().trim()
                        val password = passwordEditText.text.toString().trim()
                        if (email.isEmpty() || password.isEmpty()) {
                            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(this, DashboardActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                    R.id.btnGoogle -> {
                        Toast.makeText(this, "Google sign-in clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Apply animations to all buttons
        animateButton(btnSignIn)
        animateButton(btnLogin)
        animateButton(btnGoogle)
    }
}
