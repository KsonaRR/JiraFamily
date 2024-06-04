package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.adapters.PrioritySpinnerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var prioritySpinner: Spinner
    private lateinit var selectedImage: ImageView
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var createTask: Button
    private lateinit var database: DatabaseReference
    private var selectedPriorityPosition: Int = 0 // Переменная для хранения позиции выбранного приоритета
    private var currentFamilyName: String? = null // Переменная для хранения названия семьи

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_task)

        prioritySpinner = findViewById(R.id.prioritySpinner)
        selectedImage = findViewById(R.id.selectedImage)
        title = findViewById(R.id.nameTask)
        description = findViewById(R.id.descriptionTask)
        createTask = findViewById(R.id.createTaskButton)

        val priorities = arrayOf("Очень низкий", "Низкий", "Средний", "Высокий", "Очень высокий")
        val images = intArrayOf(R.drawable.lowest, R.drawable.low, R.drawable.medium, R.drawable.high, R.drawable.highest)
        val adapter = PrioritySpinnerAdapter(this, priorities, images)
        prioritySpinner.adapter = adapter

        prioritySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Сохраняем позицию выбранного приоритета
                selectedPriorityPosition = position
                // При выборе элемента из спиннера обновляем изображение согласно выбранному приоритету
                selectedImage.setImageResource(images[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Действия, если ничего не выбрано
            }
        }

        createTask.setOnClickListener {
            if (currentFamilyName != null) {
                saveTaskToDatabase()
            } else {
                Toast.makeText(this, "Не удалось получить название семьи. Попробуйте еще раз.", Toast.LENGTH_SHORT).show()
            }
        }

        // Получение названия семьи текущего администратора
        fetchCurrentFamilyName()
    }

    private fun navigateToTasksActivity() {
        val intent = Intent(this, TasksActivity::class.java)
        startActivity(intent)
    }

    private fun saveTaskToDatabase() {
        val titleText = title.text.toString().trim()
        val descriptionText = description.text.toString().trim()
        val priorityPosition = prioritySpinner.selectedItemPosition

        // Проверка, чтобы все поля были заполнены
        if (titleText.isEmpty() || descriptionText.isEmpty() || currentFamilyName.isNullOrEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        // Получение URL фотографии администратора
        val currentUserID = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().reference

        databaseReference.child("admins").child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val adminAvatarUrl = dataSnapshot.child("avatar").getValue(String::class.java)

                // Создание объекта задачи с URL фотографии администратора
                val task = Task(titleText, descriptionText, adminAvatarUrl ?: "", "0", priorityPosition, currentFamilyName!!)

                // Добавление задачи в базу данных
                val taskReference = databaseReference.child("tasks").push()
                taskReference.setValue(task)
                    .addOnSuccessListener {
                        // Успешно сохранено
                        Toast.makeText(this@CreateTaskActivity, "Задача сохранена", Toast.LENGTH_SHORT).show()
                        navigateToTasksActivity()
                    }
                    .addOnFailureListener { exception ->
                        // Ошибка сохранения
                        Toast.makeText(this@CreateTaskActivity, "Ошибка при сохранении задачи: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок
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
                // Обработка ошибок
                Toast.makeText(this@CreateTaskActivity, "Ошибка при получении названия семьи: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
