package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.jirafamily.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var exitButton:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        exitButton = findViewById(R.id.exitButton)
        exitButton.setOnClickListener { signOut() }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        startActivity(Intent(this, FirstPageActivity::class.java))
        finish()
    }
}