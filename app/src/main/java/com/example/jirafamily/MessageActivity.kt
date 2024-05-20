package com.example.jirafamily

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.jirafamily.DTO.Message
import com.example.jirafamily.DTO.User
import com.example.jirafamily.DTO.Users
import com.example.jirafamily.adapters.MessageAdapter
import com.example.jirafamily.adapters.UserAdapter
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class MessageActivity : AppCompatActivity(), MessageAdapter.OnItemClickListener {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private var userList = ArrayList<User>()
    private lateinit var usersDatabaseReference: DatabaseReference
    private lateinit var usersChildEventListener: ChildEventListener
    private lateinit var userLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        userList = ArrayList()

        attackUserDatabaseReferenceListener()
        buildRecycleView()
    }

    private fun buildRecycleView(){
        userRecyclerView = findViewById(R.id.UserRecycleView)
        userRecyclerView.setHasFixedSize(true)
        userLayoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(userList)

        userRecyclerView.layoutManager = userLayoutManager
        userRecyclerView.adapter = userAdapter

    }

    private fun attackUserDatabaseReferenceListener() {
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users")
        if(usersChildEventListener == null){
            usersDatabaseReference.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var user:User = snapshot.getValue(User::class.java)!!
//                    user.avatar = R.drawable.account_circle.toString()
                    userList.add(user)
                    userAdapter.notifyDataSetChanged()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    // Ваш код обработки изменения дочернего элемента
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    // Ваш код обработки удаления дочернего элемента
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    // Ваш код обработки перемещения дочернего элемента
                }

                override fun onCancelled(error: DatabaseError) {
                    // Ваш код обработки отмены
                }
            })
            usersDatabaseReference.addChildEventListener(usersChildEventListener)
        }
    }


    override fun onItemClick(position: Int) {
        val intent = Intent(this, ChatActivity::class.java)
        startActivity(intent)
    }
}



