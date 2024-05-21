package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.adapters.ListUsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListUsersActivity : AppCompatActivity(), ListUsersAdapter.OnUserClickListener {

    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<UserItem>()
    private lateinit var textLogo: TextView
    private lateinit var currentUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        recyclerView = findViewById(R.id.ListUsersRecycleView)
        usersAdapter = ListUsersAdapter(usersList)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        textLogo = findViewById(R.id.TextLogo)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserID = currentUser?.uid ?: ""

        usersAdapter.setOnUserClickListener(this)

        val databaseReference = FirebaseDatabase.getInstance().reference

        // Слушаем изменения в базе данных для пользователей
        databaseReference.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadUsers(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })

        // Слушаем изменения в базе данных для администраторов
        databaseReference.child("admins").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                loadAdmins(dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    // Метод для загрузки пользователей из снимка данных
    private fun loadUsers(dataSnapshot: DataSnapshot) {
        for (snapshot in dataSnapshot.children) {
            val user = snapshot.getValue(UserItem::class.java)
            if (user?.id != currentUserID) {
                user?.let { usersList.add(it) }
            }
        }
        usersAdapter.notifyDataSetChanged()
    }

    // Метод для загрузки администраторов из снимка данных
    private fun loadAdmins(dataSnapshot: DataSnapshot) {
        for (snapshot in dataSnapshot.children) {
            val admin = snapshot.getValue(UserItem::class.java)
            if (admin?.id != currentUserID) {
                admin?.let { usersList.add(it) }
            }
        }
        usersAdapter.notifyDataSetChanged()
    }

    override fun onUserClick(position: Int) {
        // Получить выбранного пользователя из списка
        val selectedUser = usersList[position]

        // Создать Intent для открытия ChatActivity и передать данные о пользователе
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("recipientUserId", selectedUser.id)
        intent.putExtra("userName", "${selectedUser.name} ${selectedUser.lastName}")
        intent.putExtra("userId", selectedUser.id)
        startActivity(intent)
    }
}