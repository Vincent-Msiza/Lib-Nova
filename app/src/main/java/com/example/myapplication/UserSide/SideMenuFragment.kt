package com.example.myapplication.UserSide

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.UserAuth.Login


class SideMenuFragment : Fragment() {

    private lateinit var rootView: View
    private lateinit var homeTv: TextView
    private lateinit var favouritesTv: TextView
    private lateinit var profileTv: TextView
    private lateinit var logoutTv: TextView

    private lateinit var cancel : ImageButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_side_menu, container, false)


        return rootView
    }

    private fun removeFragmentFromContainer() {

    }

    private fun findView() {
        homeTv = rootView.findViewById(R.id.homeTv)
        favouritesTv = rootView.findViewById(R.id.favouritesTv)
        profileTv = rootView.findViewById(R.id.profileTv)
        logoutTv = rootView.findViewById(R.id.logoutTv)

    }
    private fun setListener() {
        homeTv.setOnClickListener {
           startActivity(Intent(requireContext(), MainActivity::class.java))
        }
        favouritesTv.setOnClickListener {

        }
        profileTv.setOnClickListener {

        }
        logoutTv.setOnClickListener {
            startActivity(Intent(requireContext(), Login::class.java))
        }


    }


}