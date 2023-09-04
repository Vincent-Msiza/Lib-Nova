package com.example.myapplication.UserSide.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.example.myapplication.Adapters.AdapterPdfUser
import com.example.myapplication.Models.ModelPdf
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBooksUserBinding
import com.example.myapplication.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment() : Fragment() {

    //view binding fragment_books_user.xml => FragmentBooksUserBinding
    private lateinit var binding: FragmentSearchBinding
    //object
    companion object {
        const val TAG = "SEARCH_TAG"

        fun newInstance(categoryId: String, category: String, uid: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args

            return fragment
        }
    }


    private var categoryId =""
    private var category = ""
    private var uid = ""

    private lateinit var pdfArrayList: ArrayList<ModelPdf>
    private lateinit var adapterPdfUser: AdapterPdfUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //get arguments that we passed in newInstance method
        val args = arguments
        if (args != null){
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =  FragmentSearchBinding.inflate(LayoutInflater.from(context), container, false)

        //load every book from the database
        pdfArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear list before starting adding data into it
//                pdfArrayList.clear()
                for (ds in snapshot.children){
                    //get data
                    val model = ds.getValue(ModelPdf::class.java)
                    //add tp list
                    pdfArrayList.add(model!!)
                }
                //setup adapter
                adapterPdfUser = AdapterPdfUser(context!!, pdfArrayList)
                //set adapter to recycleView
                binding.booksRv.adapter = adapterPdfUser
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        //search
        binding.searchEt.addTextChangedListener { object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    adapterPdfUser.filter!!.filter(s)
                }
                catch (e: Exception){
                    Log.d(TAG, "onTextChanged: SEARCH EXCEPTION: ${e.message}")
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        }
        }
        return binding.root
    }
    //classes end here
}