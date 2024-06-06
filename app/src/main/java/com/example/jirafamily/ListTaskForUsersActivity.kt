package com.example.jirafamily


import UserTaskAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListTaskForUsersActivity : AppCompatActivity(), UserTaskAdapter.OnTaskClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: UserTaskAdapter
    private lateinit var database: DatabaseReference
    private var currentFamilyName: String? = null
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_task_for_users)

        recyclerView = findViewById(R.id.taskRecycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Создаем адаптер с пустым списком задач и текущим активити в качестве слушателя
        taskAdapter = UserTaskAdapter(ArrayList(), this@ListTaskForUsersActivity)
        recyclerView.adapter = taskAdapter
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

        fetchCurrentFamilyName()
    }

    // Реализация интерфейса OnTaskClickListener
    override fun onTaskClick(task: Task) {
        val intent = Intent(this, CompTaskActivity::class.java)
        intent.putExtra("task_id", task.id)
        startActivity(intent)
    }



    private fun fetchCurrentFamilyName() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child("users").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentFamilyName = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
                if (currentFamilyName != null) {
                    loadTasksByFamilyName()
                } else {
                    databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(adminSnapshot: DataSnapshot) {
                            currentFamilyName = adminSnapshot.child("nameOfFamily").getValue(String::class.java)
                            if (currentFamilyName != null) {
                                loadTasksByFamilyName()
                            } else {
                                Toast.makeText(this@ListTaskForUsersActivity, "Не удалось получить название семьи", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(adminDatabaseError: DatabaseError) {
                            Toast.makeText(this@ListTaskForUsersActivity, "Ошибка при получении названия семьи: ${adminDatabaseError.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@ListTaskForUsersActivity, "Ошибка при получении названия семьи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
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
                            // Проверяем, что статус задачи не равен 1, и добавляем только такие задачи
                            if (it.status != 1) {
                                tasks.add(it)
                            }
                        }
                    }
                    taskAdapter.updateTasks(tasks)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ListTaskForUsersActivity, "Ошибка при загрузке задач: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
