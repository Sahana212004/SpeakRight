package com.example.speakright.ui.theme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R

class TrendingAdapter(
    private val topics: List<TrendingTopic>
) : RecyclerView.Adapter<TrendingAdapter.TopicViewHolder>() {

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trending_topic, parent, false)
        return TopicViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val topic = topics[position]
        holder.tvTitle.text = topic.title
        holder.tvDescription.text = topic.description

        // 👉 Handle item click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TopicDetailActivity::class.java)
            intent.putExtra("TOPIC_TITLE", topic.title)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = topics.size
}
