package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.widget.ImageView
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class Notifications : AppCompatActivity() {

    private val itemList = listOf(
        Item(R.drawable.notificon, "Hi Kelly welcome to lib-nova hope you gonna enjoy using  the app."
                , "5 min ago", true)
    )

    private lateinit var backtohome: ImageView
    private lateinit var mainViewModel: MainViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        backtohome = findViewById(R.id.backToHome)

        //moving back to the home screen
        backtohome.setOnClickListener{
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    //notification display list view
        val listView: ListView = findViewById(R.id.listview)
        val adapter = ItemAdapter(itemList)
        listView.adapter = adapter

        //pass data to homeFragment
        val fragment = HomeFragment()
        val args = Bundle()
        args.putParcelableArrayList("key", itemList)
        fragment.arguments = args




    }

}

private fun Parcelable.putParcelableArrayList(s: String, itemList: List<Item>) {

}
