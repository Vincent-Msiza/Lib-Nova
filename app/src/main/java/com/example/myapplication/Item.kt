package com.example.myapplication

 data class Item(val imageResId: Int, val title: String, val description: String, val isNotification: Boolean, )

private val itemList = listOf(
 Item(R.drawable.notificon, "Hi Kelly welcome to lib-nova hope you gonna enjoy using  the app."
  , "5 min ago", true)
)

