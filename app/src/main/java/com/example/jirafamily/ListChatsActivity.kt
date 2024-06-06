package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.adapters.ListUsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListChatsActivity : AppCompatActivity(), ListUsersAdapter.OnUserClickListener {

    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<UserItem>()
    private lateinit var textLogo: TextView
    private lateinit var currentUserID: String
    private lateinit var currentAdminID: String
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        recyclerView = findViewById(R.id.ListUsersRecycleView)
        usersAdapter = ListUsersAdapter(usersList)
        usersAdapter.setOnUserClickListener(this)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        textLogo = findViewById(R.id.TextLogo)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)
        notificationButton = findViewById(R.id.imageView5)
        profileButton = findViewById(R.id.imageView4)

        messageButton.setOnClickListener {
            startActivity(Intent(this, ListChatsActivity::class.java))
        }
        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }
        profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileAdminActivity::class.java))
        }
        tasksButton.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserID = currentUser?.uid ?: ""

        // Начнем загрузку пользователей, у которых adminId такой же, как id текущего администратора
        loadUsersWithSameAdminId()
    }

    private fun loadUsersWithSameAdminId() {
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Загружаем данные текущего пользователя (администратора)
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
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

    override fun onUserClick(position: Int) {
        val selectedUser = usersList[position]
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("recipientUserId", selectedUser.id)
        intent.putExtra("userName", "${selectedUser.name} ${selectedUser.lastName}")
        intent.putExtra("userId", selectedUser.id)
        startActivity(intent)
    }
}
