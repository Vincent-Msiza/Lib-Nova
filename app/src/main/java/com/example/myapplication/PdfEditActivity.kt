package com.example.myapplication

import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.myapplication.databinding.ActivityPdfEditBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfEditBinding
    private companion object{
        private const val TAG = "PDF_EDIT_TAG"

    }
    private var bookId = ""

    private lateinit var progressDialog: ProgressDialog
    private lateinit var categoryTitleArrayList:ArrayList<String>
    private lateinit var categoryIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id to edit the book info
        bookId = intent.getStringExtra("bookId")!!

        //setup progress dialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        loadCategories()

        loadBookInfo()

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        //handle click to pick category
        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }

        //handle click to begin update
        binding.submitBtn.setOnClickListener {
            validateDate()
        }

    }

    private fun loadBookInfo() {
        Log.d(TAG, "LoadBookInfo: Loading book info")

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book info
                    selectedCategoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val title = snapshot.child("title").value.toString()

                    //set to view
                    binding.titleEt.setText(title)
                    binding.descriptionEt.setText(description)

                    //load book category info uisng categoryid
                    val refBookCategory = FirebaseDatabase.getInstance().getReference("Categories")
                    refBookCategory.child(selectedCategoryId)
                        .addListenerForSingleValueEvent(object: ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //get category
                                val category = snapshot.child("category").value
                            //set to textView
                                binding.categoryTv.text = category.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    //validate if the user has entered information
    private var title = ""
    private var description = ""
    private fun validateDate() {
        title = binding.titleEt.text.toString().trim()
        description= binding.descriptionEt.text.toString().trim()
        if (title.isEmpty()){
            Toast.makeText( this, "Enter title",Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
        }
        else if (selectedCategoryId.isEmpty()){
            Toast.makeText(this,"Enter title", Toast.LENGTH_SHORT).show()
        }
        else{
            updatePdf()
        }

    }

    private fun updatePdf() {

        Log.d(TAG, "updatePdf: Staring to update pdf info")
        //show progress
//        progressDialog.setMessage("Updating book info")
//        progressDialog.show()

        //setup data to update to db
        val hashMap = HashMap<String, Any>()
        hashMap["title"] = "$title"
        hashMap["description"] = "$description"
        hashMap["categoryId"] = "$selectedCategoryId"

        //start updating
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this,"Updated successfully..", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->

                Toast.makeText(this,"Failed to update due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //
    private var selectedCategoryId =""
    private var selectedCategoryTitle =""
    private fun categoryDialog() {
        //show dialog pick category of book

        //make string array from arraylist of string
        val categoriesArray = arrayOfNulls<String>(categoryTitleArrayList.size)
        for(i in categoryIdArrayList.indices){
            categoriesArray[i] = categoryTitleArrayList[i]
        }

        //alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Category")
            .setItems(categoriesArray){dialog, position ->
                //handle click save clicked category id and title
                selectedCategoryId = categoryIdArrayList[position]
                selectedCategoryTitle = categoryTitleArrayList[position]

                //set to textview
                binding.categoryTv.text = selectedCategoryTitle

            }
            .show()
    }

    //load categories class
    private fun loadCategories() {
        Log.d(TAG, "loadCategories: loadsing categories...")
        categoryTitleArrayList = ArrayList()
        categoryIdArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryTitleArrayList.clear()
                categoryIdArrayList.clear()

                for(ds in snapshot.children){
                    val id= "${ds.child("id").value}"
                    val category= "${ds.child("category").value}"

                    categoryIdArrayList.add(id)
                    categoryIdArrayList.add(category)

                    Log.d(TAG, "onDataChage:Category ID $id")
                    Log.d(TAG, "onDataChage:Category  $category")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}