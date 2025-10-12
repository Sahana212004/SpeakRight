package com.example.speakright.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.LoginActivity
import com.example.speakright.R

class UserDetailsActivity : AppCompatActivity() {

    private var isDarkMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        val imgProfilePic = findViewById<ImageView>(R.id.imgProfilePic)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        val tvContactInfo = findViewById<TextView>(R.id.tvContactInfo)
        val tvTerms = findViewById<TextView>(R.id.tvTerms)
        val tvPrivacy = findViewById<TextView>(R.id.tvPrivacy)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)

        // Static user info (can later be dynamic via Intent extras)
        tvUserName.text = "John Doe"
        tvContactInfo.text = "+91 9876543210"

        // Settings Button -> Show Dark/Light Mode toggle
        btnSettings.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
            val switchTheme = dialogView.findViewById<Switch>(R.id.switchTheme)

            switchTheme.isChecked = isDarkMode

            val dialog = AlertDialog.Builder(this)
                .setTitle("Settings")
                .setView(dialogView)
                .setPositiveButton("OK") { _, _ ->
                    isDarkMode = switchTheme.isChecked
                    if (isDarkMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
                .setNegativeButton("Cancel", null)
                .create()
            dialog.show()
        }

        // Terms of Service -> Show Dialog
        tvTerms.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Terms of Service")
                .setMessage("Here are the terms of service...\n\n1. You must use the app responsibly.\n2. Do not misuse data.\n3. Respect privacy of others.")
                .setPositiveButton("OK", null)
                .show()
        }

        // Privacy Policy -> Show Dialog
        tvPrivacy.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Privacy Policy")
                .setMessage("We value your privacy.\n\n1. We do not share your data.\n2. Your personal info is secured.\n3. You can request deletion anytime.")
                .setPositiveButton("OK", null)
                .show()
        }

        // Sign Out button
        btnSignOut.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
