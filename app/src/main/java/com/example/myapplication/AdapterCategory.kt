package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory(private val context: Context, var categoryArrayList: ArrayList<ModelCategory>,
                      private var filterList: ArrayList<ModelCategory> = ArrayList(categoryArrayList),
                      private var filter: FilterCategory? = null
                      ) : RecyclerView.Adapter<AdapterCategory.ViewHolder>(), Filterable {
    //constructor

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.new_category, parent, false)

                return ViewHolder(itemView)
            }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        //set data
        holder.categoryTv.text = category

        //handle click category
        holder.deleteBtn.setOnClickListener {
            // Handle image button click
            //confirm before deleting
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this category")
                .setPositiveButton("Confirm"){ _, _ ->
                   Toast.makeText(context, "Deleting...", Toast.LENGTH_SHORT).show()

                    deleteCategory(model, holder)
                }
                .setNegativeButton("cancel"){ a, _ ->
                    a.dismiss()
                }
                .show()
        }

        //handle click start pdf list admin activity also pass pdf id and tittle
        holder.itemView.setOnClickListener {
            val intent = Intent(context, BooksAdmin::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }



    }

    private fun deleteCategory(model: ModelCategory, holder: ViewHolder) {
       //get id of category to delete
        val id = model.id
        //firebase DB > categories > categoryId
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id)
            .removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e ->
                Toast.makeText(context, "Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size //number of items in list
    }


            // Other methods...

            inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                val categoryTv: TextView = itemView.findViewById(R.id.categoryTv)
                val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
            }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterCategory(filterList, this)
        }
        return filter as FilterCategory
    }
}

