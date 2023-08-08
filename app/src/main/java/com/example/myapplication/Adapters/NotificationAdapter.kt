package com.example.myapplication.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.Classes.PushNotification
import com.example.myapplication.R

class NotificationAdapter(context: Context, notifications: List<PushNotification>) :
    ArrayAdapter<PushNotification>(context, 0, notifications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val notification = getItem(position)

        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.notification_item, parent, false
        )

        // Bind the notification data to the UI elements in the notification item layout
        val itemImageView = view.findViewById<ImageView>(R.id.ivIcon)
        val heading = view.findViewById<TextView>(R.id.title)
        val time = view.findViewById<TextView>(R.id.message)

        heading.text = notification?.title
        time.text = notification?.body

        return view
    }
}
