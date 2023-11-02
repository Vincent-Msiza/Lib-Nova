package com.example.myapplication

import android.Manifest.permission.MANAGE_MEDIA
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import com.example.myapplication.Classes.MyApplication
import com.example.myapplication.databinding.ActivityPdfDetailBinding
import com.example.myapplication.databinding.DialogCommentAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.io.FileOutputStream

class PdfDetailActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityPdfDetailBinding
    private companion object{
        const val TAG = "BOOK_DETAILS_TAG"
    }
    //book id
    private var bookId = ""
    private var  bookTitle =  ""
    private var bookUrl = ""

    //will hold a boolean value false/true
    private var isInMyFavorite = false

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent, will load book info using bookid
        bookId = intent.getStringExtra("bookId")!!

        progressDialog = ProgressDialog(this )
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase
        firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser !=null ){
            checkIsFavorite()
        }

        //increment book view count
        MyApplication.incrementBookViewCount(bookId)
        //load book details
        loadBookDetail()

        binding.backToHome.setOnClickListener {
            onBackPressed()
        }

        //handle click to open pdf view activity
        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }

        binding.downloadBookBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                downloadBook()
            }
            else{
                requestStoragePermissionLauncher.launch(WRITE_EXTERNAL_STORAGE)
            }

        }

        binding.addCommentBtn.setOnClickListener {
            if (firebaseAuth.currentUser == null){

            }else{
                addCommentDialog()
            }
        }

        //handle click add or remove favorite
        binding.favoriteBtn.setOnClickListener {
            //user is logged in we can do favorite functionality
            if (isInMyFavorite){
                removeFromFavorite()
            }
            else{
                val timestamp = System.currentTimeMillis()

                //setup data in db
                val hashMap = HashMap<String, Any>()
                hashMap["bookId"] = bookId
                hashMap["timestamp"] = timestamp

                //save to db
                val ref = FirebaseDatabase.getInstance().getReference("users")
                ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
                    .setValue(hashMap)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e->
                        //failed to add to favourites
                        Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }

    private var comment = ""
    private fun addCommentDialog() {
        val commentAddBinding = DialogCommentAddBinding.inflate(LayoutInflater.from(this))

        //setup dialog
        val builder = AlertDialog.Builder(this, R.style.CustomDialog)
        builder.setView(commentAddBinding.root)

        //create and show alert dialog
        val alertDialog = builder.create()
        alertDialog.show()

        //handle click dismiss dialog
        commentAddBinding.backBtn.setOnClickListener { alertDialog.dismiss() }

        //handle click add comment
        commentAddBinding.submitBtn.setOnClickListener {
            //set data
            comment = commentAddBinding.commentEt.text.toString().trim()
            //validate data
            if (comment.isEmpty()){
                    Toast.makeText(this, "Enter comment...", Toast.LENGTH_SHORT).show()

            }else{
                alertDialog.dismiss()
                addComment()

            }

        }

    }

    private fun addComment() {
        //show progress dialog
        progressDialog.setMessage("Adding comment")
        progressDialog.show()

        //timestamp
        val timestamp = "${System.currentTimeMillis()}"

        //setup data to add in db for comment
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["bookId"] = "$bookId"
        hashMap["timestamp"] = "$timestamp"
        hashMap["comment"] = "$comment"
        hashMap["uid"] = "${firebaseAuth.uid}"


        //db path to add data into it
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(bookId).child("Comments").child(timestamp)
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Comment added...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to add comment due to ${e.message} ", Toast.LENGTH_SHORT).show()
            }

    }

    private val requestStoragePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            downloadBook()
        }
        else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadBook(){

        progressDialog.setMessage("Downloading Book")
        progressDialog.show()

        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.getBytes(Constants.MAX_BTYES_PDF)
            .addOnSuccessListener {bytes ->
                saveToDownloadsFolder(bytes)
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToDownloadsFolder(bytes: ByteArray?) {

        val nameWithExtention = "${System.currentTimeMillis()}.pdf"
        try{
            val downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadsFolder.mkdir()

            val filePath = downloadsFolder.path + "/"+ nameWithExtention

            val  out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()

            Toast.makeText(this, "saved to Downloads Folder ", Toast.LENGTH_SHORT).show()
            progressDialog.dismiss()
//            incrementDownloadCount()
        }
        catch (e: Exception){
            progressDialog.dismiss()
            Toast.makeText(this, "Failed to save due to ${e.message} ", Toast.LENGTH_SHORT).show()

        }

    }

    private fun incrementDownloadCount() {
        val ref = FirebaseDatabase.getInstance().getReference("books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var downloadsCount = "${snapshot.child("downloadsCount").value}"
                    if (downloadsCount == "" || downloadsCount ==  "null"){
                        downloadsCount = "0"
                    }
                    val newDownloadCount: Long = downloadsCount.toLong() + 1
                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap[""] = newDownloadCount

                    val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                    dbRef.child(bookId)
                        .updateChildren(hashMap)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener{ e->

                        }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }

    private fun loadBookDetail() {
        //books > bookId > Details
        var ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get data
                    val categoryId = "${snapshot.child("categoryId").value}"
                    val description = "${snapshot.child("description").value}"
                    val downloadsCount = "${snapshot.child("downloadsCount").value}"
                    val timestamp = "${snapshot.child("timestamp").value}"
                    bookTitle = "${snapshot.child("title").value}"
                    val uid = "${snapshot.child("uid").value}"
                    bookUrl = "${snapshot.child("url").value}"
                    val viewsCount = "${snapshot.child("viewsCount").value}"

                    //format date

                    //load pdf category
                    MyApplication.loadCategory(categoryId, binding.categoryTv)
                    //load pdf thumbnail
                    MyApplication.loadPdfFromUrlSinglePage("$bookUrl", "$bookTitle", binding.pdfView, binding.progressBar, binding.pagesTv)
                    //load pdf size but for us its rating
                    // MyApplication.loadPdfSize("$url", "$title", binding.ratingTv)

                    //set data
                    binding.titleTv.text = bookTitle
                    binding.descriptionTv.text = description
                    binding.viewsTv.text = viewsCount
                    binding.downloadsTv.text = downloadsCount





                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun checkIsFavorite(){
        //check if user book is in fav or not

        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    isInMyFavorite = snapshot.exists()
                    if (isInMyFavorite){
                        binding.favoriteBtn.text = "Remove Favorite"
                    }
                    else{
                        binding.favoriteBtn.text = "Add Favorite"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    private fun addToFavorite(){

        val timestamp = System.currentTimeMillis()

        //setup data in db
        val hashMap = HashMap<String, Any>()
        hashMap["bookId"] = bookId
        hashMap["timestamp"] = timestamp

        //save to db
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Added to favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                //failed to add to favourites
                Toast.makeText(this, "Failed to add to fav due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
    private fun removeFromFavorite(){

        //database ref
        val ref = FirebaseDatabase.getInstance().getReference("users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Removed from favorite", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {e->
                //failed to add to favourites
                Toast.makeText(this, "Failed to remove from fav due to ${e.message}", Toast.LENGTH_SHORT).show()

            }

    }


}