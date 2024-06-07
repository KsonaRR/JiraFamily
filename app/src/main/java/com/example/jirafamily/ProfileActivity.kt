package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.jirafamily.DTO.Task
import com.example.jirafamily.DTO.User
import com.example.jirafamily.adapters.CircleTransformation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var callingDialogButton: Button
    private lateinit var completedTasks: Button
    private lateinit var name: TextView
    private lateinit var lastName: TextView
    private lateinit var nameOfFamilyLogo: TextView
    private lateinit var avatarOfUser: ImageView
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var listOfUsers: Button
    private lateinit var databaseReference: DatabaseReference

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
        listOfUsers = findViewById(R.id.showMessageButton)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)


        listOfUsers.setOnClickListener {
            startActivity(Intent(this, ListForUsersActivity::class.java))
        }

        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }

        messageButton.setOnClickListener {
            startActivity(Intent(this, ListForChatsActivity::class.java))
        }

        tasksButton.setOnClickListener {
            startActivity(Intent(this, ListTaskForUsersActivity::class.java))
        }

        completedTasks.setOnClickListener {
            showNumbersTasks()
        }


        callingDialogButton.setOnClickListener {
            showLogOutDialog()
        }

        name.setOnClickListener {
            showEditProfileDialog()
        }

        lastName.setOnClickListener {
            showEditProfileDialog()
        }

        loadUserDataFromFirebase()
    }

    private fun loadUserDataFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = userId?.let {
            FirebaseDatabase.getInstance().getReference("users").child(it)
        }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        // Обновление полей макета данными пользователя
                        name.text = it.name
                        lastName.text = it.lastName
                        nameOfFamilyLogo.text = it.nameOfFamily

                        // Получаем ссылку на изображение из поля avatar текущего пользователя
                        val imageUrl = it.avatar

                        // Загружаем изображение с помощью Picasso
                        Glide.with(this@ProfileActivity)
                            .load(imageUrl)
                            .override(450, 450)
                            .centerCrop()
                            .transform(CircleCrop())
                            .into(avatarOfUser)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Error loading user data: ${error.message}")
            }
        })
    }

    private fun showNumbersTasks() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = adminId?.let {
            FirebaseDatabase.getInstance().getReference("tasks")
        }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var tasksCount = 0
                    for (taskSnapshot in snapshot.children) {
                        val task = taskSnapshot.getValue(Task::class.java)
                        if (task?.nameOfFamily == nameOfFamilyLogo.text.toString() && task.status == 1) {
                            tasksCount++
                        }
                    }
                    showTasksCountDialog(tasksCount)
                } else {
                    // Данные о задачах не найдены
                    showTasksCountDialog(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileAdminActivity", "Error loading tasks data: ${error.message}")
                showTasksCountDialog(0)
            }
        })
    }

    private fun showTasksCountDialog(tasksCount: Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_number_of_tasks, null)
        val textViewTasksCount = dialogView.findViewById<TextView>(R.id.textView6)
        textViewTasksCount.text = tasksCount.toString()
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()
    }


    private fun showEditProfileDialog() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
                        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
                        val editTextLastName = dialogView.findViewById<EditText>(R.id.editTextLastName)
                        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
                        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

                        editTextName.setText(it.name)
                        editTextLastName.setText(it.lastName)

                        val dialog = AlertDialog.Builder(this@ProfileActivity)
                            .setView(dialogView)
                            .create()

                        btnSave.setOnClickListener {
                            val newName = editTextName.text.toString()
                            val newLastName = editTextLastName.text.toString()

                            if (newName.isNotEmpty() && newLastName.isNotEmpty()) {
                                val updates = mapOf(
                                    "name" to newName,
                                    "lastName" to newLastName
                                )

                                databaseReference.updateChildren(updates).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@ProfileActivity, "Профиль обновлен", Toast.LENGTH_SHORT).show()
                                        name.text = newName
                                        lastName.text = newLastName
                                    } else {
                                        Toast.makeText(this@ProfileActivity, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this@ProfileActivity, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
                            }
                        }

                        btnCancel.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileActivity", "Error loading user data: ${error.message}")
            }
        })
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
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
