package com.example.myapplication.AdminSide

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.AdapterCategory
import com.example.myapplication.Models.ModelCategory
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CategoriesAdmin : AppCompatActivity() {
    private lateinit var back : ImageView
    private lateinit var add : Button
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var categoriesRv: RecyclerView

    //array list
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    //adapter
    private lateinit var adapterCategory: AdapterCategory


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories_admin)

        //recyclerview
        categoriesRv = findViewById(R.id.categoryRv)
        //init firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        loadCategories()

        //search

        //add categories btn
        add = findViewById(R.id.addCatBtn)
        add.setOnClickListener{
            val intent = Intent(this, AddCategories::class.java )
            startActivity(intent)
            finish()
        }

        //back button
        back = findViewById(R.id.backToHome)
        back.setOnClickListener{
            val intent = Intent(this, Admin::class.java )
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
                adapterCategory = AdapterCategory(this@CategoriesAdmin, categoryArrayList)
                //set adapter to recycleview
                categoriesRv.adapter = adapterCategory
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}