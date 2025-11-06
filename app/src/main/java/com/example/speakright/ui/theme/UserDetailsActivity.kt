package com.example.speakright.ui.theme

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.speakright.LoginActivity
import com.example.speakright.R
import com.example.speakright.DatabaseHelper
import de.hdodenhof.circleimageview.CircleImageView

class UserDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tvUserName: TextView
    private lateinit var tvContactInfo: TextView
    private lateinit var imgProfilePic: CircleImageView
    private lateinit var btnSignOut: Button
    private lateinit var btnSettings: ImageView
    private lateinit var Editbtn: Button
    private lateinit var tvTerms: TextView      // ✅ Added
    private lateinit var tvPrivacy: TextView    // ✅ Added

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        dbHelper = DatabaseHelper(this)

        // ✅ Initialize all views
        tvUserName = findViewById(R.id.tvUserName)
        tvContactInfo = findViewById(R.id.tvContactInfo)
        imgProfilePic = findViewById(R.id.imgProfilePic)
        btnSignOut = findViewById(R.id.btnSignOut)
        btnSettings = findViewById(R.id.btnSettings)
        Editbtn = findViewById(R.id.Editbtn)
        tvTerms = findViewById(R.id.tvTerms)          // ✅ Added
        tvPrivacy = findViewById(R.id.tvPrivacy)      // ✅ Added

        // Get the logged-in user's email passed via Intent
        val userEmail = intent.getStringExtra("email")

        if (userEmail != null) {
            loadUserData(userEmail)
        } else {
            Toast.makeText(this, "Error: No user data found", Toast.LENGTH_SHORT).show()
        }

        // 🔹 Sign Out button
        btnSignOut.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // 🔹 Edit Profile button
        Editbtn.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("email", userEmail)
            startActivity(intent)
        }

        // 🔹 Settings (Dark mode toggle)
        btnSettings.setOnClickListener {
            showDarkModeDialog()
        }

        // 🔹 Terms of Service
        tvTerms.setOnClickListener {
            showInfoDialog(
                title = "Terms of Service",
                message = """
                    • Use this app responsibly.
                    • Do not share your login details.
                    • Respect others when interacting.
                    • Data will be handled per the privacy policy.
                    • The app team is not liable for user misuse.
                """.trimIndent()
            )
        }

        // 🔹 Privacy Policy
        tvPrivacy.setOnClickListener {
            showInfoDialog(
                title = "Privacy Policy",
                message = """
                    • We collect only necessary user information.
                    • Your data is never sold to third parties.
                    • Profile photos are stored securely.
                    • You can delete your account anytime.
                    • App permissions are used only for functionality.
                """.trimIndent()
            )
        }
    }

    private fun loadUserData(email: String) {
        val user = dbHelper.getUserByEmail(email)

        if (user != null) {
            val fullName = "${user.firstName ?: ""} ${user.lastName ?: ""}".trim()
            tvUserName.text = if (fullName.isNotEmpty()) fullName else "Unknown User"
            tvContactInfo.text = user.phone ?: "No phone provided"

            if (user.profilePic != null) {
                val bitmap = BitmapFactory.decodeByteArray(user.profilePic, 0, user.profilePic.size)
                imgProfilePic.setImageBitmap(bitmap)
            } else {
                imgProfilePic.setImageResource(R.drawable.ic_person_placeholder)
            }
        } else {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    // 🔸 Function to show dark mode toggle dialog
    private fun showDarkModeDialog() {
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkMode = sharedPref.getBoolean("dark_mode", false)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Dark Mode")
        builder.setMessage("Enable or disable dark mode")
        builder.setPositiveButton(if (isDarkMode) "Disable" else "Enable") { _, _ ->
            val newMode = !isDarkMode
            AppCompatDelegate.setDefaultNightMode(
                if (newMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
            sharedPref.edit().putBoolean("dark_mode", newMode).apply()
            Toast.makeText(this, if (newMode) "Dark mode enabled" else "Dark mode disabled", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    // 🔸 Function to show info dialog (for terms/privacy)
    private fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
