package com.example.jirafamily

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message
import com.example.jirafamily.DTO.Notification
import com.example.jirafamily.adapters.NotificationAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotificationAcitivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var tasksDatabase: DatabaseReference
    private lateinit var notificationsDatabase: DatabaseReference
    private lateinit var usersDatabase: DatabaseReference
    private val notifications = ArrayList<Notification>()
    private var currentFamilyName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        recyclerView = findViewById(R.id.NotificationRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(notifications)
        recyclerView.adapter = notificationAdapter

        tasksDatabase = FirebaseDatabase.getInstance().reference.child("tasks")
        notificationsDatabase = FirebaseDatabase.getInstance().reference.child("notifications")
        usersDatabase = FirebaseDatabase.getInstance().reference.child("users")

        fetchCurrentFamilyName()
    }

    private fun fetchCurrentFamilyName() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentFamilyName = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
                if (currentFamilyName != null) {
                    addTaskChildEventListener()
                } else {
                    databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                            currentFamilyName = adminSnapshot.child("nameOfFamily").getValue(String::class.java)
                            if (currentFamilyName != null) {
                                addTaskChildEventListener()
                            } else {
                                Toast.makeText(this@NotificationAcitivity, "Не удалось получить название семьи", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(adminDatabaseError: DatabaseError) {
                            Toast.makeText(this@NotificationAcitivity, "Ошибка при получении названия семьи: ${adminDatabaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NotificationAcitivity, "Ошибка при получении названия семьи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addTaskChildEventListener() {
        tasksDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val nameOfFamily = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
                if (nameOfFamily == currentFamilyName) {
                    val notificationMessage = "Добавлена новая задача"
                    val notification = Notification(notificationMessage, nameOfFamily!!)

                    notifications.add(notification)
                    notificationAdapter.notifyItemInserted(notifications.size - 1)
                    notificationsDatabase.push().setValue(notification)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        usersDatabase.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val nameOfFamily = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
                if (nameOfFamily == currentFamilyName) {
                    val notificationMessage = "Добавлен новый пользователь"
                    val notification = Notification(notificationMessage, nameOfFamily!!)

                    notifications.add(notification)
                    notificationAdapter.notifyItemInserted(notifications.size - 1)
                    notificationsDatabase.push().setValue(notification)
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {}

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}