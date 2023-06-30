package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Admin : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        bottomNav = findViewById(R.id.bottom_navigation)

        loadFragment(HomeAdmin())
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeAdmin())

                    return@setOnItemSelectedListener true
                }
                R.id.search -> {
                    loadFragment(SearchAdmin())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    loadFragment(ProfileAdmin())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }



    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}