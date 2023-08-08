package com.example.myapplication.UserAuth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

class Forgotpassword : AppCompatActivity() {

    private lateinit var etEmailAddress : EditText
    private lateinit var btnReset : Button
    private lateinit var toLogin : TextView

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        etEmailAddress=findViewById(R.id.etEmailAddress)
        btnReset=findViewById(R.id.btnReset)
        toLogin=findViewById(R.id.toLogin)

        auth= FirebaseAuth.getInstance()



        btnReset.setOnClickListener {
            reset()
        }

        toLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }
    private fun reset(){

        val sPassword = etEmailAddress.text.toString()

        if (sPassword.isEmpty()) {
            Toast.makeText(this, "Email can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        auth.sendPasswordResetEmail(sPassword)

            .addOnSuccessListener {
                Toast.makeText(this,"Please check your email", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener{
                Toast.makeText(this,"email does not exist", Toast.LENGTH_SHORT).show()

            }


    }


}