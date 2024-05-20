package com.example.jirafamily

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginAdminActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var button: Button
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        button = findViewById(R.id.logButton)
        auth = Firebase.auth

        button.setOnClickListener {
            // Получение введенного email и пароля
            val userEmail = email.text.toString().trim()
            val userPassword = password.text.toString().trim()

            // Проверка наличия email и пароля
            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Вход в аккаунт с использованием введенного email и пароля
            auth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Успешный вход, переход на главный экран
                        startActivity(Intent(this, ProfileAdminActivity::class.java))
                        finish()
                    } else {
                        // Ошибка входа, вывод сообщения об ошибке
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Ошибка входа: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }

    }


}