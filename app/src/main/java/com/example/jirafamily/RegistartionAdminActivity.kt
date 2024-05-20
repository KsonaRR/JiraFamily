package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistartionAdminActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var repeatPassword: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var regButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registartion_admin)

        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        repeatPassword = findViewById(R.id.repeatPasswordField)
        regButton = findViewById(R.id.logButton)
        auth = Firebase.auth

        regButton.setOnClickListener {
            val adminEmail = email.text.toString().trim()
            val adminPassword = password.text.toString().trim()
            val repeatUserPassword = repeatPassword.text.toString().trim()

            // Проверка на совпадение паролей
            if (adminPassword == repeatUserPassword) {
                registerAdmin(adminEmail, adminPassword)
            } else {
                Toast.makeText(this, "Пароль не совпадает!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функция регистрации нового пользователя
    private fun registerAdmin(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Регистрация успешна, переход на следующий экран
                    startActivity(Intent(this, FillingDataMain::class.java))
                    finish()
                } else {
                    // Регистрация не удалась, вывод сообщения об ошибке
                    Toast.makeText(baseContext, "Ошибка регистрации: ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}