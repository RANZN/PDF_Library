package com.example.pdflibrary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pdflibrary.databinding.PdfLayoutBinding
import com.example.pdflibrary.model.PdfData
import com.example.pdflibrary.util.CustomFileCreator

class PdfAdapter(var onItemClick: OnItemClick) : RecyclerView.Adapter<PdfAdapter.PdfViewHolder>() {
    private lateinit var binding: PdfLayoutBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = PdfLayoutBinding.inflate(layoutInflater, parent, false)
        return PdfViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val data = differ.currentList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    inner class PdfViewHolder(
        private val binding: PdfLayoutBinding, var onItemClick: OnItemClick
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: PdfData) {
            val extension = data.pdfLink?.let { it.substring(it.lastIndexOf(".")) }
            val title = data.name + extension
            val file = CustomFileCreator.getFile(title)
            if (file.exists()) binding.btnDownload.text = "OPEN"
            binding.apply {
                pdfImage.load(data.image)
                fileName.text = data.name
                btnDownload.setOnClickListener {
                    onItemClick.onClickOfItem(title, data, adapterPosition, binding.progressBar)
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<PdfData>() {
        override fun areItemsTheSame(
            oldItem: PdfData, newItem: PdfData
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PdfData, newItem: PdfData
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

}