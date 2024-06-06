package com.example.jirafamily

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*



    class TasksActivity : AppCompatActivity(), TaskAdapter.OnTaskDeleteListener {

        private lateinit var createTaskButton: FloatingActionButton
        private lateinit var recyclerView: RecyclerView
        private lateinit var taskAdapter: TaskAdapter
        private lateinit var database: DatabaseReference
        private var currentFamilyName: String? = null
        private lateinit var notificationButton: ImageView
        private lateinit var messageButton: ImageView
        private lateinit var tasksButton: ImageView
        private lateinit var profileButton: ImageView

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tasks)

            createTaskButton = findViewById(R.id.floatingActionButton)
            recyclerView = findViewById(R.id.taskRecycleView)
            recyclerView.layoutManager = LinearLayoutManager(this)
            taskAdapter = TaskAdapter(ArrayList(), this)
            recyclerView.adapter = taskAdapter
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

            // Добавляем свайп для удаления задачи
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
                    taskAdapter.tasks.getOrNull(position)?.let { task ->
                        deleteTask(task)
                    }
                }
            }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)

            createTaskButton.setOnClickListener {
                startActivity(Intent(this, CreateTaskActivity::class.java))
            }

            fetchCurrentFamilyName()

            // Добавляем слушатель кликов на задачи
            taskAdapter.setOnTaskClickListener(object : TaskAdapter.OnTaskClickListener {
                override fun onTaskClick(task: Task) {
                    // Переходим на экран редактирования задачи, передаем id задачи
                    val intent = Intent(this@TasksActivity, EditTaskActivity::class.java)
                    intent.putExtra("task_id", task.id)
                    startActivity(intent)
                }
            })
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
                                // Проверяем, что статус задачи не равен 1, и добавляем только такие задачи
                                if (it.status != 1) {
                                    tasks.add(it)
                                }
                            }
                        }
                        taskAdapter.updateTasks(tasks)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@TasksActivity, "Ошибка при загрузке задач: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        override fun onTaskDelete(position: Int) {
            // Метод будет вызываться из адаптера по свайпу
            taskAdapter.tasks.getOrNull(position)?.let { task ->
                deleteTask(task)
            }
        }

        private fun deleteTask(task: Task) {
            val databaseReference = FirebaseDatabase.getInstance().reference.child("tasks")

            databaseReference.orderByChild("nameOfFamily").equalTo(task.nameOfFamily).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (taskSnapshot in dataSnapshot.children) {
                        taskSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@TasksActivity, "Задача удалена", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@TasksActivity, "Ошибка при удалении задачи: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        return
                    }
                    Toast.makeText(this@TasksActivity, "Задача не найдена", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@TasksActivity, "Ошибка при удалении задачи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }