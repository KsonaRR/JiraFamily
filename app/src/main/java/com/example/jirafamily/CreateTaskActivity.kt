package com.example.jirafamily

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.adapters.PrioritySpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var prioritySpinner: Spinner
    private lateinit var selectedImage: ImageView
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var attachFileButton: Button
    private lateinit var attachedFileName: ImageView
    private lateinit var createTask: Button
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private var selectedPriorityPosition: Int = 0
    private var currentFamilyName: String? = null
    private var attachedFileUri: Uri? = null
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        prioritySpinner = findViewById(R.id.prioritySpinner)
        selectedImage = findViewById(R.id.selectedImage)
        title = findViewById(R.id.nameTask)
        description = findViewById(R.id.descriptionTask)
        attachFileButton = findViewById(R.id.attachFileButton)
        attachedFileName = findViewById(R.id.attachedFileName)
        createTask = findViewById(R.id.createTaskButton)
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

        attachFileButton.setOnClickListener {
            openGallery()
        }

        val priorities = arrayOf("Очень низкий", "Низкий", "Средний", "Высокий", "Очень высокий")
        val images = intArrayOf(R.drawable.lowest, R.drawable.low, R.drawable.medium, R.drawable.high, R.drawable.highest)
        val adapter = PrioritySpinnerAdapter(this, priorities, images)
        prioritySpinner.adapter = adapter

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriorityPosition = position
                selectedImage.setImageResource(images[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        createTask.setOnClickListener {
            if (currentFamilyName != null) {
                saveTaskToDatabase()
            } else {
                Toast.makeText(this, "Не удалось получить название семьи. Попробуйте еще раз.", Toast.LENGTH_SHORT).show()
            }
        }

        fetchCurrentFamilyName()

        storageReference = FirebaseStorage.getInstance().reference
    }

    private fun navigateToTasksActivity() {
        val intent = Intent(this, TasksActivity::class.java)
        startActivity(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // Устанавливаем тип MIME для изображений
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            attachedFileUri = data.data
            try {
                // Загрузка изображения с помощью Glide
                Glide.with(this)
                    .load(attachedFileUri)
                    .override(500, 500) // Устанавливаем размер 300x300
                    .centerCrop() // Центрируем изображение и обрезаем лишнее
                    .into(selectedImage)

                selectedImage.visibility = View.VISIBLE
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Ошибка при загрузке файла: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun saveTaskToDatabase() {
        val titleText = title.text.toString().trim()
        val descriptionText = description.text.toString().trim()
        val priorityPosition = prioritySpinner.selectedItemPosition

        if (titleText.isEmpty() || descriptionText.isEmpty() || currentFamilyName == null) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        // Генерируем уникальный идентификатор для задачи
        val taskId = databaseReference.child("tasks").push().key

        databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adminAvatarUrl = dataSnapshot.child("avatar").getValue(String::class.java)

                val task: Task
                if (attachedFileUri != null) {
                    val fileReference = storageReference.child("uploads/${System.currentTimeMillis()}_${attachedFileUri?.lastPathSegment}")
                    fileReference.putFile(attachedFileUri!!)
                        .addOnSuccessListener { taskSnapshot ->
                            fileReference.downloadUrl.addOnSuccessListener { uri ->
                                val attachedFileUrl = uri.toString()
                                val task = taskId?.let { Task(it, titleText, descriptionText, adminAvatarUrl ?: "", 0, priorityPosition, currentFamilyName!!, attachedFileUrl) }
                                val taskReference = databaseReference.child("tasks").child(taskId!!)
                                taskReference.setValue(task)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@CreateTaskActivity, "Задача сохранена", Toast.LENGTH_SHORT).show()
                                        navigateToTasksActivity()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(this@CreateTaskActivity, "Ошибка при сохранении задачи: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@CreateTaskActivity, "Ошибка при загрузке файла: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    task = taskId?.let { Task(it, titleText, descriptionText, adminAvatarUrl ?: "", 0, priorityPosition, currentFamilyName!!, "") }!!
                    val taskReference = databaseReference.child("tasks").child(taskId!!)
                    taskReference.setValue(task)
                        .addOnSuccessListener {
                            Toast.makeText(this@CreateTaskActivity, "Задача сохранена", Toast.LENGTH_SHORT).show()
                            navigateToTasksActivity()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this@CreateTaskActivity, "Ошибка при сохранении задачи: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CreateTaskActivity, "Ошибка при получении данных администратора: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchCurrentFamilyName() {
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                currentFamilyName = dataSnapshot.child("nameOfFamily").getValue(String::class.java)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CreateTaskActivity, "Ошибка при получении названия семьи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val PICK_FILE_REQUEST = 101
    }
}
