package com.example.myapplication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    val arrayListLiveData = MutableLiveData<ArrayList<Item>>()

}