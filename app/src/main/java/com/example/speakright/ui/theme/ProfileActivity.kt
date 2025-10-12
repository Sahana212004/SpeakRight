package com.example.speakright.ui.theme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.speakright.R
import de.hdodenhof.circleimageview.CircleImageView
import androidx.activity.result.contract.ActivityResultContracts

class ProfileActivity : AppCompatActivity() {

    private lateinit var profilePhoto: CircleImageView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etPhone: EditText

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(profilePhoto)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        profilePhoto = findViewById(R.id.profilePhoto)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etPhone = findViewById(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Profile image picker
        profilePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Save button (navigates to Dashboard if fields filled)
        btnSave.setOnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val phone = etPhone.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && phone.isNotEmpty()) {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("USERNAME", "$firstName $lastName")
                intent.putExtra("PHONE", phone)
                startActivity(intent)
                finish()
            } else {
                if (firstName.isEmpty()) etFirstName.error = "Required"
                if (lastName.isEmpty()) etLastName.error = "Required"
                if (phone.isEmpty()) etPhone.error = "Required"
            }
        }
    }
}
