package com.example.speakright

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val btnGoogle = findViewById<Button>(R.id.btnGoogle)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // 👉 Sign Up / Sign In → go to ProfileActivity
        btnSignIn.setOnClickListener {
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

        // 👉 Login → go to DashboardActivity
        btnLogin.setOnClickListener {
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

        // 👉 Google button (dummy action for now)
        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google sign-in clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
