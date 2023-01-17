package com.example.pdflibrary

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.pdflibrary.databinding.ActivityPdfViewerBinding

class PdfViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pdf_viewer)
        val file : Uri= intent.extras?.get("fileUri") as Uri
        binding.pdfView.fromUri(file).load()

    }
}