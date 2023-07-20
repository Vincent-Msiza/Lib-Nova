package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityMainBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: LibraryFragment.ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //get current user names from firebase
        val fireBaseUser = firebaseAuth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users")
        //get user name
        ref.child(fireBaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").value
                    val greeting = "Hey, $username"
                    binding.nameTv.text = greeting
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        binding.menuIb.setOnClickListener {
            val intent = Intent(this, MenuUsers::class.java)
            startActivity(intent)
        }



    }
}