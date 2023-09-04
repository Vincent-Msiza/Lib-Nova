package com.example.myapplication.AdminSide

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Adapters.AdapterPdfAdmin
import com.example.myapplication.Models.ModelPdf
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityBooksAdminBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BooksAdmin : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityBooksAdminBinding

    private lateinit var back : ImageView
    private lateinit var addbooks : Button
    private lateinit var booksRv : RecyclerView
    private lateinit var search: EditText

    companion object{
        const val TAG = "PDF_LIST_TAG"
    }

    //category id, title
    private var categoryId = ""
    private var category = ""

    //array to hold the book list
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    //adapter
    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBooksAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recycle view
        booksRv = findViewById(R.id.booksRv)

        //add books button
        addbooks = findViewById(R.id.addBooksBtn)

        //back button
        back = findViewById(R.id.backToHome)

        back.setOnClickListener{
            val intent = Intent(this, Admin::class.java )
            startActivity(intent)
            finish()
        }

        addbooks.setOnClickListener{
            val intent = Intent(this, AddBooks::class.java )
            startActivity(intent)
            finish()
        }

        //get from intent that we passed from adapter
        val intent = intent
        categoryId = intent.getStringExtra("categoryId").toString()
        category = intent.getStringExtra("category").toString()

        //set pdf category


        //loadPdf list
        loadPdfList()

        //search
        binding.searchEt.addTextChangedListener(textWatcher)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Implement the logic you want to perform before the text changes.
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Implement the logic you want to perform when the text changes.
            try {
                adapterPdfAdmin.filter!!.filter(s)
            }
            catch (e: Exception){
                Log.d(TAG, "onTextChanged: ${e.message}")

            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Implement the logic you want to perform after the text changes.
        }
    }

    private fun loadPdfList() {
        //init arrayList
        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //clear list before start adding data into it
                    pdfArrayList.clear()
                    for(ds in snapshot.children){
                        //get data
                        val model = ds.getValue(ModelPdf::class.java)
                        //add to list
                        if (model != null) {
                            pdfArrayList.add(model)
                            Log.d(TAG, "onDataChange: ${model.title} ${model.categoryId}")
                        }
                    }
                    //setup adapter
                    adapterPdfAdmin = AdapterPdfAdmin(this@BooksAdmin, pdfArrayList)
                    binding.booksRv.adapter = adapterPdfAdmin

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}