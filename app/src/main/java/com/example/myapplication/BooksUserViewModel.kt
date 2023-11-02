package com.example.myapplication

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Models.ModelPdf
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class BooksUserViewModel(
    val categoryId: String,
    val category: String,
    val uid: String
) : ViewModel() {

    private val _pdfList = MutableLiveData<List<ModelPdf>>()
    val pdfList: LiveData<List<ModelPdf>> = _pdfList

    fun loadAllBooks() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val ref = FirebaseDatabase.getInstance().getReference("Books")
            try {
                val pdfList = fetchBooks(ref)  // Here ref is a DatabaseReference, which is allowed
                _pdfList.postValue(pdfList)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadMostDownloadedBooks(orderBy: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val query = FirebaseDatabase.getInstance().getReference("Books")
                .orderByChild(orderBy).limitToLast(10)  // Here query is a Query
            try {
                val pdfList = fetchBooks(query)  // Passing Query is allowed
                _pdfList.postValue(pdfList)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun loadCategoryBooks() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val query = FirebaseDatabase.getInstance().getReference("Books")
                .orderByChild("categoryId").equalTo(categoryId)  // Here query is a Query
            try {
                val pdfList = fetchBooks(query)  // Passing Query is allowed
                _pdfList.postValue(pdfList)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private suspend fun fetchBooks(query: Query): List<ModelPdf> =
        suspendCancellableCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfList = mutableListOf<ModelPdf>()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        model?.let { pdfList.add(it) }
                    }
                    continuation.resume(pdfList)
                }

                @SuppressLint("RestrictedApi")
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(DatabaseException(error.message))
                }
            })
        }

}
