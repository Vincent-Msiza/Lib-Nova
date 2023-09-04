package com.example.myapplication.UserAuth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.AdminSide.Admin
import com.example.myapplication.UserSide.MainActivity
import com.example.myapplication.R
import com.example.myapplication.UserSide.Fragments.HomeUserFragment
import com.example.myapplication.UserSide.MainUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class Login : AppCompatActivity() {
    private lateinit var tvRedirectSignUp: TextView
    lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    lateinit var btnLogin: Button
    private lateinit var forgotpassword: TextView

    // Creating firebaseAuth object
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // View Binding
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)
        forgotpassword = findViewById(R.id.forgot_password)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar_sign_in)



        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            btnLogin.visibility = View.INVISIBLE

            // Perform sign-in operation or any other async task
            // ...
            // Simulate a delay for demonstration purposes (replace with your actual sign-in logic)
            Handler().postDelayed({
                // Hide the progress bar and show the sign-in button
                progressBar.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE

                // Move to the next screen or perform other actions
                // ...
            }, 6000) // 2-second delay for demonstration

            login()
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
            // using finish() to end the activity
            finish()
        }
        //for the forgot password btn
        forgotpassword.setOnClickListener {
            val intent = Intent(this, Forgotpassword::class.java)
            startActivity(intent)
            // using finish() to end the activity

        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Email and Password can't be blank", Toast.LENGTH_SHORT).show()
            return
        }
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {

               checkuser()

            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }


    }

    private fun checkuser() {
        val firebaseuser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseuser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val usertype = snapshot.child("usertype").value

                    if (usertype == "user"){
                        startActivity(Intent(this@Login, MainUser::class.java))
                        finish()
                    }
                    else if(usertype == "admin"){
                        startActivity(Intent(this@Login, Admin::class.java))
                        finish()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            }


            )
    }


}
