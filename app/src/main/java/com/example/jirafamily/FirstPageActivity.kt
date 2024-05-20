package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirstPageActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_page)

        auth = FirebaseAuth.getInstance()

        val loginButton: Button = findViewById(R.id.LoginButton)
        val registrationButton: Button = findViewById(R.id.RegistrationButton)
        val tokenButton: Button = findViewById(R.id.TokenButton)

        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        registrationButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        tokenButton.setOnClickListener {
            startActivity(Intent(this, AuthAdminActivity::class.java))
        }
    }



}
