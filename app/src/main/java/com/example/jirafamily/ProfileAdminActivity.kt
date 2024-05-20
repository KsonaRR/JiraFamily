package com.example.jirafamily

import TasksActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.Admin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var callingDialogButton: Button
    private lateinit var completedTasks: Button
    private lateinit var name: TextView
    private lateinit var lastName: TextView
    private lateinit var nameOfFamilyLogo: TextView
    private lateinit var avatarOfUser: ImageView
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var showUsersButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin)

        callingDialogButton = findViewById(R.id.exitButton)
        completedTasks = findViewById(R.id.completedTasks)
        name = findViewById(R.id.name)
        lastName = findViewById(R.id.lastName)
        nameOfFamilyLogo = findViewById(R.id.TextLogo)
        avatarOfUser = findViewById(R.id.profilePhotoImageView)
        notificationButton = findViewById(R.id.imageView5)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)
        showUsersButton = findViewById(R.id.showUsersButton)

        showUsersButton.setOnClickListener {
            startActivity(Intent(this, ListUsersActivity::class.java))
        }

        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }

        messageButton.setOnClickListener {
            startActivity(Intent(this, ListUsersActivity::class.java))
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

        loadUserDataFromFirebase()
    }

    private fun loadUserDataFromFirebase() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = adminId?.let {
            FirebaseDatabase.getInstance().getReference("admins").child(it)
        }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val admin = snapshot.getValue(Admin::class.java)
                    admin?.let {
                        // Обновление полей макета данными админа
                        name.text = it.name
                        lastName.text = it.lastName
                        nameOfFamilyLogo.text = it.nameOfFamily
                        Glide.with(this@ProfileAdminActivity)
                            .load(it.avatar)
                            .into(avatarOfUser)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileAdminActivity", "Error loading admin data: ${error.message}")
            }
        })
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
    }

    private fun showNumbersTasks() {
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
            dialog.dismiss()
        }
    }
}
