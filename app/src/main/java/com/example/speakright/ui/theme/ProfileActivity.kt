package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.speakright.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var profilePhoto: ImageView
    private val PICK_IMAGE = 100
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        drawerLayout = findViewById(R.id.drawerLayout)

        val settingsButton = findViewById<ImageView>(R.id.settingsButton)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)
        profilePhoto = findViewById(R.id.profilePhoto)

        // Open/close settings drawer
        settingsButton.setOnClickListener {
            if (!drawerLayout.isDrawerOpen(findViewById(R.id.settingsDrawer))) {
                drawerLayout.openDrawer(findViewById(R.id.settingsDrawer))
            } else {
                drawerLayout.closeDrawer(findViewById(R.id.settingsDrawer))
            }
        }

        // Pick profile image
        profilePhoto.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery, PICK_IMAGE)
        }

        // Save button click
        btnSave.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val phone = etPhone.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phone.isNotEmpty()) {
                // Go to Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                if (firstName.isEmpty()) etFirstName.error = "Required"
                if (lastName.isEmpty()) etLastName.error = "Required"
                if (phone.isEmpty()) etPhone.error = "Required"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE) {
            imageUri = data?.data
            profilePhoto.setImageURI(imageUri)
        }
    }
}
