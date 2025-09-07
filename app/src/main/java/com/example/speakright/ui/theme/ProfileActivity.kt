package com.example.speakright

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.Switch

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImage: ImageView
    private lateinit var editButton: ImageButton
    private lateinit var settingsButton: ImageButton
    private val PICK_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profileImage)
        editButton = findViewById(R.id.editButton)
        settingsButton = findViewById(R.id.settingsButton)

        // Click on profile image → upload new image
        profileImage.setOnClickListener {
            openGallery()
        }

        // Pencil icon → upload new image
        editButton.setOnClickListener {
            openGallery()
        }

        // ⚙️ Settings button → open bottom sheet
        settingsButton.setOnClickListener {
            showSettingsBottomSheet()
        }

        // ✅ Continue button → go to Dashboard
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // so user can’t return here with back button
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            profileImage.setImageURI(selectedImage)
        }
    }

    // ⬇️ Settings bottom sheet with dark mode toggle
    private fun showSettingsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_settings, null)
        bottomSheetDialog.setContentView(view)

        val switchTheme = view.findViewById<Switch>(R.id.switchTheme)
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)

        // Load saved theme state
        val isDarkMode = sharedPref.getBoolean("DarkMode", false)
        switchTheme.isChecked = isDarkMode

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                saveTheme(true)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                saveTheme(false)
            }
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun saveTheme(isDarkMode: Boolean) {
        val editor = getSharedPreferences("AppSettings", MODE_PRIVATE).edit()
        editor.putBoolean("DarkMode", isDarkMode)
        editor.apply()
    }
}
