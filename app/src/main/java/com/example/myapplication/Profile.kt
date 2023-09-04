package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.myapplication.Classes.MyApplication
import com.example.myapplication.databinding.ActivityProfile2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Profile : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityProfile2Binding
    //firebase
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfile2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        loadUserinfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

    }

    private fun loadUserinfo() {
        //db ref to load user info
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get user info
                    val email = "${snapshot.child("phone").value}"
                    val name = "${snapshot.child("name").value}"
                    val profileImage = "${snapshot.child("profileImage").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    val uid = "${snapshot.child("uid").value}"
                    val userType = "${snapshot.child("usertype").value}"

                    //convert timestamp to proper date
//                    val formattedDate = MyApplication.formatTimeStamp(timestamp.toLong())

                    //set data
                    binding.nameTv.text = name
                    binding.emailTv.text = email
//                    binding.memberDateTv.text = formattedDate
                    binding.accountTypeTv.text = userType

                    //set image
                    try{
                        Glide.with(this@Profile).load(profileImage).placeholder(R.drawable.baseline_person_24)
                            .into(binding.profileIv)
                    }
                    catch (e:Exception){

                    }


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}