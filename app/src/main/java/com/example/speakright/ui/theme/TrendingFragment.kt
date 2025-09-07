package com.example.speakright

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TrendingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trending, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerTrending)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val topics = listOf(
            TrendingTopic("AI in 2025", "Learn AI buzzwords"),
            TrendingTopic("Climate Change", "Eco-vocabulary"),
            TrendingTopic("Sports Update", "Words from today’s match")
        )

        recycler.adapter = TrendingAdapter(topics) { topic ->
            // handle click
        }

        return view
    }
}
