package com.example.jirafamily

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import com.squareup.picasso.Picasso

class EditTaskActivity : AppCompatActivity() {

    private lateinit var nameTaskEditText: EditText
    private lateinit var descriptionTaskEditText: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var createTaskButton: Button
    private lateinit var editAttachmentsButton: Button
    private lateinit var selectedImageView: ImageView
    private lateinit var taskId: String
    private lateinit var database: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton:ImageView

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

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
        setContentView(R.layout.activity_edit_task)

        nameTaskEditText = findViewById(R.id.nameTask)
        descriptionTaskEditText = findViewById(R.id.descriptionTask)
        prioritySpinner = findViewById(R.id.prioritySpinner)
        createTaskButton = findViewById(R.id.createTaskButton)
        editAttachmentsButton = findViewById(R.id.attachFileButton)
        selectedImageView = findViewById(R.id.attachedFileName)
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

        taskId = intent.getStringExtra("task_id") ?: ""
        database = FirebaseDatabase.getInstance().reference.child("tasks")
        storageReference = FirebaseStorage.getInstance().reference.child("task_attachments")

        setupPrioritySpinner()
        loadTaskDetails()

        createTaskButton.setOnClickListener {
            updateTask()
        }

        editAttachmentsButton.setOnClickListener {
            chooseImage()
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
                Toast.makeText(this@EditTaskActivity, "Ошибка при загрузке задачи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadImageFromUrl(imageUrl: String) {
        Log.d("EditTaskActivity", "Loading image from URL: $imageUrl")

        val requestOptions = RequestOptions()
            .fitCenter() // Центрировать изображение и сохранить пропорции
            .override(500, 500) // Установить размер 500x500 пикселей

        val imageView = findViewById<ImageView>(R.id.attachedFileName)
        Glide.with(this)
            .load(imageUrl) // imageUrl - ссылка на изображение в Firebase Storage
            .apply(requestOptions) // Применить параметры загрузки
            .into(imageView)
    }


    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            selectedImageView.setImageURI(selectedImageUri)
            Log.d("EditTaskActivity", "Image selected: $selectedImageUri")
        }
    }

    private fun updateTask() {
        val taskName = nameTaskEditText.text.toString().trim()
        val taskDescription = descriptionTaskEditText.text.toString().trim()
        val taskPriority = prioritySpinner.selectedItemPosition

        if (taskName.isEmpty() || taskDescription.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageUri != null) {
            uploadImageAndSaveTask(selectedImageUri!!)
        } else {
            saveTaskData(null)
        }
    }

    private fun uploadImageAndSaveTask(imageUri: Uri) {
        val ref = storageReference.child(taskId + ".jpg")
        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("EditTaskActivity", "Image uploaded successfully: $uri")
                    saveTaskData(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this@EditTaskActivity, "Ошибка загрузки изображения: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("EditTaskActivity", "Error uploading image", exception)
            }
    }

    private fun saveTaskData(imageUrl: String?) {
        val taskName = nameTaskEditText.text.toString().trim()
        val taskDescription = descriptionTaskEditText.text.toString().trim()
        val taskPriority = prioritySpinner.selectedItemPosition

        val taskRef = database.child(taskId)
        taskRef.child("title").setValue(taskName)
        taskRef.child("description").setValue(taskDescription)
        taskRef.child("priority").setValue(taskPriority)
        imageUrl?.let {
            taskRef.child("attachments").setValue(it)
        }

        Toast.makeText(this, "Задача успешно обновлена", Toast.LENGTH_SHORT).show()
        finish()
    }
}
