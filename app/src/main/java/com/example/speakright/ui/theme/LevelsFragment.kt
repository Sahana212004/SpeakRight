package com.example.speakright

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class LevelsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_levels, container, false)

        val takeTest = view.findViewById<TextView>(R.id.tvTakeTest)
        takeTest.setOnClickListener {
            // 👉 Go to TestActivity
            startActivity(Intent(requireContext(), TestActivity::class.java))
        }

        return view
    }
}
