package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@Suppress("DEPRECATION")
class Registration2 : AppCompatActivity() {
    private lateinit var name: EditText
    private lateinit var surname: EditText
    private lateinit var phone: EditText
    private lateinit var next: Button
    private lateinit var usertype: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration2)

        name = findViewById(R.id.etFirstName)
        surname = findViewById(R.id.etSurname)
        phone = findViewById(R.id.etPhoneNumber)
        next = findViewById(R.id.btnNext)
        usertype = "user"

        //firebase for
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        val uid = auth.currentUser?.uid



        next.setOnClickListener {
            val name = name.text.toString()
            val surname = surname.text.toString()
            val phone = phone.text.toString()
            val usertype = usertype

            val progressBar = findViewById<ProgressBar>(R.id.progress_bar_sign_in)

            progressBar.visibility = View.VISIBLE
            next.visibility = View.INVISIBLE

            // Perform sign-in operation or any other async task
            // ...
            val user = User(name,surname,phone,usertype)

            if(uid != null){

                database.child(uid).setValue(user).addOnCompleteListener {

                    if(it.isSuccessful){

                        if (name.isEmpty()) {
                            Toast.makeText(this, "Please fill in the first name", Toast.LENGTH_SHORT).show()
                        }
                        else if (surname.isEmpty()) {
                            Toast.makeText(this, "Please fill in your surname", Toast.LENGTH_SHORT).show()

                        }
                        else if (phone.isEmpty()) {
                            Toast.makeText(this, "Please fill in your cellphone number", Toast.LENGTH_SHORT).show()
                        }
                        else if (phone.length<10) {
                            Toast.makeText(this, "Please fill in a proper cellphone number", Toast.LENGTH_SHORT).show()

                        }

                        else{

                            // Simulate a delay for demonstration purposes (replace with your actual sign-in logic)
                            Handler().postDelayed({
                                // Hide the progress bar and show the sign-in button
                                progressBar.visibility = View.GONE
                                next.visibility = View.VISIBLE

                                // Move to the next screen or perform other actions
                                // ...
                            }, 1000) // 2-second delay for demonstration


                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }


                    }
                    else{

                        Toast.makeText(this, "failed to update user profile",Toast.LENGTH_SHORT).show()

                    }

                }


            }


        }













    }
}