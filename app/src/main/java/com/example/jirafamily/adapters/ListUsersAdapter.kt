package com.example.jirafamily.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.Users
import com.example.jirafamily.R


class ListUsersAdapter(private val users: List<Users>) :
    RecyclerView.Adapter<ListUsersAdapter.UsersViewHolder>() {
    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.profilePhotoImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val lastNameTextView: TextView = itemView.findViewById(R.id.lastNameTextView)
    }

    private var listener: OnUserClickListener? = null

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listofusers, parent, false)
        return UsersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val currentItem = users[position]
        holder.nameTextView.text = currentItem.name
        holder.lastNameTextView.text = currentItem.lastName
        Glide.with(holder.profilePhotoImageView.context)
            .load(currentItem.avatarUrl)
            .placeholder(R.drawable.account_circle)
            .into(holder.profilePhotoImageView)

        // Установка обработчика кликов на элемент списка
        holder.itemView.setOnClickListener {
            listener?.onUserClick(position)
        }
    }

    override fun getItemCount() = users.size
}
