package com.example.speakright

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.network.RetrofitClient
import com.example.speakright.network.UserRequest
import com.example.speakright.network.UserResponse
import com.example.speakright.ui.theme.DashboardActivity
import com.example.speakright.ui.theme.ProfileActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val logoImage = findViewById<ImageView>(R.id.logoImage)

        // Animation
        logoImage.alpha = 0f
        logoImage.scaleX = 0.5f
        logoImage.scaleY = 0.5f
        logoImage.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(1000).setStartDelay(300).start()

        // --- Button Actions ---
        btnSignIn.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UserRequest(email, password)
            RetrofitClient.instance.signup(request).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "User already exists!", Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Retrofit", "Signup Network Error", t)
                }
            })
        }

        btnLogin.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UserRequest(email, password)
            RetrofitClient.instance.login(request).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Retrofit", "Login Network Error", t)
                }
            })
        }
    }
}
