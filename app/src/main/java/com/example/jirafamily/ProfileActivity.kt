package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var callingDialogButton: Button
    private lateinit var completedTasks: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        callingDialogButton = findViewById(R.id.exitButton)
        completedTasks = findViewById(R.id.completedTasks)

        completedTasks.setOnClickListener {
            showNumbersTasks()
        }
        callingDialogButton.setOnClickListener {
            showLogOutDialog()
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