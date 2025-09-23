package com.example.speakright.ui.theme

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R
import com.google.android.material.card.MaterialCardView

class LevelsAdapter(
    private val context: Context,
    private val levels: List<Int>
) : RecyclerView.Adapter<LevelsAdapter.LevelViewHolder>() {

    inner class LevelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardLevel: MaterialCardView = itemView.findViewById(R.id.cardLevel)
        val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LevelViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_level_card, parent, false)
        return LevelViewHolder(view)
    }

    override fun getItemCount(): Int = levels.size

    override fun onBindViewHolder(holder: LevelViewHolder, position: Int) {
        val levelNumber = levels[position]
        holder.tvLevel.text = levelNumber.toString()

        holder.cardLevel.setOnClickListener {
            val intent = Intent(context, LevelDetailActivity::class.java)
            intent.putExtra("LEVEL_NUMBER", levelNumber)
            context.startActivity(intent)
        }
    }
}
