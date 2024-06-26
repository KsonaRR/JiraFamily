package com.example.jirafamily.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message
import com.example.jirafamily.R

class MessageAdapter(private val chats: List<Message>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.profilePhotoImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val lastMessageTextView: TextView = itemView.findViewById(R.id.lastMessageTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentItem = chats[position]

        holder.profilePhotoImageView.setImageResource(currentItem.profilePhoto)
        holder.nameTextView.text = currentItem.name
        holder.lastMessageTextView.text = currentItem.lastMessage
        holder.timeTextView.text = currentItem.time

        holder.itemView.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun getItemCount() = chats.size
}


