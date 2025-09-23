package com.example.speakright.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.LoginActivity
import com.example.speakright.R

class UserDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val imgProfilePic = findViewById<ImageView>(R.id.imgProfilePic)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvContactInfo = findViewById<TextView>(R.id.tvContactInfo)
        val tvTerms = findViewById<TextView>(R.id.tvTerms)
        val tvPrivacy = findViewById<TextView>(R.id.tvPrivacy)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)

        // Static user info (can later be dynamic via Intent extras)
        tvUserName.text = "John Doe"
        tvContactInfo.text = "johndoe@example.com"

        // Optional: Clickable Terms and Privacy
        tvTerms.setOnClickListener {
            Toast.makeText(this, "Terms of Service clicked", Toast.LENGTH_SHORT).show()
        }

        tvPrivacy.setOnClickListener {
            Toast.makeText(this, "Privacy Policy clicked", Toast.LENGTH_SHORT).show()
        }

        // Sign Out button
        btnSignOut.setOnClickListener {
            // Go back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
