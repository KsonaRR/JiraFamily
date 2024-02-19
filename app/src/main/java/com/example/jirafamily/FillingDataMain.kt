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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.io.InputStream


class FillingDataMain : AppCompatActivity() {

    private lateinit var profilePhoto:ImageView
    private lateinit var inputName:EditText
    private lateinit var inputLastName:EditText
    private lateinit var openProfileButton: Button
    private val PICK_IMAGE_REQUEST = 1
    private val MAX_IMAGE_SIZE = 300
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filling_data_main)

        profilePhoto = findViewById(R.id.addPhotoButton)
        inputName = findViewById(R.id.inputName)
        inputLastName = findViewById(R.id.inputLastName)
        openProfileButton = findViewById(R.id.openProfileButton)

        profilePhoto.setOnClickListener {
            changeProfileImage()
        }
        openProfileButton.setOnClickListener {
            saveUserProfile()
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
        }
        throw IllegalArgumentException("Ошибка загрузки изображения")
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
    private fun saveUserProfile() {
        val name = inputName.text.toString()
        val lastName = inputLastName.text.toString()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userReference = database.getReference("users").child(userId)
            val userData = mapOf(
                "name" to name,
                "lastName" to lastName,
                "avatarUrl" to ""
            )
            userReference.setValue(userData)
                .addOnSuccessListener {
                    startActivity(Intent(this, ProfileActivity::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Ошибка сохранения данных", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
