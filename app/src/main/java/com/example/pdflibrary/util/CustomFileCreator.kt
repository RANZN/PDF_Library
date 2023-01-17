package com.example.pdflibrary.util

import android.os.Environment
import java.io.File

class CustomFileCreator {
    companion object {
        fun getFile(title: String): File {
            return File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/pdfLibrary/$title"
            )
        }
    }
}

