package com.example.myapplication

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Classes.MyApplication
import com.example.myapplication.Models.ModelPdf
import com.example.myapplication.UserSide.FilterPdfUser
import com.example.myapplication.databinding.RowPdfUserBinding
import java.io.IOException
import java.util.UUID

class AdapterFetch(
    private val context: Context,
    private var pdfArrayList: ArrayList<ModelPdf>
) : RecyclerView.Adapter<AdapterFetch.HolderPdfUser>() {

    private var filterList: ArrayList<ModelPdf> = pdfArrayList
    private var isRobotMoving = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUser {
        val binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfUser(binding)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfUser, position: Int) {
        val model = pdfArrayList[position]

        holder.titleTv.text = model.title
        holder.descriptionTv.text = model.description

        MyApplication.loadPdfFromUrlSinglePage(model.url, model.title, holder.pdfView, holder.progressBar, null)
        MyApplication.loadCategory(model.categoryId, holder.categoryTv)
        MyApplication.loadPdfSize(model.url, model.title, holder.sizeTv)

        holder.itemView.setOnClickListener {
            val command = model.command // Assuming `command` is a String field in your `ModelPdf`

            if (BluetoothManager.isConnected()) {
                BluetoothManager.sendData(command)
                Toast.makeText(context, "Command sent: $command", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Not connected to robot!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class HolderPdfUser(binding: RowPdfUserBinding) : RecyclerView.ViewHolder(binding.root) {
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var categoryTv = binding.categoryTv
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv
    }

    // ... rest of your code
}
