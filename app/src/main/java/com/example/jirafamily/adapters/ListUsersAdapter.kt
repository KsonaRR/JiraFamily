package com.example.jirafamily.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.R

class ListUsersAdapter(private val userList: List<UserItem>) :
    RecyclerView.Adapter<ListUsersAdapter.UsersViewHolder>() {

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    private var listener: OnUserClickListener? = null

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }

    class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.profilePhotoImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val lastNameTextView: TextView = itemView.findViewById(R.id.lastNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listofusers, parent, false)
        return UsersViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.nameTextView.text = currentUser.name
        holder.lastNameTextView.text = currentUser.lastName
        Glide.with(holder.profilePhotoImageView.context)
            .load(currentUser.avatarUrl)
            .placeholder(R.drawable.account_circle)
            .into(holder.profilePhotoImageView)

        // Set click listener
        holder.itemView.setOnClickListener {
            listener?.onUserClick(position)
        }
    }

    override fun getItemCount() = userList.size
}
