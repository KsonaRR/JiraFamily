package com.example.jirafamily


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.jirafamily.DTO.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.io.InputStream

class FillingDataMain : AppCompatActivity() {

    private lateinit var profilePhoto: ImageView
    private lateinit var inputName: EditText
    private lateinit var inputLastName: EditText
    private lateinit var openProfileButton: Button
    private lateinit var nameOfFamily: EditText
    private val PICK_IMAGE_REQUEST = 1
    private val MAX_IMAGE_SIZE = 300
    private lateinit var auth: FirebaseAuth
    private lateinit var imageUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filling_data_main)

        profilePhoto = findViewById(R.id.addPhotoButton)
        inputName = findViewById(R.id.inputName)
        inputLastName = findViewById(R.id.inputLastName)
        openProfileButton = findViewById(R.id.openProfileButton)
        nameOfFamily = findViewById(R.id.editTextInputNameOfFamily)

        auth = FirebaseAuth.getInstance()

        profilePhoto.setOnClickListener {
            changeProfileImage()
        }
        openProfileButton.setOnClickListener {
            // Получение значений из EditText
            val name = inputName.text.toString().trim()
            val lastName = inputLastName.text.toString().trim()
            val nameOfFamily = nameOfFamily.text.toString().trim()

            // Проверка, чтобы все поля были заполнены
            if (name.isEmpty() || lastName.isEmpty() || nameOfFamily.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка наличия ссылки на изображение аватара
            if (imageUrl.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, выберите изображение для аватара", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(
                name = name,
                lastName = lastName,
                nameOfFamily = nameOfFamily,
                avatar = imageUrl, // imageUrl - это ссылка на изображение профиля пользователя
                email = auth.currentUser?.email ?: "", // Получение email текущего пользователя
                id = auth.currentUser?.uid, // Получение ID текущего пользователя
//                avatarMockUpResource = null
            )

            // Ссылка на базу данных Realtime Database
            val database = FirebaseDatabase.getInstance().reference

            // Добавление нового пользователя в базу данных
            val userId = auth.currentUser?.uid
            if (userId != null) {
                database.child("users").child(userId).setValue(user)
                    .addOnSuccessListener {

                        val profileIntent = Intent(this, ProfileActivity::class.java)

                        profileIntent.putExtra("USER_DATA", user)

                        profileIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        startActivity(profileIntent)

                        finish()
                    }
                    .addOnFailureListener { exception ->
                        // Ошибка сохранения данных, вывод сообщения об ошибке
                        Toast.makeText(this, "Ошибка сохранения данных: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            val profileIntent = Intent(this, ProfileActivity::class.java)
            // Поместите данные пользователя в интент
            profileIntent.putExtra("USER_DATA", user)
            // Запуск активности профиля
            startActivity(profileIntent)
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
            val imageUri: Uri = data.data!!
            val resizedBitmap = resizeImage(imageUri)
            val circularBitmap = CircleCropTransformation().transform(resizedBitmap)
            profilePhoto.setImageBitmap(circularBitmap)

            imageUrl = imageUri.toString()
        }
    }

    private fun resizeImage(uri: Uri): Bitmap {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            var scale = 1
            while (options.outWidth / scale / 2 >= MAX_IMAGE_SIZE && options.outHeight / scale / 2 >= MAX_IMAGE_SIZE) {
                scale *= 2
            }

            val newOptions = BitmapFactory.Options()
            newOptions.inSampleSize = scale
            val newInputStream: InputStream? = contentResolver.openInputStream(uri)
            val resizedBitmap = BitmapFactory.decodeStream(newInputStream, null, newOptions)
            newInputStream?.close()

            return resizedBitmap ?: throw IllegalArgumentException("Ошибка загрузки изображения")
        } catch (e: IOException) {
            e.printStackTrace()
            throw IllegalArgumentException("Ошибка загрузки изображения")
        }
    }

    inner class CircleCropTransformation {
        fun transform(source: Bitmap): Bitmap {
            val output: Bitmap = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            val paint = Paint()
            paint.isAntiAlias = true
            val radius = Math.min(source.width, source.height) / 2f
            canvas.drawCircle(source.width / 2f, source.height / 2f, radius, paint)
            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(source, 0F, 0F, paint)
            source.recycle()
            return output
        }
    }





}
