package com.example.speakright.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R

class LevelsActivity : AppCompatActivity() {

    private lateinit var tvStreak: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels)

        tvStreak = findViewById(R.id.tvStreak)
        updateStreak()

        val levels = listOf(
            R.id.btnLevel1, R.id.btnLevel2, R.id.btnLevel3,
            R.id.btnLevel4, R.id.btnLevel5
        )

        for (levelId in levels) {
            val btn = findViewById<Button>(levelId)
            btn.setOnClickListener {
                // Navigate to LevelDetailActivity instead of just showing Toast
                val intent = Intent(this, LevelDetailActivity::class.java)
                intent.putExtra("LEVEL_NAME", btn.text.toString()) // Pass level name
                startActivity(intent)
            }
        }
    }

    private fun updateStreak() {
        val prefs = getSharedPreferences("streakPrefs", Context.MODE_PRIVATE)
        val lastOpen = prefs.getLong("lastOpenTime", 0L)
        var streak = prefs.getInt("streakCount", 0)

        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L
        var newStreak = streak

        newStreak = when {
            lastOpen == 0L -> 1 // first time opening
            now - lastOpen >= oneDay && now - lastOpen < 2 * oneDay -> streak + 1 // next day → increment
            now - lastOpen >= 2 * oneDay -> 1 // missed > 1 day → reset
            else -> streak // opened same day → keep
        }

        // ✅ Always save the last open time and streak count
        prefs.edit()
            .putLong("lastOpenTime", now)
            .putInt("streakCount", newStreak)
            .apply()

        // Show streak popup only if it increased
        if (newStreak > streak) {
            Toast.makeText(this, "🔥 Streak increased to $newStreak!", Toast.LENGTH_SHORT).show()
        }

        tvStreak.text = "🔥 Streak: $newStreak"
    }
}
