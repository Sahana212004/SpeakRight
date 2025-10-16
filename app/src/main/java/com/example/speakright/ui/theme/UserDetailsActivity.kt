package com.example.speakright.ui.theme

import android.graphics.BitmapFactory
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.speakright.LoginActivity
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        dbHelper = DatabaseHelper(this)
        tvUserName = findViewById(R.id.tvUserName)
        tvContactInfo = findViewById(R.id.tvContactInfo)
        imgProfilePic = findViewById(R.id.imgProfilePic)
        btnSignOut = findViewById(R.id.btnSignOut)
        btnSettings = findViewById(R.id.btnSettings)

        // Get the logged-in user's email passed via Intent
        val userEmail = intent.getStringExtra("email")

        if (userEmail != null) {
            loadUserData(userEmail)
        } else {
            Toast.makeText(this, "Error: No user data found", Toast.LENGTH_SHORT).show()
        }

        btnSignOut.setOnClickListener {
            // Clear session
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            Toast.makeText(this, "Signed out!", Toast.LENGTH_SHORT).show()

            // Go back to login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSettings.setOnClickListener {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        }

        Editbtn = findViewById(R.id.Editbtn)

        Editbtn.setOnClickListener {
            // Open ProfileActivity with the current user's email
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("email", userEmail) // pass the logged-in user email
            startActivity(intent)
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
}
