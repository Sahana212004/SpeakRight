package com.example.speakright

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotificationsFragment : Fragment() {

    private val PREFS = "AppNotifications"
    private val KEY_COUNT = "notif_count"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val imgBell = view.findViewById<ImageView>(R.id.imgBell)
        val tvCount = view.findViewById<TextView>(R.id.tvNotifCount)
        val btnClear = view.findViewById<Button>(R.id.btnClearNotifications)

        // Load count from SharedPreferences
        val prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_COUNT, 0)
        tvCount.text = if (count > 0) "You have $count new notifications" else "No new notifications"

        // Update bottom nav badge (if DashboardActivity has BottomNavigationView with id bottomNav)
        updateBadge(count)

        btnClear.setOnClickListener {
            // Clear count
            prefs.edit().putInt(KEY_COUNT, 0).apply()
            tvCount.text = "No new notifications"
            updateBadge(0)
        }

        return view
    }

    private fun updateBadge(count: Int) {
        activity?.findViewById<BottomNavigationView?>(R.id.bottomNav)?.let { nav ->
            if (count > 0) {
                val badge = nav.getOrCreateBadge(R.id.nav_notifications)
                badge.number = count
            } else {
                nav.removeBadge(R.id.nav_notifications)
            }
        }
    }
}
