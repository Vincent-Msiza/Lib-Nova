package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout.AttachedBehavior
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.databinding.FragmentBooksUserBinding
import com.example.myapplication.databinding.FragmentLibraryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding

    // firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var categoryArrayList: ArrayList<ModelCategory>
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        setupWithViewPagerAdapter(binding.viewPager, requireContext())
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        return binding.root
    }

    private fun setupWithViewPagerAdapter(viewPager: ViewPager, context: Context) {
        val viewPagerAdapter = ViewPagerAdapter(childFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = viewPagerAdapter

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
    }

    inner class ViewPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
        private val fragmentsList: ArrayList<BooksUserFragment> = ArrayList()
        private val fragmentTitleList: ArrayList<String> = ArrayList()

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
