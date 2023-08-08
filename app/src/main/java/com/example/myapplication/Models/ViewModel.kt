package com.example.myapplication.Models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.Item

class MainViewModel: ViewModel() {
    val arrayListLiveData = MutableLiveData<ArrayList<Item>>()

}