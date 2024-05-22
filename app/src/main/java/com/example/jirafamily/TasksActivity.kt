package com.example.jirafamily


import TaskAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TasksActivity : AppCompatActivity() {

    private lateinit var createTaskButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        createTaskButton = findViewById(R.id.floatingActionButton)

        createTaskButton.setOnClickListener {
            startActivity(Intent(this, CreateTaskActivity::class.java))
        }
        recyclerView = findViewById(R.id.taskRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Получаем ссылку на базу данных
        database = FirebaseDatabase.getInstance().reference.child("tasks")

        // Инициализируем адаптер
        taskAdapter = TaskAdapter(ArrayList())

        // Устанавливаем адаптер в RecyclerView
        recyclerView.adapter = taskAdapter

        // Слушаем изменения в базе данных
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tasks = ArrayList<Task>()
                for (taskSnapshot in snapshot.children) {
                    val title = taskSnapshot.child("title").getValue(String::class.java) ?: ""
                    val description = taskSnapshot.child("description").getValue(String::class.java) ?: ""
                    val avatarUrl = taskSnapshot.child("avatarUrl").getValue(String::class.java)
                    val status = taskSnapshot.child("status").getValue(String::class.java) ?: ""
                    val priority = taskSnapshot.child("priority").getValue(Int::class.java) ?: 0
                    val task = Task(title, description, avatarUrl, status, priority)
                    tasks.add(task)
                }
                taskAdapter.updateTasks(tasks)
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })
    }
}