package com.example.pdflibrary.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel


class ViewModel(private val context: Application) : AndroidViewModel(context) {

    fun getData() {
        context.assets
    }

}
