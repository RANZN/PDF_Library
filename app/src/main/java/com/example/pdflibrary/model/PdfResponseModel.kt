package com.example.pdflibrary.model


import com.google.gson.annotations.SerializedName

data class PdfResponseModel(
    @SerializedName("pdf_data")
    val pdfData: List<PdfData?>?
)