package com.example.jirafamily

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message


class NotificationAcitivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val messagesList = listOf(
            Message(R.drawable.account_circle , "Dima" , "new task!", "12:00" , "Dima"),
            Message(R.drawable.account_circle , "Dima" , "new task!", "12:00" , "Dima"),
            Message(R.drawable.account_circle , "Dima" , "new task!", "12:00" , "Dima")

        )

        val recyclerView: RecyclerView = findViewById(R.id.NotificationRecycleView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }
}