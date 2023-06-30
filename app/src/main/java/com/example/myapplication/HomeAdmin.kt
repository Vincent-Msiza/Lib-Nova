package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    //for the image slider
    private lateinit var viewPager: ViewPager
    private lateinit var adapter: ImageSliderAdapter
    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 3000 // Delay in milliseconds before auto sliding
    private val PERIOD_MS: Long = 5000 // Interval in milliseconds for auto sliding


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

        //image slider viewpager
        viewPager = view.findViewById(R.id.viewPager)

        val slides = listOf(
            ImageSliderAdapter.Slide(R.drawable.sliderimageone, "Lib-Nova", "Discover, \n" +
                    "Read, and Connect"),
            ImageSliderAdapter.Slide(R.drawable.sliderimagetwo, "Lib-Nova", "Access a Vast \n" +
                    "Collection of Books"),
            ImageSliderAdapter.Slide(R.drawable.sliderimagethree, "Lib-Nova", "Easy Digital\n" +
                    "Borrowing and Reading")
        )

        adapter = ImageSliderAdapter(requireContext(), slides)
        viewPager.adapter = adapter

        //Auto sliding
        val handler = Handler()
        val update = Runnable {
            if (currentPage == slides.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)


    }






    }