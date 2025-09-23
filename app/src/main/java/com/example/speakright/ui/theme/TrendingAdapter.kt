package com.example.speakright.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R

class TrendingAdapter(
    private val trendingList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trending, parent, false)
        return TrendingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val item = trendingList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = trendingList.size

    inner class TrendingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTrending: TextView = itemView.findViewById(R.id.tvTrendingItem)

        fun bind(item: String) {
            tvTrending.text = item
            itemView.setOnClickListener { onItemClick(item) }
        }
    }
}
