package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddCategories : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var add: Button
    lateinit var input: TextInputEditText
    lateinit var back: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_categories)

        add = findViewById(R.id.btnAddCategory)
        input = findViewById(R.id.etCategory)
        back = findViewById(R.id.backToHome)

        //firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //configure back to home
        back.setOnClickListener{
            val intent = Intent(this, Admin::class.java)
            startActivity(intent)
            finish()
        }

        //begin to upload
        add.setOnClickListener {

            val category = input.text.toString()

            if(category.isEmpty()){

                Toast.makeText(this, "Enter category...", Toast.LENGTH_SHORT).show()

            }
            else{

                //get timestamp
                val timestamp = System.currentTimeMillis()

                //setup date to add in firebase
                val hashMap = HashMap<String, Any>()
                hashMap["id"] = "$timestamp"
                hashMap["category"] = category
                hashMap["timestamp"] = timestamp
                hashMap["uid"] = "${firebaseAuth.uid}"

                //add to firebase
                val ref = FirebaseDatabase.getInstance().getReference("Categories")
                ref.child("$timestamp")
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "category is successfully added", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener{
                        Toast.makeText(this, "Failed to add category", Toast.LENGTH_SHORT).show()
                    }
            }



        }


    }

}