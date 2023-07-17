package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.barteksc.pdfviewer.PDFView

class AdapterPdfAdmin( private val context: Context, public var pdfArrayList: ArrayList<ModelPdf>
) : RecyclerView.Adapter<AdapterPdfAdmin.ViewHolder>(), Filterable {
    //constructor

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_pdf_admin, parent, false)

        return ViewHolder(itemView)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val author = model.author
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //convert timestamp to dd/mm/yyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)
        //lets create an application class that will contain the functions that will be used multiple times

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate
        holder.authorTv.text = author

        //load further details like category, pdf from url and pdf size

        //categoryID
        MyApplication.loadCategory(categoryId, holder.categoryTv)

            //we don't need page number so we pass null
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl,title,author, holder.pdfView, holder.progressBar, pagesTv = null)

        //load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)

    }

    override fun getItemCount(): Int {
        return pdfArrayList.size //items count

    }


    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfView = itemView.findViewById<PDFView>(R.id.pdfView)!!
        val progressBar = itemView.findViewById<PDFView>(R.id.progressBar)!!
        val titleTv = itemView.findViewById<TextView>(R.id.titleTv)!!
        val descriptionTv = itemView.findViewById<TextView>(R.id.descriptionTv)!!
        val categoryTv = itemView.findViewById<TextView>(R.id.categoryTv)!!
        val authorTv = itemView.findViewById<TextView>(R.id.authorTv)!!
        val sizeTv = itemView.findViewById<TextView>(R.id.sizeTv)!!
        val dateTv = itemView.findViewById<TextView>(R.id.dateTv)!!
        val moreBtn = itemView.findViewById<ImageButton>(R.id.moreBtn)!!
    }


}