package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.adapters.ListOfUserAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListForUsersActivity : AppCompatActivity() {
    private lateinit var usersAdapter: ListOfUserAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<UserItem>()
    private lateinit var textLogo: TextView
    private lateinit var currentUserID: String
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_for_users)

        recyclerView = findViewById(R.id.ListUsersRecycleView)
        usersAdapter = ListOfUserAdapter(usersList)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        textLogo = findViewById(R.id.TextLogo)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)
        notificationButton = findViewById(R.id.imageView5)
        profileButton = findViewById(R.id.imageView4)

        messageButton.setOnClickListener {
            startActivity(Intent(this, ListForChatsActivity::class.java))
        }
        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        tasksButton.setOnClickListener {
            startActivity(Intent(this, ListTaskForUsersActivity::class.java))
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserID = currentUser?.uid ?: ""

        // Начнем загрузку всех пользователей приложения
        loadUsersWithSameNameOfFamily()
    }

    private fun loadUsersWithSameNameOfFamily() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Получаем название семьи текущего пользователя
        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUserFamilyName = dataSnapshot.child("nameOfFamily").getValue(String::class.java)

                // Фильтруем пользователей по названию семьи текущего пользователя
                databaseReference.child("users")
                    .orderByChild("nameOfFamily")
                    .equalTo(currentUserFamilyName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val user = snapshot.getValue(UserItem::class.java)
                                user?.let {
                                    // Проверяем, что пользователь не текущий пользователь
                                    if (user.id != currentUserID) {
                                        // Добавляем пользователей с одинаковым nameOfFamily в список
                                        usersList.add(it)
                                    }
                                }
                            }
                            // Обновляем список после загрузки данных
                            usersAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Обработка ошибок
                        }
                    })

                // Получаем администраторов с тем же названием семьи
                databaseReference.child("admins")
                    .orderByChild("nameOfFamily")
                    .equalTo(currentUserFamilyName)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                val admin = snapshot.getValue(UserItem::class.java)
                                admin?.let {
                                    usersList.add(it)
                                }
                            }
                            // Обновляем список после загрузки данных
                            usersAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Обработка ошибок
                        }
                    })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }
}