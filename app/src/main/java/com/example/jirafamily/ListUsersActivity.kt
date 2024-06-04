package com.example.jirafamily

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.adapters.ListUsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListUsersActivity : AppCompatActivity() {
    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<UserItem>()
    private lateinit var textLogo: TextView
    private lateinit var currentUserID: String
    private lateinit var currentAdminID: String

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

        // Начнем загрузку пользователей, у которых adminId такой же, как id текущего администратора
        loadUsersWithSameAdminId()
    }

    private fun loadUsersWithSameAdminId() {
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Загружаем данные текущего пользователя (администратора)
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentAdmin = snapshot.getValue(UserItem::class.java)
                currentAdmin?.let {
                    currentAdminID = it.id ?: ""
                    // Загружаем всех пользователей с таким же adminId, как у текущего администратора
                    databaseReference.child("users").orderByChild("adminId").equalTo(currentAdminID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val user = snapshot.getValue(UserItem::class.java)
                                user?.let {
                                    usersList.add(it)
                                    // Проверяем ссылку на изображение пользователя

                                    val imageUrl = it.avatar
                                    Toast.makeText(this@ListUsersActivity, "URL изображения: ${imageUrl.toString()}", Toast.LENGTH_LONG).show()

                                }
                            }
                            // После загрузки пользователей, обновляем список
                            usersAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Обработка ошибок
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }
}
