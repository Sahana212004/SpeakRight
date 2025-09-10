package com.example.speakright.ui.theme

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.speakright.R

class TopicDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvContent = findViewById<TextView>(R.id.tvDetailContent)

        val title = intent.getStringExtra("TOPIC_TITLE") ?: "Topic"
        tvTitle.text = title

        // Load content based on the topic clicked
        val content = when (title) {
            "AI in 2025" -> """
                🤖 AI Buzzwords:
                - Generative AI
                - Large Language Models
                - Autonomous Agents
                - Neural Networks
                - Explainable AI
            """.trimIndent()

            "Climate Change" -> """
                🌍 Climate Change Vocabulary:
                - Carbon Footprint
                - Renewable Energy
                - Global Warming
                - Sustainability
                - Greenhouse Gases
            """.trimIndent()

            "Sports Update" -> """
                🏆 Latest Sports Updates:
                - Cricket: India vs Australia Test Match Highlights
                - Football: Champions League group stage updates
                - Tennis: US Open results coming in
            """.trimIndent()

            else -> "No details available for this topic."
        }

        tvContent.text = content
    }
}
