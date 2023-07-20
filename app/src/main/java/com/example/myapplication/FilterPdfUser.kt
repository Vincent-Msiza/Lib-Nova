package com.example.myapplication

import android.widget.Filter

class FilterPdfUser: Filter {
    //arraylist in which we want to search
    var filterList: ArrayList<ModelPdf>
    //adapter in which filter need to be implimented
    var adapterPdfUser: AdapterPdfUser

    //constructor
    constructor(filterList: ArrayList<ModelPdf>, adapterPdfUser: AdapterPdfUser) {
        this.filterList = filterList
        this.adapterPdfUser = adapterPdfUser
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        //value to search
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        //value to be searched should not be null and not empty
        //value should not be null and not empty
        if (constraint != null && constraint.length>0){
            //change to upper case, or lower case to avoid case sensitivity
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelPdf>()
            for(i in filterList.indices){
                //validate if match
                if(filterList[i].title.lowercase().contains(constraint)){
                    //searched value is similar to value in list, add to filtered list
                    filteredModels.add(filterList[i])
                }
            }
            //return filtered list and size
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //searched value is either null or empty, return all data
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        //apply filter changes
        adapterPdfUser.pdfArrayList = results.values as ArrayList<ModelPdf>

        //notify changes
        adapterPdfUser.notifyDataSetChanged()
    }


}