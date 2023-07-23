package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.myapplication.databinding.ActivityPdfViewBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfViewActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityPdfViewBinding
    //TAG
    private companion object{
        const val TAG = "PDF_TAG_VIEW"
    }

    //book id
    var bookId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get book id from intent, it will be used to load book from firebase
        bookId = intent.getStringExtra("bookId")!!
        loaadBookDetails()

        //handle click go back
        binding.backToHome.setOnClickListener {
            onBackPressed()
        }



    }

    private fun loaadBookDetails() {
      Log.d(TAG, "Get pdf url from db")
        //database reference
        //get book url using book id
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get book url
                    val pdfUrl = snapshot.child("url").value
                    Log.d(TAG, "onDataChange: PDF_URL")

                    //Load pdf using url from firebase database
                    loadPdfFromUrl("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    private fun loadPdfFromUrl(pdfUrl: String) {
        Log.d(TAG, "loadBookFromUrl: Get pdf from firebase storage using its url")

        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constants.MAX_BTYES_PDF)
            .addOnSuccessListener {bytes->
                //load pdf
                binding.pdfView.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange { page, pageCount ->
                        //set current and total pages in toolbar subTitle
                        val currentPage = page+1
                        binding.toolbarSubtitleTV.text = "$currentPage/$pageCount"
                    }
                    .onError { t->
                        Log.d(TAG, "LoadBookFromUrl: ${t.message}")
                    }
                    .onPageError { page, t ->
                        Log.d(TAG, "LoadBookFromUrl: ${t.message}")
                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {e->
                Log.d(TAG, "LoadBookFromUrl: ${e.message}")
                binding.progressBar.visibility = View.GONE
            }

    }
}