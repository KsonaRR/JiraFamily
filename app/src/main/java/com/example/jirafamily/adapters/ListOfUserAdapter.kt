package com.example.jirafamily.adapters

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.R
import com.squareup.picasso.Picasso

class ListOfUserAdapter(private val userList: List<UserItem>) :
    RecyclerView.Adapter<ListOfUserAdapter.UsersViewHolder>() {

    interface OnUserClickListener {
        fun onUserClick(position: Int)
    }

    private var listener: OnUserClickListener? = null

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
        val currentItem = userList[position]
        holder.nameTextView.text = currentItem.name
        holder.lastNameTextView.text = currentItem.lastName

        // Загрузка и отображение фотографии пользователя с использованием Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.avatar)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.account_circle)
            .into(holder.profilePhotoImageView)

        // Установка обработчика кликов
        holder.itemView.setOnClickListener {
            listener?.onUserClick(position)
        }
    }

    override fun getItemCount() = userList.size

    fun setOnUserClickListener(listener: OnUserClickListener) {
        this.listener = listener
    }
}