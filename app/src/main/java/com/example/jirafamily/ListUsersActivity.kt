package com.example.jirafamily

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Users
import com.example.jirafamily.adapters.ListUsersAdapter
import com.example.jirafamily.adapters.UserAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListUsersActivity : AppCompatActivity(), UserAdapter.OnUserClickListener,
    ListUsersAdapter.OnUserClickListener {

    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<Users>()
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

        var databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Слушаем изменения в базе данных
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(Users::class.java)
                    // Проверяем, что пользователь не текущий пользователь
                    if (user?.id != currentUserID) {
                        user?.let { usersList.add(it) }
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    override fun onUserClick(position: Int) {
        // Получить выбранного пользователя из списка
        val selectedUser = usersList[position]

        // Создать Intent для открытия ChatActivity и передать данные о пользователе
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("recipientUserId", usersList.get(position).id)
        intent.putExtra("userName", usersList.get(position).name)
        intent.putExtra("userId", selectedUser.id)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "ListUsersActivity"
    }
}