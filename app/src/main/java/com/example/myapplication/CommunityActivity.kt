package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class CommunityActivity : AppCompatActivity() {

    private val repository = FirebaseRepository()
    private val messages: MutableList<Message> = mutableListOf()
    private lateinit var messageAdapter: MessageAdapter
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        val messageEditText: EditText = findViewById(R.id.messageEditText)
        val sendButton: Button = findViewById(R.id.sendButton)
        val messagesRecyclerView: RecyclerView = findViewById(R.id.messagesRecyclerView)

        // Setup RecyclerView
//        messageAdapter = MessageAdapter(messages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = messageAdapter

        sendButton.setOnClickListener {
            val content = messageEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                val message = Message(auth.currentUser?.uid ?: "", content, System.currentTimeMillis())
                repository.addMessage(message, {
                    // Message added successfully
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                    messageEditText.text.clear()
                }, {
                    // Handle error
                    Toast.makeText(this, "Error sending message.", Toast.LENGTH_SHORT).show()
                })
            } else {
                Toast.makeText(this, "Message cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }


        // Fetch messages and display them in the RecyclerView
        repository.getMessages({ newMessages ->
            messages.clear()
            messages.addAll(newMessages)
            messageAdapter.notifyDataSetChanged()
        }, { exception ->
            Toast.makeText(this, "Error fetching messages: ${exception.message}", Toast.LENGTH_SHORT).show()
        })
    }
}
