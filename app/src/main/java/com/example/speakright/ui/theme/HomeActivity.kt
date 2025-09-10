package com.example.speakright.ui.theme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recycler = findViewById<RecyclerView>(R.id.recyclerTrending)
        recycler.layoutManager = LinearLayoutManager(this)

        // ✅ Sample topics
        val trendingTopics = listOf(
            TrendingTopic("AI in 2025", "Understand AI buzzwords"),
            TrendingTopic("Climate Change", "Learn eco-related vocabulary"),
            TrendingTopic("Sports Update", "Get latest sports news")
        )

        val adapter = TrendingAdapter(trendingTopics)
        recycler.adapter = adapter
    }
}

data class TrendingTopic(
    val title: String,
    val description: String
)
