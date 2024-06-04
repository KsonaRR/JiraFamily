package com.example.jirafamily

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.Admin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

import java.util.*


class FillingDataMain : AppCompatActivity() {

    private lateinit var profilePhoto: ImageView
    private lateinit var inputName: EditText
    private lateinit var inputLastName: EditText
    private lateinit var openProfileButton: Button
    private lateinit var nameOfFamily: EditText
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUrl: String
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filling_data_main)

        profilePhoto = findViewById(R.id.addPhotoButton)
        inputName = findViewById(R.id.inputName)
        inputLastName = findViewById(R.id.inputLastName)
        openProfileButton = findViewById(R.id.openProfileButton)
        nameOfFamily = findViewById(R.id.editTextInputNameOfFamily)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        profilePhoto.setOnClickListener {
            changeProfileImage()
        }

        openProfileButton.setOnClickListener {
            // Получение значений из EditText
            val name = inputName.text.toString().trim()
            val lastName = inputLastName.text.toString().trim()
            val familyName = nameOfFamily.text.toString().trim()

            // Проверка, чтобы все поля были заполнены
            if (name.isEmpty() || lastName.isEmpty() || familyName.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка наличия ссылки на изображение аватара
            if (imageUri == null) {
                Toast.makeText(
                    this,
                    "Пожалуйста, выберите изображение для аватара",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Генерация токена приглашения
            val inviteToken = UUID.randomUUID().toString()

            // Загрузка изображения в Firebase Storage и сохранение данных администратора
            uploadImageToStorage { imageUrl ->
                val admin = Admin(
                    name = name,
                    lastName = lastName,
                    nameOfFamily = familyName,
                    avatar = imageUrl, // Сохранение ссылки на изображение в поле avatar
                    email = auth.currentUser?.email ?: "", // Получение email текущего пользователя
                    id = auth.currentUser?.uid, // Получение ID текущего пользователя
                    inviteToken = inviteToken // Добавление токена в Admin объект
                )

                // Ссылка на базу данных Realtime Database
                val database = FirebaseDatabase.getInstance().reference

                // Добавление нового администратора в базу данных
                val adminId = auth.currentUser?.uid
                if (adminId != null) {
                    database.child("admins").child(adminId).setValue(admin)
                        .addOnSuccessListener {
                            val profileIntent = Intent(this, ProfileAdminActivity::class.java)
                            profileIntent.putExtra("ADMIN_DATA", admin)
                            profileIntent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(profileIntent)
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            // Ошибка сохранения данных, вывод сообщения об ошибке
                            Toast.makeText(
                                this,
                                "Ошибка сохранения данных: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }

    private fun changeProfileImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data

            // Загрузка изображения с помощью Glide
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(profilePhoto)
        }
    }

    private fun uploadImageToStorage(onSuccess: (imageUrl: String) -> Unit) {
        if (imageUri != null) {
            val storageRef: StorageReference = storage.reference.child("images/${UUID.randomUUID()}")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Ошибка загрузки изображения: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}

