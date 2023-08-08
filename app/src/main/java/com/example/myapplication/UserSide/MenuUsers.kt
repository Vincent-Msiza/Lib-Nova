package com.example.myapplication.UserSide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.UserAuth.Login
import com.example.myapplication.databinding.ActivityMenuUsersBinding
import com.google.firebase.auth.FirebaseAuth

class MenuUsers : AppCompatActivity() {
    private lateinit var binding: ActivityMenuUsersBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init firebase

        binding.backToHome.setOnClickListener {
            onBackPressed()
        }
        //logout
        binding.logoutTv.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
        //go home from the menu
        binding.homeTv.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }



    }
}