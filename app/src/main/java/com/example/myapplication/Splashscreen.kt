package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.SharedPreferences

@SuppressLint("CustomSplashScreen")
class Splashscreen : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Handler().postDelayed({
        Handler(Looper.getMainLooper()).postDelayed({

            // get reference to button
            sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

            // Check if the page has been shown before
            val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

            if (isFirstTime) {
                // Set the flag to false indicating the page has been shown
                val editor = sharedPreferences.edit()
                editor.putBoolean("isFirstTime", false)
                editor.apply()
                // Show the page for the first time
                val intent = Intent(this, OnBoarding::class.java)
                startActivity(intent)
                finish()

            } else {
                // Page has already been shown before, do something else
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 3000) // 3000 is the delayed time in milliseconds.

    }
}