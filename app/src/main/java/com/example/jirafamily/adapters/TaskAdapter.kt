package com.example.jirafamily.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.R

class TaskAdapter(private val tasks: List<Task>) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskNameTextView: TextView = itemView.findViewById(R.id.taskNameTextView)
        val taskPriorityImageView: ImageView = itemView.findViewById(R.id.taskPriorityImageView)
        val taskAvatarImageView: ImageView = itemView.findViewById(R.id.taskAvatarImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]

        holder.taskNameTextView.text = currentTask.title
        // Пример загрузки и отображения заглушки для приоритета
        holder.taskPriorityImageView.setImageResource(R.drawable.account_circle)
        // Пример загрузки и отображения заглушки для аватарки
        holder.taskAvatarImageView.setImageResource(R.drawable.account_circle)
    }

    override fun getItemCount() = tasks.size
}
