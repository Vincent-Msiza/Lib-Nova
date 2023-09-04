package com.example.myapplication.UserSide

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.AdminSide.Admin
import com.example.myapplication.R
import com.example.myapplication.UserSide.Fragments.HomeUserFragment
import com.example.myapplication.UserSide.Fragments.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp

class MainUser : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user)
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        bottomNav = findViewById(R.id.botton_nav)
//        removing the effect of grey icons or default tint
//        bottomNav.itemIconTintList = null

        loadFragment(HomeUserFragment())

       bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
               R.id.home -> {
//                   loadFragment(HomeUserFragment())

                   return@setOnItemSelectedListener true
               }
              R.id.search -> {
//                   loadFragment(SearchFragment())
                   return@setOnItemSelectedListener true
               }
//                R.id.nova -> {
//                    loadFragment(Novaragment())
//                    return@setOnItemSelectedListener true
//                }
                R.id.book -> {
//                    loadFragment(LibraryFragment())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
//                    loadFragment(AccountFragment())
                    return@setOnItemSelectedListener true
                }

            }
            false
       }





    }
//    class to load the fragments
private  fun loadFragment(fragment: Fragment){
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.container,fragment)
    transaction.commit()
}

}