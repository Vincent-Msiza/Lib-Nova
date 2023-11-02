package com.example.myapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BooksUserViewModelFactory(
    private val categoryId: String,
    private val category: String,
    private val uid: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksUserViewModel::class.java)) {
            return BooksUserViewModel(categoryId, category, uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}