package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ItemAdapter(private val itemList: List<Item>) : BaseAdapter() {
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.notification_item, parent, false)

        val itemImageView = view.findViewById<ImageView>(R.id.ivIcon)
        val heading = view.findViewById<TextView>(R.id.title)
        val time = view.findViewById<TextView>(R.id.message)
        val item = itemList[position]

        itemImageView.setImageResource(item.imageResId)
        heading.text = item.title
        time.text = item.description
        return view
    }
}
