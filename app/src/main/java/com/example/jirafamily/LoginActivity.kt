package com.example.jirafamily

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.jirafamily.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var button:Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        button = findViewById(R.id.logButton)
        auth = Firebase.auth

        button.setOnClickListener {
            logInUser(email.text.toString().trim(), password.text.toString().trim())
        }
    }

    private fun logInUser(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    startActivity(Intent(this, ProfileActivity::class.java))
                } else {
                    Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                }
            }
    }
}