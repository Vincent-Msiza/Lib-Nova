package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.Adapters.AdapterPdfFavorite
import com.example.myapplication.Models.ModelPdf
import com.example.myapplication.databinding.ActivityFetchBooksBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.IOException

class FetchBooksActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityFetchBooksBinding
    //setup the bluetooth
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothSocket: BluetoothSocket
    private val TAG = "ControlBotActivity"

    // Define a list to store your books
    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterFetch

    //firebase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var bookArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfFavorite: AdapterPdfFavorite

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setup bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // Load all books
        loadAllBooks()
        //move back
        binding.backToHome.setOnClickListener {
            onBackPressed()
        }


    }
    //connecting to bluetooth class

    //fetch all the books man come on
    private fun loadAllBooks() {
        // Initialize the list
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear the list before adding data into it
                pdfArrayList.clear()
                for (ds in snapshot.children) {
                    // Get data
                    val model = ds.getValue(ModelPdf::class.java)
                    // Add to the list
                    pdfArrayList.add(model!!)
                }
                // Setup the adapter
                adapterPdfUser = AdapterFetch(this@FetchBooksActivity, pdfArrayList)
                // Set the adapter to your RecyclerView (assuming you have a RecyclerView in your activity's layout)
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
                Log.e("YourActivity", "DatabaseError: ${error.message}")
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (::bluetoothSocket.isInitialized) {
                // Close the Bluetooth socket to release resources if it's initialized
                bluetoothSocket.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error closing Bluetooth socket: ${e.message}")
        }
    }



}