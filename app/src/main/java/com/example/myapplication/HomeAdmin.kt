package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Timer
import java.util.TimerTask

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeAdmin : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth

    lateinit var nameTextView: TextView

    lateinit var next : ImageView

    //for the dashboard
    lateinit var addbooks: FrameLayout
    lateinit var addCategories: FrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // initialising Firebase auth object
        auth = FirebaseAuth.getInstance()
        nameTextView = view.findViewById(R.id.name)
        next = view.findViewById(R.id.viewNotif)
        addCategories = view.findViewById(R.id.tile1)
        addbooks = view.findViewById(R.id.tile2)

        val firebaseuser = auth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users")

        ref.child(firebaseuser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").value
                    val greeting = "Hey, $username"
                    nameTextView.text = greeting
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

        //moving to the notifications
        next.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsAdmin::class.java)
            startActivity(intent)
        }
        //for adding books and categories
        addCategories.setOnClickListener{
            val intent = Intent(requireContext(), AddCategories::class.java)
            startActivity(intent)
        }
        addbooks.setOnClickListener{
            val intent = Intent(requireContext(), AddBooks::class.java)
            startActivity(intent)
        }



    }
    }