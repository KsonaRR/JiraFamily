package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class AuthAdminActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth_admin)

        auth = FirebaseAuth.getInstance()

        val loginButton: Button = findViewById(R.id.LoginButton)
        val registrationButton: Button = findViewById(R.id.RegistrationButton)


        loginButton.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }

        registrationButton.setOnClickListener {
            startActivity(Intent(this, RegistartionAdminActivity::class.java))
        }

    }
}