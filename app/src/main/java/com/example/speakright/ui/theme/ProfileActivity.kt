package com.example.speakright.ui.theme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.DatabaseHelper
import com.example.speakright.R
import com.example.speakright.User
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnSave: Button
    private lateinit var profilePhoto: CircleImageView
    private var selectedImageBytes: ByteArray? = null
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        dbHelper = DatabaseHelper(this)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etPhone = findViewById(R.id.etPhone)
        btnSave = findViewById(R.id.btnSave)
        profilePhoto = findViewById(R.id.profilePhoto)

        // Get email from intent OR SharedPreferences
        userEmail = intent.getStringExtra("email")
            ?: getSharedPreferences("UserSession", MODE_PRIVATE).getString("email", "")!!

        // Load user data if exists
        loadUserData()

        profilePhoto.setOnClickListener { pickImageFromGallery() }

        btnSave.setOnClickListener {
            val first = etFirstName.text.toString()
            val last = etLastName.text.toString()
            val phone = etPhone.text.toString()

            if (first.isEmpty() || last.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                val updated = dbHelper.updateUserProfile(userEmail, first, last, phone, selectedImageBytes)
                if (updated) {
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadUserData() {
        val user: User? = dbHelper.getUserByEmail(userEmail)
        if (user != null) {
            etFirstName.setText(user.firstName)
            etLastName.setText(user.lastName)
            etPhone.setText(user.phone)
            user.profilePic?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                profilePhoto.setImageBitmap(bitmap)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            val inputStream = contentResolver.openInputStream(uri!!)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            profilePhoto.setImageBitmap(bitmap)
            selectedImageBytes = bitmapToByteArray(bitmap)
        }
    }

    private fun bitmapToByteArray(bitmap: android.graphics.Bitmap): ByteArray {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)
        val stream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream) // Compress to JPEG with 80% quality
        return stream.toByteArray()
    }
}
