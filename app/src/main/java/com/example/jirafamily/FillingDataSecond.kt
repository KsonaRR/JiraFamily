package com.example.jirafamily

import android.app.Activity
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.User
import com.example.jirafamily.adapters.CircleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class FillingDataSecond : AppCompatActivity() {

    private lateinit var profilePhoto: ImageView
    private lateinit var inputName: EditText
    private lateinit var inputLastName: EditText
    private lateinit var inputToken: EditText
    private lateinit var openProfileButton: Button
    private lateinit var user: User
    private lateinit var auth: FirebaseAuth
    private var imageUrl: String? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filling_data_second)

        profilePhoto = findViewById(R.id.addPhotoButton)
        inputName = findViewById(R.id.userName3)
        inputLastName = findViewById(R.id.userLastName3)
        openProfileButton = findViewById(R.id.saveButton)
        inputToken = findViewById(R.id.token)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance() // Инициализация FirebaseStorage
        storageReference = storage.reference // Получение ссылки на хранилище

        profilePhoto.setOnClickListener {
            changeProfileImage()
        }

        openProfileButton.setOnClickListener {
            val name = inputName.text.toString().trim()
            val lastName = inputLastName.text.toString().trim()
            val inviteToken = inputToken.text.toString().trim()

            if (name.isEmpty() || lastName.isEmpty() || inviteToken.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Пожалуйста, выберите изображение для аватара", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid ?: return@setOnClickListener
            val database = FirebaseDatabase.getInstance().reference

            database.child("admins").orderByChild("inviteToken").equalTo(inviteToken)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (adminSnapshot in snapshot.children) {
                                val adminId = adminSnapshot.key ?: ""
                                uploadImageToStorage(imageUri!!) { imageUrl ->
                                    user = User(
                                        name = name,
                                        lastName = lastName,
                                        nameOfFamily = adminSnapshot.child("nameOfFamily").value.toString(),
                                        avatar = imageUrl ?: "",
                                        email = auth.currentUser?.email ?: "",
                                        id = userId,
                                        inviteToken = inviteToken,
                                        adminId = adminId
                                    )

                                    database.child("users").child(userId).setValue(user)
                                        .addOnSuccessListener {
                                            val profileIntent = Intent(this@FillingDataSecond, ProfileActivity::class.java)
                                            profileIntent.putExtra("USER_DATA", user)
                                            profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(profileIntent)
                                            finish()
                                        }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(this@FillingDataSecond, "Ошибка сохранения данных: ${exception.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            }
                        } else {
                            Toast.makeText(this@FillingDataSecond, "Неверный токен приглашения", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@FillingDataSecond, "Ошибка: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
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
            imageUri = data.data // Обновляем значение переменной класса imageUri

            // Выводим URI изображения в тост
            Toast.makeText(this, "Image URI: $imageUri", Toast.LENGTH_SHORT).show()

            // Загрузка изображения с помощью Glide
            Glide.with(this)
                .load(imageUri)
                .circleCrop()
                .into(profilePhoto)
        }
    }

    private fun uploadImageToStorage(imageUri: Uri, onSuccess: (imageUrl: String) -> Unit) {
        val storageRef: StorageReference = storage.reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri)
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
