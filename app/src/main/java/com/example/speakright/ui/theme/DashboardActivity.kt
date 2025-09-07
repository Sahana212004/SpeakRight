package com.example.speakright

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

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
                    loadFragment(NotificationsFragment()) // create fragment later
                    true
                }
                R.id.nav_profile -> {
                    // 👉 Open ProfileActivity instead of loading fragment
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
