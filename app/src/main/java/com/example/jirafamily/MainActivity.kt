package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        Handler().postDelayed({
            checkUserStatus()
        }, 3000)
    }

    private fun checkUserStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Пользователь уже авторизован, проверяем его статус (пользователь или админ)
            val userId = currentUser.uid
            checkIfUserIsAdmin(userId)
        } else {
            // Пользователь не авторизован, перенаправление на FirstPageActivity
            startActivity(Intent(this, FirstPageActivity::class.java))
            finish()
        }
    }

    private fun checkIfUserIsAdmin(userId: String) {
        database.child("admins").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(adminSnapshot: DataSnapshot) {
                if (adminSnapshot.exists()) {
                    // Перенаправление в ProfileAdminActivity
                    startActivity(Intent(this@MainActivity, ProfileAdminActivity::class.java))
                    finish()
                } else {
                    // Проверяем, является ли пользователь обычным пользователем
                    checkIfUserIsRegular(userId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Ошибка при доступе к базе данных: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, FirstPageActivity::class.java))
                finish()
            }
        })
    }

    private fun checkIfUserIsRegular(userId: String) {
        database.child("users").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(userSnapshot: DataSnapshot) {
                if (userSnapshot.exists()) {
                    // Перенаправление в ProfileActivity
                    startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, FirstPageActivity::class.java))
                }
                finish()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@MainActivity, "Ошибка при доступе к базе данных: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@MainActivity, FirstPageActivity::class.java))
                finish()
            }
        })
    }
}
