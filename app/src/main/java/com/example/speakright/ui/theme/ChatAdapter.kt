package com.example.speakright.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.speakright.R

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tvMessage)
    }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].sender == Sender.BOT) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0)
            BotViewHolder(inflater.inflate(R.layout.item_bot_message, parent, false))
        else
            UserViewHolder(inflater.inflate(R.layout.item_user_message, parent, false))
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is BotViewHolder) holder.tvMessage.text = msg.message
        else if (holder is UserViewHolder) holder.tvMessage.text = msg.message
    }
}
