package com.example.speakright

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.ui.theme.DashboardActivity
import com.example.speakright.ui.theme.ProfileActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var btnSignIn: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)
        if (savedEmail != null) {
            // User is already logged in, go to Dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        // 2️⃣ Normal setup if no session

        setContentView(R.layout.activity_login)

        dbHelper = DatabaseHelper(this)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        btnSignIn = findViewById(R.id.btnSignIn)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.validateUser(userEmail, userPassword)) {
                    saveUserSession(userEmail)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnSignIn.setOnClickListener {
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.userExists(userEmail)) {
                    // If user exists, log them in
                    saveUserSession(userEmail)
                    Toast.makeText(this, "User already exists, logging in...", Toast.LENGTH_SHORT).show()
                    goToDashboard()
                } else {
                    // New user → sign up
                    val inserted = dbHelper.insertUser(userEmail, userPassword)
                    if (inserted) {
                        saveUserSession(userEmail)
                        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                        goToProfile()
                    } else {
                        Toast.makeText(this, "Error creating account", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveUserSession(email: String) {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        sharedPref.edit().putString("email", email).apply()
    }

    private fun goToDashboard() {
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }

    private fun goToProfile() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}
