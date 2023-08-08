package com.example.myapplication.AdminSide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.AdapterCategory
import com.example.myapplication.Models.ModelCategory
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Admin : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityAdminBinding
    //Firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoriesRv: RecyclerView

    //array list
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    //adapter
    private lateinit var adapterCategory: AdapterCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recyclerview
        categoriesRv = findViewById(R.id.categoryRv)

        //init firebase
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        val firebaseuser = firebaseAuth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users")

        //get current username from firebase
        ref.child(firebaseuser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").value
                    val greeting = "Hello,$username"
                    binding.nameTv.text = greeting
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        //search
        binding.searchEt.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterCategory.filter.filter(s)
                }
                catch (e: Exception){

                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        //moving to the notifications
            binding.notificationsIv.setOnClickListener {
            val intent = Intent(this, NotificationsAdmin::class.java)
            startActivity(intent)
        }

        //load categories function
        loadCategories()

        //add categories btn
        binding.addCatBtn.setOnClickListener{
            val intent = Intent(this, AddCategories::class.java )
            startActivity(intent)
            finish()
        }
        //go to pdf file
        binding.pdfBtn.setOnClickListener{
            val intent = Intent(this, AddBooks::class.java )
            startActivity(intent)
            finish()
        }

    }

    private fun loadCategories() {

        //init arraylist
        categoryArrayList = ArrayList()

        //get all categories from firebase database
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data
                categoryArrayList.clear()
                for(ds in snapshot.children){
                    //get data as model
                    val model = ds.getValue(ModelCategory::class.java)

                    //add to arraylist
                    categoryArrayList.add(model!!)
                }
                //setup adapter
                adapterCategory = AdapterCategory(this@Admin, categoryArrayList)
                //set adapter to recycleview
                categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

}