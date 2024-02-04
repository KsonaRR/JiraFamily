package com.example.jirafamily

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirstPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_page)

        var loginButton:Button = findViewById(R.id.LoginButton)
        var registrationButton:Button = findViewById(R.id.RegistrationButton)
        var tokenButton:Button = findViewById(R.id.TokenButton)

        loginButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        })

        registrationButton.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        })

        tokenButton.setOnClickListener {
            showTokenInputDialog()
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
            val enteredToken = editTextToken.text.toString()

            // TODO: Добавить обработку введенного токена

            dialog.dismiss()
        }
    }
}