package com.example.jirafamily

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.jirafamily.DTO.Admin
import com.example.jirafamily.DTO.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileAdminActivity : AppCompatActivity() {

    private lateinit var callingDialogButton: Button
    private lateinit var completedTasks: Button
    private lateinit var name: TextView
    private lateinit var lastName: TextView
    private lateinit var nameOfFamilyLogo: TextView
    private lateinit var avatarOfUser: ImageView
    private lateinit var showUsersButton: Button
    private lateinit var editUsers: Button
    private lateinit var showTokenButton: Button
    private lateinit var notificationButton: ImageView
    private lateinit var messageButton: ImageView
    private lateinit var tasksButton: ImageView
    private lateinit var profileButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_admin)

        callingDialogButton = findViewById(R.id.exitButton)
        completedTasks = findViewById(R.id.completedTasks)
        name = findViewById(R.id.name)
        lastName = findViewById(R.id.lastName)
        nameOfFamilyLogo = findViewById(R.id.TextLogo)
        avatarOfUser = findViewById(R.id.profilePhotoImageView)
        showTokenButton = findViewById(R.id.showTokenButton)
        notificationButton = findViewById(R.id.imageView5)
        editUsers = findViewById(R.id.TokenButton)
        messageButton = findViewById(R.id.imageView6)
        tasksButton = findViewById(R.id.imageView7)
        showUsersButton = findViewById(R.id.showUsersButton)
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

        showTokenButton.setOnClickListener {
            showTokenDialog()
        }

        editUsers.setOnClickListener {
            startActivity(Intent(this, EditUsersActivity::class.java))
        }

        showUsersButton.setOnClickListener {
            startActivity(Intent(this, ListUsersActivity::class.java))
        }

        notificationButton.setOnClickListener {
            startActivity(Intent(this, NotificationAcitivity::class.java))
        }

        messageButton.setOnClickListener {
            startActivity(Intent(this, ListChatsActivity::class.java))
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

        name.setOnClickListener {
            showEditProfileDialog()
        }

        lastName.setOnClickListener {
            showEditProfileDialog()
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

                        // Получаем ссылку на изображение из поля avatar текущего админа
                        val imageUrl = it.avatar

                        // Используем Glide для загрузки изображения
                        Glide.with(this@ProfileAdminActivity)
                            .load(imageUrl)
                            .circleCrop()
                            .into(avatarOfUser)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileAdminActivity", "Error loading admin data: ${error.message}")
            }
        })
    }

    private fun showEditProfileDialog() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val databaseReference = FirebaseDatabase.getInstance().getReference("admins").child(adminId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val admin = snapshot.getValue(Admin::class.java)
                    admin?.let {
                        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
                        val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
                        val editTextLastName = dialogView.findViewById<EditText>(R.id.editTextLastName)
                        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)
                        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

                        editTextName.setText(it.name)
                        editTextLastName.setText(it.lastName)

                        val dialog = AlertDialog.Builder(this@ProfileAdminActivity)
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
                                        Toast.makeText(this@ProfileAdminActivity, "Профиль обновлен", Toast.LENGTH_SHORT).show()
                                        name.text = newName
                                        lastName.text = newLastName
                                    } else {
                                        Toast.makeText(this@ProfileAdminActivity, "Ошибка обновления профиля", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                dialog.dismiss()
                            } else {
                                Toast.makeText(this@ProfileAdminActivity, "Поля не должны быть пустыми", Toast.LENGTH_SHORT).show()
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
                Log.e("ProfileAdminActivity", "Error loading admin data: ${error.message}")
            }
        })
    }

    private fun showTokenDialog() {
        val adminId = FirebaseAuth.getInstance().currentUser?.uid
        val databaseReference = adminId?.let {
            FirebaseDatabase.getInstance().getReference("admins").child(it)
        }

        databaseReference?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val inviteToken = snapshot.child("inviteToken").value as? String ?: "No Token"

                    val dialogView = layoutInflater.inflate(R.layout.dialog_show_token, null)
                    val editTextToken = dialogView.findViewById<TextView>(R.id.editTextToken)
                    val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)
                    val btnCopyToken = dialogView.findViewById<Button>(R.id.btnCopyToken)

                    editTextToken.text = inviteToken

                    val dialog = AlertDialog.Builder(this@ProfileAdminActivity)
                        .setView(dialogView)
                        .create()

                    btnContinue.setOnClickListener {
                        dialog.dismiss()
                    }

                    btnCopyToken.setOnClickListener {
                        copyToClipboard(inviteToken)
                        Toast.makeText(this@ProfileAdminActivity, "Токен скопирован", Toast.LENGTH_SHORT).show()
                    }

                    dialog.show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileAdminActivity", "Error loading token: ${error.message}")
            }
        })
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Invite Token", text)
        clipboard.setPrimaryClip(clip)
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

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
    }
}
