package com.example.pdflibrary.model


import com.google.gson.annotations.SerializedName

data class PdfData(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("pdf_link")
    val pdfLink: String?
)