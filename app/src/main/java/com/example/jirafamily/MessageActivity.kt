package com.example.jirafamily

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message
import com.example.jirafamily.adapters.MessageAdapter

class MessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        val messagesList = listOf(
            Message(R.drawable.account_circle , "Dima" , "Hello!", "12:00" , "Dima"),
            Message(R.drawable.account_circle , "Dima" , "Hello!", "12:00" , "Dima"),
            Message(R.drawable.account_circle , "Dima" , "Hello!", "12:00" , "Dima")

        )

        val recyclerView: RecyclerView = findViewById(R.id.MessageRecycleView)
        recyclerView.adapter = MessageAdapter(messagesList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }
}