package com.example.jirafamily.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Notification
import com.example.jirafamily.R

class NotificationAdapter(private val notification: List<Notification>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.profilePhotoImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val notificationTextView: TextView = itemView.findViewById(R.id.notificationTextView)
        val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = notification[position]

        holder.profilePhotoImageView.setImageResource(currentItem.profilePhoto)
        holder.nameTextView.text = currentItem.name
        holder.notificationTextView.text = currentItem.notification
        holder.timeTextView.text = currentItem.time
    }

    override fun getItemCount() = notification.size
}