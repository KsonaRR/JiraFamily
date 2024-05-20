package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loader)

        if (isUserLoggedIn()) {
            // Если пользователь уже вошел в аккаунт, переходим на ProfileActivity
            navigateToProfileActivity()
        } else {
            // Если пользователь не вошел в аккаунт, переходим на FirstPageActivity
            navigateToFirstPageActivity()
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser != null
    }

    private fun navigateToProfileActivity() {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToFirstPageActivity() {
        Handler().postDelayed({
            val intent = Intent(this, FirstPageActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000) // Задержка перед переходом на экран входа
    }
}