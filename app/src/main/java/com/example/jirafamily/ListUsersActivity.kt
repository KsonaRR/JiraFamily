package com.example.jirafamily

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Users
import com.example.jirafamily.adapters.ListUsersAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class ListUsersActivity : AppCompatActivity() {

    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<Users>()
    private lateinit var textLogo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_users)

        recyclerView = findViewById(R.id.ListUsersRecycleView)
        usersAdapter = ListUsersAdapter(usersList)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        textLogo = findViewById(R.id.TextLogo)

        loadAdminData()
        loadUsersFromFirestore()
    }

    private fun loadAdminData() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid
        if (adminId != null) {
            val db = FirebaseFirestore.getInstance()
            val adminRef = db.collection("admins").document(adminId)
            adminRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val nameFamily = document.getString("nameFamily")
                        textLogo.text = nameFamily
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }

    private fun loadUsersFromFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = document.toObject(Users::class.java)
                    usersList.add(user)
                    usersAdapter.notifyDataSetChanged()
                    updateAvatarForUser(user.userId, user.avatarUrl)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun updateAvatarForUser(userId: String, newAvatarUrl: String) {
        val position = usersList.indexOfFirst { it.userId == userId }
        if (position != -1) {
            usersAdapter.updateUserAvatar(position, newAvatarUrl)
        }
    }
}