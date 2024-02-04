package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistrationActivity : AppCompatActivity() {
    private lateinit var email: EditText;
    private lateinit var password: EditText;
    private lateinit var repeatPassword: EditText;
    private lateinit var auth: FirebaseAuth
    private lateinit var regButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        email = findViewById(R.id.emailField)
        password = findViewById(R.id.passwordField)
        repeatPassword = findViewById(R.id.repeatPasswordField)
        regButton = findViewById(R.id.logButton)
        auth = Firebase.auth
        regButton.setOnClickListener(View.OnClickListener {
            if (password.text.toString().trim() == repeatPassword.text.toString().trim()) {
                registerUser(email.text.toString().trim(), password.text.toString().trim())
            } else {
                Toast.makeText(this, "Пароль не совпадает!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun registerUser(email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    startActivity(Intent(this, FillingDataMain::class.java))
                } else {
                    Toast.makeText(this, "Проверьте введенные данные" , Toast.LENGTH_SHORT).show()
                }
            }
    }
}