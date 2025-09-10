package com.example.speakright

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserProfileFragment : Fragment() {

    private lateinit var imgProfilePic: ImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvContactInfo: TextView
    private lateinit var tvTerms: TextView
    private lateinit var tvPrivacy: TextView
    private lateinit var btnSignOut: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        imgProfilePic = view.findViewById(R.id.imgProfilePic)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvContactInfo = view.findViewById(R.id.tvContactInfo)
        tvTerms = view.findViewById(R.id.tvTerms)
        tvPrivacy = view.findViewById(R.id.tvPrivacy)
        btnSignOut = view.findViewById(R.id.btnSignOut)

        // Load saved user info
        val prefs = requireContext().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)

        // Load profile picture
        prefs.getString("profile_image_uri", null)?.let {
            imgProfilePic.setImageURI(Uri.parse(it))
        }

        // Load name and email
        val firstName = prefs.getString("first_name", "") ?: ""
        val lastName = prefs.getString("last_name", "") ?: ""
        val email = prefs.getString("email", "") ?: ""

        tvUserName.text = "$firstName $lastName"
        tvContactInfo.text = email

        tvTerms.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/terms"))
            startActivity(intent)
        }

        tvPrivacy.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.example.com/privacy"))
            startActivity(intent)
        }

        btnSignOut.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        return view
    }
}
