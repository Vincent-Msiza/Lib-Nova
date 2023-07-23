package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.RowPdfAdminBinding
import com.github.barteksc.pdfviewer.PDFView

class AdapterPdfAdmin :RecyclerView.Adapter<AdapterPdfAdmin.ViewHolder>, Filterable{
    //context
    private var context: Context
    //arraylist to hold pdfs
    public var pdfArrayList: ArrayList<ModelPdf>
    private val filterList: ArrayList<ModelPdf>

    //viewBinding
    private lateinit var binding:RowPdfAdminBinding

    //filter object
    private var filter: FilterPdfAdmin? = null

    //constructor
    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) : super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //bind/inflate layout row_pdf_admin
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)

        return  ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        get data, set data, handle click etc

        //get data
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp

        //convert timestamp to dd/mm/yyy format
        val formattedDate = MyApplication.formatTimeStamp(timestamp)

        //set data
        holder.titleTv.text = title
        holder.descriptionTv.text = description
//        holder.dateTv.text = formattedDate

        //load further details like category, pdf from url and pdf size

        //categoryID
        MyApplication.loadCategory(categoryId, holder.categoryTv)

        //we don't need page number so we pass null
        MyApplication.loadPdfFromUrlSinglePage(pdfUrl, title, holder.pdfView,holder.progressBar, null)

        //load pdf size
        MyApplication.loadPdfSize(pdfUrl, title, holder.sizeTv)

        //handle click show dialog with options edit book and delete book
        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }
        //handle item click to open pdfDetails activity
        holder.itemView.setOnClickListener {
            //intent with book id
            val intent = Intent(context, PdfDetailActivity::class.java)
            intent.putExtra("bookId", pdfId)
            context.startActivity(intent)
        }

    }

    private fun moreOptionsDialog(model: ModelPdf, holder: AdapterPdfAdmin.ViewHolder) {
    //get id, url title of the books
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title

        //options to show in dialog
        val options = arrayOf("Edit", "Delete")

        //alert dialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Option")
            .setItems(options){dialog, position->
                //handle item
                if (position==0){
                    //Edit is clicked
                    val intent = Intent(context, PdfEditActivity::class.java)
                    intent.putExtra("bookId", bookId)
                    context.startActivity(intent)
                }
                else if(position == 1){
                    //delete is clicked
                    MyApplication.deleteBooks(context, bookId, bookUrl, bookTitle)
                }

            }
            .show()

    }

    override fun getItemCount(): Int {
        return pdfArrayList.size //items count
    }



    override fun getFilter(): Filter {
        if (filter == null){
            filter = FilterPdfAdmin(filterList, this)
        }
        return filter as FilterPdfAdmin
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn

    }

}