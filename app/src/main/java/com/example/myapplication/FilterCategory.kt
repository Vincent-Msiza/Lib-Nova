package com.example.myapplication

import android.annotation.SuppressLint
import java.util.logging.Filter
import java.util.logging.LogRecord

class FilterCategory: android.widget.Filter {

    //arraylist in which we want to search
    private var filterList: ArrayList<ModelCategory>

    //adapter in which filter need to be implemented
    private var adapterCategory: AdapterCategory

    //constructor
    constructor(filterList: ArrayList<ModelCategory>, adapterCategory: AdapterCategory) {
        this.filterList = filterList
        this.adapterCategory = adapterCategory
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()


        //value should not be null and not empty
        if(constraint != null && constraint.isNotEmpty()){
            //searched value is not null or empty

            //change to upper case or lower case to avoid case sensitivity
            constraint = constraint.toString().uppercase()
            val filteredModels:ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until  filterList.size){
                //validate

                if (filterList[i].category.uppercase().contains(constraint)){

                    //add to the filtered list
                    filteredModels.add(filterList[i])

                }

            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value is either null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results //don't miss it

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun publishResults(contraint: CharSequence?, results: FilterResults?) {
       //apply filter changes
        adapterCategory.categoryArrayList = results?.values as ArrayList<ModelCategory>

        //notify changes
        adapterCategory.notifyDataSetChanged()


    }


}