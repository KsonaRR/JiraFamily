package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.adapters.PrioritySpinnerAdapter
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CompTaskActivity : AppCompatActivity() {

    private lateinit var nameTaskEditText: EditText
    private lateinit var descriptionTaskEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var createTaskButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var taskId: String
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton:ImageView
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference

    private val priorityImages = intArrayOf(
        R.drawable.lowest,
        R.drawable.low,
        R.drawable.medium,
        R.drawable.high,
        R.drawable.highest
    )
    private val priorityLabels = arrayOf(
        "Очень низкий",
        "Низкий",
        "Средний",
        "Высокий",
        "Очень высокий"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comp_task)

        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)
        notificationButton = findViewById(R.id.imageView5)
        profileButton = findViewById(R.id.imageView4)
        nameTaskEditText = findViewById(R.id.nameTask)
        descriptionTaskEditText = findViewById(R.id.descriptionTask)
        prioritySpinner = findViewById(R.id.prioritySpinner)
        createTaskButton = findViewById(R.id.createTaskButton)
        selectedImageView = findViewById(R.id.attachedFileName)  // Это ImageView для прикрепленного файла

        taskId = intent.getStringExtra("task_id") ?: ""
        database = FirebaseDatabase.getInstance().reference.child("tasks")
        storageReference = FirebaseStorage.getInstance().reference.child("task_attachments")

        setupPrioritySpinner()
        loadTaskDetails()

        createTaskButton.setOnClickListener {
            completeTask()
        }
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
    }

    private fun setupPrioritySpinner() {
        val priorityAdapter = PrioritySpinnerAdapter(this, priorityLabels, priorityImages)
        prioritySpinner.adapter = priorityAdapter

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val priorityImage = priorityImages[position]
                selectedImageView.setImageResource(priorityImage)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Ничего не делаем
            }
        }
    }

    private fun loadTaskDetails() {
        database.child(taskId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val task = dataSnapshot.getValue(Task::class.java)
                task?.let {
                    nameTaskEditText.setText(it.title)
                    descriptionTaskEditText.setText(it.description)
                    val taskPriority = it.priority ?: 0
                    prioritySpinner.setSelection(taskPriority)

                    // Загрузка изображения из Firebase Storage и установка его в ImageView
                    val imageUrl = it.attachments
                    if (!imageUrl.isNullOrEmpty()) {
                        loadImageFromUrl(imageUrl)
                    } else {
                        Log.d("EditTaskActivity", "No image URL found in task")
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CompTaskActivity, "Ошибка при загрузке задачи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadImageFromUrl(imageUrl: String) {
        Log.d("EditTaskActivity", "Loading image from URL: $imageUrl")

        try {
            Glide.with(this)
                .load(imageUrl)
                .override(500, 500) // Устанавливаем размер 500x500
                .centerCrop() // Центрируем изображение и обрезаем лишнее
                .into(selectedImageView)

            selectedImageView.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка при загрузке файла: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun completeTask() {
        val taskUpdate = mapOf(
            "status" to 1 // Обновляем статус задачи на 1
        )

        database.child(taskId).updateChildren(taskUpdate).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Задача завершена", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Ошибка при завершении задачи", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
