package com.example.jirafamily

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class TasksActivity : AppCompatActivity() {

    private lateinit var createTaskButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var database: DatabaseReference
    private var currentFamilyName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        createTaskButton = findViewById(R.id.floatingActionButton)
        recyclerView = findViewById(R.id.taskRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(ArrayList())
        recyclerView.adapter = taskAdapter

        createTaskButton.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }

        // Получаем ссылку на базу данных
        fetchCurrentFamilyName()
    }

    private fun fetchCurrentFamilyName() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Получение имени семьи из таблицы пользователей
        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentFamilyName = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
                if (currentFamilyName != null) {
                    loadTasksByFamilyName()
                } else {
                    // Получение имени семьи из таблицы администраторов
                    databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                            currentFamilyName = adminSnapshot.child("nameOfFamily").getValue(String::class.java)
                            if (currentFamilyName != null) {
                                loadTasksByFamilyName()
                            } else {
                                Toast.makeText(this@TasksActivity, "Не удалось получить название семьи", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(adminDatabaseError: DatabaseError) {
                            Toast.makeText(this@TasksActivity, "Ошибка при получении названия семьи: ${adminDatabaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@TasksActivity, "Ошибка при получении названия семьи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun loadTasksByFamilyName() {
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child("tasks")
            .orderByChild("nameOfFamily")
            .equalTo(currentFamilyName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tasks = ArrayList<Task>()
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        task?.let {
                            tasks.add(it)
                        }
                    }
                    taskAdapter.updateTasks(tasks)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@TasksActivity, "Ошибка при загрузке задач: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
