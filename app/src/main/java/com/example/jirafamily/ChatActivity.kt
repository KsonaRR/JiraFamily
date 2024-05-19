package com.example.jirafamily

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.jirafamily.DTO.AwesomeMessage
import com.example.jirafamily.adapters.AwesomeMessageAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChatActivity: AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var adapter: AwesomeMessageAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendMessageButton: Button
    private lateinit var messageEditText: EditText

    private lateinit var userName: String

    private lateinit var database: FirebaseDatabase
    private lateinit var messagesDatabaseReference: DatabaseReference
    private lateinit var messageChildEventListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        database = FirebaseDatabase.getInstance()
        messagesDatabaseReference = database.getReference().child("messages")

        progressBar = findViewById(R.id.progressBar)
        sendImageButton = findViewById(R.id.sendPhotoButton)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.messageEditText)

        userName = "Default User"

        messageListView = findViewById(R.id.messageListView)
        val awesomeMessages = mutableListOf<AwesomeMessage>()
        adapter = AwesomeMessageAdapter(this, R.layout.item_awesome_message, awesomeMessages)
        messageListView.adapter = adapter

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Ваш код здесь
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Ваш код здесь
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendMessageButton.isEnabled = s.toString().trim().isNotEmpty()
            }
        })

        messageEditText.filters = arrayOf(InputFilter.LengthFilter(500))

        sendMessageButton.setOnClickListener {
            val message = AwesomeMessage(
                text = messageEditText.text.toString(),
                name = userName,
                imageUrl = null
            )
            messagesDatabaseReference.push().setValue(message)
            messageEditText.setText("")
        }

        sendImageButton.setOnClickListener {
            // Обработчик нажатия на кнопку отправки изображения
        }

        progressBar.visibility = View.VISIBLE

        messageChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message: AwesomeMessage? = snapshot.getValue(AwesomeMessage::class.java)
                if (message != null) {
                    adapter.add(message)
                }
                progressBar.visibility = View.GONE
            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Ваш код обработки события изменения дочернего узла
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Ваш код обработки события удаления дочернего узла
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Ваш код обработки события перемещения дочернего узла
            }

            override fun onCancelled(error: DatabaseError) {
                // Ваш код обработки отмены события
            }
        }
        messagesDatabaseReference.addChildEventListener(messageChildEventListener)
    }
}
