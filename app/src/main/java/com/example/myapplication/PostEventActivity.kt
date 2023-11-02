package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PostEventActivity : AppCompatActivity() {

    // Declare UI components
    private lateinit var eventTitle: EditText
    private lateinit var eventDescription: EditText
    private lateinit var postEventButton: Button

    // Declare a reference to Firebase Database
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_event)

        // Initialize UI components
        eventTitle = findViewById(R.id.eventTitle)
        eventDescription = findViewById(R.id.eventDescription)
        postEventButton = findViewById(R.id.postEventButton)

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Events")

        // Set onClickListener for the button
        postEventButton.setOnClickListener {
            val title = eventTitle.text.toString().trim()
            val description = eventDescription.text.toString().trim()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                val eventId = databaseReference.push().key
                val event = Event(eventId!!, title, description)

                // Post the event to Firebase
                databaseReference.child(eventId).setValue(event).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Event posted successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to post event!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill out all fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

data class Event(val eventId: String, val title: String, val description: String)
