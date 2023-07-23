package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityMainBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()

        //get current user names from firebase
        val fireBaseUser = firebaseAuth.currentUser
        val ref = FirebaseDatabase.getInstance().getReference("users")
        //get user name
        ref.child(fireBaseUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.child("name").value
                    val greeting = "Hey, $username"
                    binding.nameTv.text = greeting
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

        binding.menuIb.setOnClickListener {
            val intent = Intent(this, MenuUsers::class.java)
            startActivity(intent)
        }

        setupWithViewPagerAdapter(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)


    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager) {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this)


        //init list
        categoryArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryArrayList.clear()

                val modelAll = ModelCategory("01", "All", 1, "")
                val modelMostViewed = ModelCategory("02", "Most Viewed", 2, "")
                val modelMostDownloaded = ModelCategory("03", "Most Downloaded", 3, "")

                categoryArrayList.add(modelAll)
                categoryArrayList.add(modelMostViewed)
                categoryArrayList.add(modelMostDownloaded)

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        modelAll.id,
                        modelAll.category,
                        modelAll.uid
                    ),
                    modelAll.category
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        modelMostViewed.id,
                        modelMostViewed.category,
                        modelMostViewed.uid
                    ),
                    modelMostViewed.category
                )

                viewPagerAdapter.addFragment(
                    BooksUserFragment.newInstance(
                        modelMostDownloaded.id,
                        modelMostDownloaded.category,
                        modelMostDownloaded.uid
                    ),
                    modelMostDownloaded.category
                )

                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    model?.let {
                        categoryArrayList.add(it)
                        viewPagerAdapter.addFragment(
                            BooksUserFragment.newInstance(
                                it.id,
                                it.category,
                                it.uid
                            ),
                            it.category
                        )
                    }
                }

                viewPagerAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancelled event
            }
        })

        //setup adapter
        viewPager.adapter = viewPagerAdapter
    }

    inner class ViewPagerAdapter(fm: FragmentManager, behavior: Int, context: Context) : FragmentPagerAdapter(fm, behavior) {
        private val fragmentsList: ArrayList<BooksUserFragment> = ArrayList()
        private val fragmentTitleList: ArrayList<String> = ArrayList()

        private val context: Context

        init {
            this.context = context
        }

        override fun getCount(): Int {
            return fragmentsList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentsList[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: BooksUserFragment, title: String) {
            fragmentsList.add(fragment)
            fragmentTitleList.add(title)
        }
    }
}