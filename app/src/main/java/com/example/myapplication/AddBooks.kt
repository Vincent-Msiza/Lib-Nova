package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AbsListView.MultiChoiceModeListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class AddBooks : AppCompatActivity() {

    private lateinit var back: ImageView
    private lateinit var book: EditText
    private lateinit var bookOne: EditText
    private lateinit var bookTwo: EditText
    private lateinit var submit: Button

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    //array list to hold pdf categories
    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    //uri of picked pdf
    private var pdfUri: Uri? = null
    //Tag
    private val TAG = "PDF_ADD_TAG"

    //importing the textview from the ui
    lateinit var categoryTV: TextView
    lateinit var attach: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_books)

        back = findViewById(R.id.backToHome)
        categoryTV = findViewById(R.id.categoryTv)
        attach = findViewById(R.id.attachPdfBtn)
        book = findViewById(R.id.titleEt)
        bookOne = findViewById(R.id.descriptionEt)
        bookTwo = findViewById(R.id.authorEt)
        submit = findViewById(R.id.submitBtn)



        //handle click call back




        //configure back to home
        back.setOnClickListener{
            val intent = Intent(this, Admin::class.java)
            startActivity(intent)
            finish()
        }

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        loadPdfCategories()

        //handle click, show category pick dialog
        categoryTV.setOnClickListener{
            categoryPickDialog()
        }


        //handle click, pick pdf intent
        attach.setOnClickListener {
            pdfPickIntent()
        }

        //handle click, pick pdf and start uploading pdf
        submit.setOnClickListener{
            validateData()
        }

    }

    private var title = ""
    private var description = ""
    private var author = ""
    private var category = ""
    private fun validateData() {
        //validate data
        Log.d(TAG, "validateData: validating data")

        //get data
        title = book.text.toString().trim()
        description = bookOne.text.toString().trim()
        author = bookTwo.text.toString().trim()
        category = categoryTV.text.toString().trim()

        //validate data
        if (title.isEmpty()){
            Toast.makeText(this, "Enter Title..", Toast.LENGTH_SHORT).show()
        }
        else if (description.isEmpty()){
            Toast.makeText(this, "Enter Book description", Toast.LENGTH_SHORT).show()
        }
        else if (author.isEmpty()){
            Toast.makeText(this, "Enter author name", Toast.LENGTH_SHORT).show()
        }
        else if (category.isEmpty()){
            Toast.makeText(this, "Pick category..", Toast.LENGTH_SHORT).show()
        }
        else if (pdfUri == null){
            Toast.makeText(this, "Pick PDF...", Toast.LENGTH_SHORT).show()
        }
        else{
            //data validated, begin upload
            uploadPdfToStorage()
        }

    }

    private fun uploadPdfToStorage() {
        //upload pdf to firestore storage
        Log.d(TAG, "uploadPdfToStorage: uploading to storage")

        //show progress dialog

        //timestamp
        val timestamp = System.currentTimeMillis()

        //path of pdf in firebase storage
        val filePathAndName = "Books/$timestamp"
        //storage references
        val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
        storageReference.putFile(pdfUri!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadPdfToStorage: PDF uploaded now getting url")

                //get the url
                val uriTask: Task<Uri> = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedPdfUrl = "${uriTask.result}"

                uploadPdfInfoToDb(uploadedPdfUrl, timestamp)
            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPdfToStorage: failed to upload file due to ${e.message}")

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPdfInfoToDb(uploadedPdfUrl: String, timestamp: Long) {

        Log.d(TAG, "uploadPdfInfoToDb: uploading to db")

        //uid of the current user
        val uid = firebaseAuth.uid

        //setup data to upload
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["author"] = author
        hashMap["categoryId"] = selectedCategoryId
        hashMap["url"] = uploadedPdfUrl
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        //db reference DB > books > BookId > (Book info)
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child("$timestamp")
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "uploadPdfInfoToDb: uploaded to db")
                //progress dialog
                Toast.makeText(this, "Uploaded..", Toast.LENGTH_SHORT).show()



            }
            .addOnFailureListener{e->
                Log.d(TAG, "uploadPdfInfoToDb: failed to upload file due to ${e.message}")

                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()

            }

    }

    private fun loadPdfCategories() {
        Log.d(TAG, "loadPdfCategories: Loading pdf categories")
        //init arraylist
        categoryArrayList = ArrayList()

        //db reference to load categories
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before adding data
                categoryArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelCategory::class.java)
                    //add to arrayList
                    categoryArrayList.add(model!!)
                    Log.d(TAG, "onDataChange: ${model.category}")

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
    }

    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""



    private fun categoryPickDialog(){
        Log.d(TAG, "categoryPickDialog: showing pdf category pick dialog")
        //get string array

        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices){
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray){dialog, which ->
                //handle item click
                selectedCategoryTitle = categoryArrayList[which].category
                selectedCategoryId = categoryArrayList[which].id
                //set category to textview
                categoryTV.text = selectedCategoryTitle

                Log.d(TAG, "categoryPickDialog: Selected category ID: $selectedCategoryId")
                Log.d(TAG, "categoryPickDialog: Selected category Tittle: $selectedCategoryTitle")
            }
            .show()
    }

    private fun pdfPickIntent(){
        Log.d(TAG, "pdfPickIntent: starting pdf pick intent")

        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT

        pdfActivityResultLauncher.launch(intent)


    }

    val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),

        ActivityResultCallback<ActivityResult>{ result ->
            if (result.resultCode == RESULT_OK){
                Log.d(TAG, "PDF Picked: ")
                pdfUri = result.data!!.data
            }
            else{
                Log.d(TAG, "PDF Pick cancelled: ")
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()

            }



        }

    )



}