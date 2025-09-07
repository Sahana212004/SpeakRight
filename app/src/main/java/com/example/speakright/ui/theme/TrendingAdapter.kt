package com.example.speakright

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrendingAdapter(
    private val topics: List<TrendingTopic>,
    private val onClick: (TrendingTopic) -> Unit
) : RecyclerView.Adapter<TrendingAdapter.TopicViewHolder>() {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTopicTitle)
        val desc: TextView = itemView.findViewById(R.id.tvTopicDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position]
        holder.title.text = topic.title
        holder.desc.text = topic.description
        holder.itemView.setOnClickListener { onClick(topic) }
    }

    override fun getItemCount() = topics.size
}
