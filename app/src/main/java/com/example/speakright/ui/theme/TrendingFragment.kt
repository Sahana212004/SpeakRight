package com.example.speakright.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R

class TrendingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trending, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerTrending)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val trendingTopics = listOf(
            TrendingTopic("AI in 2025", "Understand AI buzzwords"),
            TrendingTopic("Climate Change", "Learn eco-related vocabulary"),
            TrendingTopic("Sports Update", "Get latest sports news")
        )

        val adapter = TrendingAdapter(trendingTopics)
        recycler.adapter = adapter

        return view
    }
}
