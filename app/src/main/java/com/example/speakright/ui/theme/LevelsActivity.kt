package com.example.speakright.ui.theme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R
import com.google.android.material.card.MaterialCardView

class LevelsActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_levels) // updated to your new levels.xml



        val levelCards = listOf(
            R.id.cardSimple to "Simple",
            R.id.cardMedium to "Medium",
            R.id.cardComplex to "Complex"
        )

        for ((cardId, levelName) in levelCards) {
            val card = findViewById<MaterialCardView>(cardId)
            card.setOnClickListener {
                val intent = Intent(this, LevelDetailActivity::class.java)
                intent.putExtra("LEVEL_NAME", levelName)
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

        prefs.edit()
            .putLong("lastOpenTime", now)
            .putInt("streakCount", newStreak)
            .apply()

        if (newStreak > streak) {
            Toast.makeText(this, "🔥 Streak increased to $newStreak!", Toast.LENGTH_SHORT).show()
        }


    }
}
