package com.example.speakright

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val recycler = findViewById<RecyclerView>(R.id.recyclerTrending)
        recycler.layoutManager = LinearLayoutManager(this)

        // ✅ Sample topics (merged into one list)
        val trendingTopics = listOf(
            TrendingTopic("Global Warming", "Learn eco-related vocabulary"),
            TrendingTopic("AI in News", "Understand AI buzzwords"),
            TrendingTopic("Sports Buzz", "Vocabulary from today’s match"),
            TrendingTopic("Tech Trends", "Hot words from latest gadgets"),
            TrendingTopic("Global Politics", "Words in international news")
        )

        // ✅ Attach adapter
        val adapter = TrendingAdapter(trendingTopics) { topic ->
            // 👉 When user clicks a topic
            Toast.makeText(this, "Clicked: ${topic.title}", Toast.LENGTH_SHORT).show()
        }

        recycler.adapter = adapter
    }
}

// ✅ Data class should be separate (not inside onCreate)
data class TrendingTopic(
    val title: String,
    val description: String
)
