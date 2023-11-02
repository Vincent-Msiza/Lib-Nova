package com.example.myapplication.UserAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.myapplication.UserSide.MainActivity
import com.example.myapplication.R
import com.example.myapplication.Classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Registration2 : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var phone: EditText
    private lateinit var next: Button
    private lateinit var usertype: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration2)

        name = findViewById(R.id.etFirstName)
        surname = findViewById(R.id.etSurname)
        phone = findViewById(R.id.etPhoneNumber)
        next = findViewById(R.id.btnNext)
        usertype = "user"

        // Firebase setup
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        val uid = auth.currentUser?.uid

        next.setOnClickListener {
            val nameValue = name.text.toString()
            val surnameValue = surname.text.toString()
            val phoneValue = phone.text.toString()

            val progressBar = findViewById<ProgressBar>(R.id.progress_bar_sign_in)

            progressBar.visibility = View.VISIBLE
            next.visibility = View.INVISIBLE

            if (isValidInput(nameValue, surnameValue, phoneValue)) {
                val user = User(nameValue, surnameValue, phoneValue, usertype)

                if (uid != null) {
                    database.child(uid).setValue(user).addOnCompleteListener {
                        if (it.isSuccessful) {
                            // Delay for demonstration purposes (replace with your actual logic)
                            Handler().postDelayed({
                                progressBar.visibility = View.GONE
                                next.visibility = View.VISIBLE

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }, 1000) // 1-second delay
                        } else {
                            progressBar.visibility = View.GONE
                            next.visibility = View.VISIBLE
                            Toast.makeText(this, "Failed to update user profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                progressBar.visibility = View.GONE
                next.visibility = View.VISIBLE
            }
        }
    }

    private fun isValidInput(name: String, surname: String, phone: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(this, "Please fill in the first name", Toast.LENGTH_SHORT).show()
                false
            }
            surname.isEmpty() -> {
                Toast.makeText(this, "Please fill in your surname", Toast.LENGTH_SHORT).show()
                false
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "Please fill in your cellphone number", Toast.LENGTH_SHORT).show()
                false
            }
            phone.length < 10 -> {
                Toast.makeText(this, "Please fill in a proper cellphone number", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}
