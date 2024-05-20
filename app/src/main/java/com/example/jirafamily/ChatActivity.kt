package com.example.jirafamily

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.jirafamily.DTO.AwesomeMessage
import com.example.jirafamily.adapters.AwesomeMessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatActivity : AppCompatActivity() {

    private lateinit var messageListView: ListView
    private lateinit var adapter: AwesomeMessageAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var sendImageButton: ImageButton
    private lateinit var sendMessageButton: Button
    private lateinit var messageEditText: EditText

    private val RC_IMAGE_PICKER: Int = 123

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var messagesDatabaseReference: DatabaseReference
    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var messageChildEventListener: ChildEventListener
    private lateinit var recipientUserId: String
    private lateinit var storageRef: StorageReference
    private lateinit var userName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        database = FirebaseDatabase.getInstance()
        messagesDatabaseReference = database.getReference().child("messages")
        usersDatabaseReference = database.getReference().child("users")
        database = FirebaseDatabase.getInstance()
        storageRef = FirebaseStorage.getInstance().reference.child("chat_images")

        auth = FirebaseAuth.getInstance()
        recipientUserId = intent.getStringExtra("recipientUserId").toString()

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val currentUserUid = it.uid
            // Получение имени текущего пользователя из базы данных Firebase
            usersDatabaseReference.child(currentUserUid).child("name")
                .get()
                .addOnSuccessListener { dataSnapshot ->
                    userName = dataSnapshot.value as? String ?: "Admin"
                }
                .addOnFailureListener { exception ->
                    Log.e("ChatActivity", "Error getting user name", exception)
                }
        }


        progressBar = findViewById(R.id.progressBar)
        sendImageButton = findViewById(R.id.sendPhotoButton)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        messageEditText = findViewById(R.id.messageEditText)

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
            val messageText = messageEditText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = AwesomeMessage(
                    text = messageText,
                    name = userName,
                    sender = auth.currentUser?.uid ?: "",
                    recipient = recipientUserId,
                    imageUrl = null
                )
                messagesDatabaseReference.push().setValue(message)
                messageEditText.setText("")
            }
        }

        sendImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(
                Intent.createChooser(intent, "Choose an image"),
                RC_IMAGE_PICKER
            )
        }

        progressBar.visibility = View.VISIBLE

        messageChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(AwesomeMessage::class.java)
                val senderId = message?.sender
                val recipientId = message?.recipient

                if (message != null && senderId != null && recipientId != null &&
                    (senderId == auth.currentUser?.uid && recipientId == recipientUserId) ||
                    (senderId == recipientUserId && recipientId == auth.currentUser?.uid)) {
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
    private fun sendMessageWithImage(imageUrl: String) {
        val message = AwesomeMessage(
            text = "", // Текстовое сообщение пустое, так как это изображение
            name = userName,
            sender = auth.currentUser?.uid ?: "",
            recipient = recipientUserId,
            imageUrl = imageUrl
        )
        messagesDatabaseReference.push().setValue(message)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE_PICKER && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            selectedImageUri?.let {
                val imageRef = storageRef.child("images/${System.currentTimeMillis()}")
                imageRef.putFile(it)
                    .addOnSuccessListener { taskSnapshot ->
                        // Получаем URL загруженного изображения и отправляем сообщение с ним
                        imageRef.downloadUrl.addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            sendMessageWithImage(imageUrl)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("ChatActivity", "Error uploading image: ${exception.message}")
                    }
            }
        }
    }

}
