package com.example.myapplication

data class User(
    var name: String? = null,
    var surname: String? = null,
    var phone: String? = null,
    val usertype: String? ="user"
)
