package com.example.speakright

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import com.example.speakright.ui.theme.NotificationWorker
import com.example.speakright.ui.theme.TrendingFragment

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        // Load Home (Trending) by default
        if (savedInstanceState == null) {
            loadFragment(TrendingFragment())
            bottomNav.selectedItemId = R.id.nav_home
        }

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(TrendingFragment())
                    true
                }
                R.id.nav_levels -> {
                    loadFragment(LevelsFragment())
                    true
                }
                R.id.nav_notifications -> {
                    loadFragment(NotificationsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(UserProfileFragment()) // ← New fragment for user details
                    true
                }
                else -> false
            }
        }

        // Schedule background notifications
        scheduleNotifications()
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun scheduleNotifications() {
        // Periodic WorkManager request
        val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            Duration.ofHours(1) // Change to Duration.ofMinutes(15) for testing
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SpeakRightHourlyNotifications",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    override fun onResume() {
        super.onResume()
        updateNotificationBadge()
    }

    private fun updateNotificationBadge() {
        val prefs = getSharedPreferences("AppNotifications", Context.MODE_PRIVATE)
        val count = prefs.getInt("notif_count", 0)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

        try {
            if (count > 0) {
                val badge = bottomNav.getOrCreateBadge(R.id.nav_notifications)
                badge.number = count
            } else {
                bottomNav.removeBadge(R.id.nav_notifications)
            }
        } catch (e: IllegalArgumentException) {
            // Avoid crash if theme is not MaterialComponents
            e.printStackTrace()
        }
    }
}
