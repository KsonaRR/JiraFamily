package com.example.jirafamily

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.UserItem
import com.example.jirafamily.adapters.ListUsersAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditUsersActivity : AppCompatActivity(), ListUsersAdapter.OnUserClickListener {

    private lateinit var usersAdapter: ListUsersAdapter
    private lateinit var recyclerView: RecyclerView
    private val usersList = mutableListOf<UserItem>()
    private lateinit var currentUserID: String
    private lateinit var currentUserAdminID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_users)

        recyclerView = findViewById(R.id.ListUsersRecycleView)
        usersAdapter = ListUsersAdapter(usersList)
        usersAdapter.setOnUserClickListener(this)
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUserID = currentUser?.uid ?: ""

        // Получить adminId текущего пользователя
        val databaseReference = FirebaseDatabase.getInstance().reference.child("admins").child(currentUserID)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentUser = dataSnapshot.getValue(UserItem::class.java)
                if (currentUser != null) {
                    currentUserAdminID = currentUser.id ?: ""
                    loadUsers(currentUserAdminID)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val user = usersList[position]
                deleteUser(user)
                usersList.removeAt(position)
                usersAdapter.notifyItemRemoved(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun loadUsers(adminId: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Загружаем всех пользователей с тем же adminId, что и текущий администратор
        databaseReference.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear() // Очищаем список перед загрузкой новых данных
                for (snapshot in dataSnapshot.children) {
                    val user = snapshot.getValue(UserItem::class.java)
                    if (user?.adminId == adminId) {
                        user?.let { usersList.add(it) }
                    }
                }
                usersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    private fun deleteUser(user: UserItem) {
        val databaseReference = FirebaseDatabase.getInstance().reference
        val userRef = databaseReference.child("users").child(user.id!!)
        userRef.removeValue()
    }

    override fun onUserClick(position: Int) {
        // Обработка кликов на элемент списка (если требуется)
    }
}
