package com.example.jirafamily

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.FillingDataMain.CircleCropTransformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.IOException
import java.io.InputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var callingDialogButton: Button
    private lateinit var completedTasks: Button
    private lateinit var name:TextView
    private lateinit var lastName:TextView
    private lateinit var nameOfFamilyLogo:TextView
    private lateinit var avatarOfUser:ImageView
    private lateinit var notificationButton:ImageView
    private lateinit var messageButton:ImageView
    private lateinit var tasksButton:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        callingDialogButton = findViewById(R.id.exitButton)
        completedTasks = findViewById(R.id.completedTasks)
        name = findViewById(R.id.name)
        lastName = findViewById(R.id.lastName)
        nameOfFamilyLogo = findViewById(R.id.TextLogo)
        avatarOfUser = findViewById(R.id.profilePhotoImageView)
        notificationButton = findViewById(R.id.imageView5)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)

        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }

        messageButton.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        tasksButton.setOnClickListener {
            startActivity(Intent(this, TasksActivity::class.java))
        }

        completedTasks.setOnClickListener {
            showNumbersTasks()
        }
        callingDialogButton.setOnClickListener {
            showLogOutDialog()
        }
        loadUserDataFromFirestore()
    }

    private fun loadUserDataFromFirestore() {
        val userId = Firebase.auth.currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userRef = db.collection("users").document(userId)

            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = document.data
                        if (userData != null) {
                            // Обновление представлений данными из Firestore
                            name.text = userData["name"].toString()
                            lastName.text = userData["lastName"].toString()
                            nameOfFamilyLogo.text = userData["familyName"].toString()


                            // Загрузка и отображение изображения пользователя
                            val imageUrl = userData["avatarUrl"].toString()
                            Glide.with(this@ProfileActivity)
                                .load(imageUrl)
                                .into(avatarOfUser)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }



    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
    }

    private fun showNumbersTasks(){
            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialogView = inflater.inflate(R.layout.dialog_number_of_tasks, null)
            builder.setView(dialogView)

            val tasksForTheWeek = dialogView.findViewById<CheckBox>(R.id.tasksForTheWeek)
            val tasksForTheMonth = dialogView.findViewById<CheckBox>(R.id.tasksForTheMonth)
            val tasksForTheYear = dialogView.findViewById<CheckBox>(R.id.tasksForTheYear)

            val dialog = builder.create()
            dialog.show()
    }
    private fun showLogOutDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_log_out, null)
        builder.setView(dialogView)

        val returnButton = dialogView.findViewById<Button>(R.id.returnButton)
        val approvalButton = dialogView.findViewById<Button>(R.id.approvalButton)

        val dialog = builder.create()
        dialog.show()

        approvalButton.setOnClickListener {
            signOut()
            dialog.dismiss()
        }
        returnButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            dialog.dismiss()
        }
    }
}