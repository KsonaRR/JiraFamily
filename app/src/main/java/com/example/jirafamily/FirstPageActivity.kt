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
//            showTokenInputDialog()
        }
    }

    private fun showTokenInputDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(R.layout.dialog_input_token, null)
        builder.setView(dialogView)

        val editTextToken = dialogView.findViewById<EditText>(R.id.editTextToken)
        val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)

        val dialog = builder.create()
        dialog.show()

        btnContinue.setOnClickListener {
            val enteredToken = editTextToken.text.toString().trim()

            if (enteredToken.isNotEmpty()) {
                checkTokenAndProceed(enteredToken)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите токен", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkTokenAndProceed(token: String) {
        val db = FirebaseFirestore.getInstance()
        val adminsRef = db.collection("admins")

        adminsRef.whereEqualTo("token", token).get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val adminId = documents.documents[0].id
                    val intent = Intent(this, FillingDataSecond::class.java)
                    intent.putExtra("token", token)
                    intent.putExtra("adminId", adminId)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Неверный токен", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Ошибка при проверке токена", Toast.LENGTH_SHORT).show()
            }
    }

}
