package com.example.myapplication

import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val messagesCollection = db.collection("messages")

    fun addMessage(message: Message, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        messagesCollection.add(message).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }

    fun getMessages(onSuccess: (List<Message>) -> Unit, onFailure: (Exception) -> Unit) {
        messagesCollection.orderBy("timestamp").get().addOnSuccessListener { querySnapshot ->
            val messages = querySnapshot.documents.mapNotNull { it.toObject(Message::class.java) }
            onSuccess(messages)
        }.addOnFailureListener {
            onFailure(it)
        }
    }
}
