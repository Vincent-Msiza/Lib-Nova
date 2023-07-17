package com.example.myapplication

import android.app.Application
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.play.integrity.internal.t
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata
import java.sql.Timestamp
import java.util.Calendar
import java.util.Locale

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }

    companion object{

        fun formatTimeStamp(timestamp: Long) : String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = timestamp

            //format dd/mm/yyy
            return DateFormat.format("dd/MM/yyyy", cal).toString()
        }
        //function to get pdf size
        fun loadPdfSize(pdfUrl: String, pdfTitle: String, sizeTv:TextView){
            val TAG = "PDF_SIZE_TAG"
            //using url we can get file and its data from firestore
            val ref = FirebaseStorage.getInstance().getReference(pdfUrl)
            ref.metadata
                .addOnSuccessListener { storageMetadata ->
                    Log.d(TAG, "loadPdfSize: got metadata")
                    val bytes = storageMetadata.sizeBytes.toDouble()
                    Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                    //convert bytes to KB/MB
                    val kb = bytes/1024
                    val mb = kb/1024
                    if(mb>1){
                        sizeTv.text = "${String.format("$.2f", mb)} MB"
                    }
                    else if(kb>=1){
                        sizeTv.text = "${String.format("$.2f", kb)} KB"
                    }
                    else{
                        sizeTv.text = "${String.format("$.2f", bytes)} byte"
                    }
                }
                .addOnFailureListener{ e ->
                    //failed to get metadata
                    Log.d(TAG, "loadPdfSize: failed to get metadata due to ${e.message}")

                }
        }


      fun loadPdfFromUrlSinglePage(
          pdfUrl: String,
          pdfTitle: String,
          author: String,
          pdfView: PDFView,
          progressBar: PDFView,
          pagesTv: TextView?) {

          val TAG = "PDF_THUMBNAIL_TAG"
          //using url we can get file and its metadata from firebase

          val ref = FirebaseStorage.getInstance().getReference(pdfUrl)
          ref.getBytes(Constants.MAX_BTYES_PDF)
              .addOnSuccessListener { bytes ->
                  Log.d(TAG, "loadPdfSize: got metadata")

                  Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                  //set to pdfview
                  pdfView.fromBytes(bytes)
                      .pages(0)
                      .spacing(0)
                      .swipeHorizontal(false)
                      .enableSwipe(false)
                      .onError{t->
                          progressBar.visibility = View.INVISIBLE
                          Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")

                      }
                      .onPageError { page, t ->
                          progressBar.visibility = View.INVISIBLE
                          Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                      }
                      .onLoad { nbPages->
                          //
                          Log.d(TAG, "loadPdfFromUrlSinglePage: $nbPages")
                          progressBar.visibility = View.INVISIBLE

                          //if pagesTV param is not null then set page numbers
                          if(pagesTv != null){
                              pagesTv.text = "$nbPages"
                          }


                      }
                      .load()
              }
              .addOnFailureListener{ e ->
                  //failed to get metadata
                  Log.d(TAG, "loadPdfSize: failed to get metadata due to ${e.message}")

              }







      }
        fun loadCategory(categoryId: String, CategoryTv: TextView){
            //load category using category id from firebase
            val ref = FirebaseDatabase.getInstance().getReference("Category")
            ref.child(categoryId)
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        //get category
                        val category:String = "${snapshot.child("category").value}"

                        //set category
                        CategoryTv.text = category
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }
    }
}