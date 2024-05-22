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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var prioritySpinner: Spinner
    private lateinit var selectedImage: ImageView
    private lateinit var title:EditText
    private lateinit var description:EditText
    private lateinit var createTask: Button
    private lateinit var database: DatabaseReference
    private var selectedPriorityPosition: Int = 0 // Переменная для хранения позиции выбранного приоритета

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
            saveTaskToDatabase()

        }
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
        if (titleText.isEmpty() || descriptionText.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        // Создание объекта задачи
        val task = Task(titleText, descriptionText, "", "0", priorityPosition)

        // Получение ссылки на базу данных
        val database = FirebaseDatabase.getInstance().reference

        // Добавление задачи в базу данных
        val taskReference = database.child("tasks").push()
        taskReference.setValue(task)
            .addOnSuccessListener {
                // Успешно сохранено
                Toast.makeText(this, "Задача сохранена", Toast.LENGTH_SHORT).show()
                navigateToTasksActivity()
            }
            .addOnFailureListener { exception ->
                // Ошибка сохранения
                Toast.makeText(this, "Ошибка при сохранении задачи: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


}
