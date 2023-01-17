package com.example.pdflibrary

import com.example.pdflibrary.databinding.ProgressBinding
import com.example.pdflibrary.model.PdfData

interface OnItemClick {
    fun onClickOfItem(
        title: String, responseModelItem: PdfData, pos: Int, progressbar: ProgressBinding
    )
}