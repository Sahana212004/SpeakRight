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

        btnSignIn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // ✅ Navigate to ProfileActivity after login
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish() // closes login page so user can’t go back
            }
        }

        btnGoogle.setOnClickListener {
            Toast.makeText(this, "Google sign-in clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
