package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message
import com.example.jirafamily.adapters.MessageAdapter
import com.google.firebase.firestore.FirebaseFirestore

class MessageActivity : AppCompatActivity(), MessageAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private var messageList = mutableListOf<Message>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        recyclerView = findViewById(R.id.MessageRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(messageList, this) // Передаем текущий контекст и этот класс как слушатель
        recyclerView.adapter = messageAdapter

        // Получение данных админов из Firestore и заполнение списка сообщений
        loadAdminsFromFirestore()
    }

    private fun loadAdminsFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        val adminsRef = db.collection("admins")

        // Загрузка данных админов из Firestore
        adminsRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val profilePhoto = R.drawable.account_circle // Замените на изображение профиля из базы данных
                    val name = document.getString("name") ?: "Unknown"
                    val time = "00:00" // Время пока оставим по умолчанию
                    val lastMessage = "123" // Пока оставим пустым
                    val author = "Admin" // Предполагаем, что сообщение отправлено админом

                    val message = Message(profilePhoto, name, lastMessage, time, author)
                    messageList.add(message)
                }

                // Обновление RecyclerView после загрузки данных админов
                messageAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Обработка ошибки загрузки данных из Firestore
                exception.printStackTrace()
            }
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}

