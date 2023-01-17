package com.example.pdflibrary

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pdflibrary.databinding.ActivityMainBinding
import com.example.pdflibrary.databinding.ProgressBinding
import com.example.pdflibrary.model.PdfData
import com.example.pdflibrary.model.PdfResponseModel
import com.example.pdflibrary.util.CustomFileCreator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.lang.reflect.Type


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnItemClick {
    private lateinit var binding: ActivityMainBinding
    private lateinit var pdfAdapter: PdfAdapter
    private lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        binding.toolbar.txtCenter.text = getString(R.string.pdf_library)
    }

    override fun onStart() {
        super.onStart()
        fetchDataFromAssets()
        setUpRecyclerView()
    }

    private fun fetchDataFromAssets() {
        try {
            val inputStream: InputStream = assets.open("ResponseModel.json")

            var data: Int = inputStream.read()
            val stringBuffer = StringBuffer()
            while (data != -1) {
                val ch = data.toChar()
                stringBuffer.append(ch)
                data = inputStream.read()
            }
            buildDataFromJson(stringBuffer.toString())
        } catch (e: Exception) {
            Log.d("TAG", e.message.toString())
        }
    }

    private fun buildDataFromJson(json: String) {
        val gson = Gson()
        val type: Type = object : TypeToken<PdfResponseModel?>() {}.type
        val pdfResponseModel: PdfResponseModel = gson.fromJson(json, type)
        pdfAdapter.differ.submitList(pdfResponseModel.pdfData)
    }

    private fun setUpRecyclerView() {
        pdfAdapter = PdfAdapter(this)
        binding.rvCategory.apply {
            adapter = pdfAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    override fun onClickOfItem(
        title: String, responseModelItem: PdfData, pos: Int, progressBar: ProgressBinding
    ) {
        val file = CustomFileCreator.getFile(title)
        if (file.exists()) {
            openFile(file)
        } else {
            downloadPdf(responseModelItem.pdfLink, title, pos, progressBar)
        }
    }

    private fun downloadPdf(url: String?, title: String, pos: Int, progressBar: ProgressBinding) {
        Toast.makeText(this, "start Downloading..", Toast.LENGTH_SHORT).show()
        progressBar.root.visibility = View.VISIBLE
        try {
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOCUMENTS, "/pdfLibrary/$title"
            )
            val downloadId = downloadManager.enqueue(request)

            val query = DownloadManager.Query().setFilterById(downloadId)

            GlobalScope.launch {
                while (true) {
                    val cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    val status = cursor.intValue(DownloadManager.COLUMN_STATUS)
                    val bytesDownloaded =
                        cursor.intValue(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                    val bytesTotal = cursor.intValue(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                    Log.d("ranjan", "downloadPdf: $bytesDownloaded  $bytesTotal")
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            withContext(Dispatchers.Main) {
                                progressBar.root.visibility = View.GONE
                                pdfAdapter.notifyItemChanged(pos)
                                Toast.makeText(this@MainActivity, "Downloaded", Toast.LENGTH_SHORT)
                                    .show()
                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                request.setTitle(title)
                            }
                            cursor.close()
                            return@launch
                        }
                        DownloadManager.STATUS_FAILED -> {
                            withContext(Dispatchers.Main) {
                                progressBar.root.visibility = View.GONE
                                Toast.makeText(
                                    this@MainActivity, "Download Failed", Toast.LENGTH_SHORT
                                ).show()
                            }
                            cursor.close()
                            return@launch
                        }
                        DownloadManager.STATUS_PAUSED -> {
                            withContext(Dispatchers.Main) {
                                progressBar.root.visibility = View.GONE
                            }
                            cursor.close()
                            return@launch
                        }
                        DownloadManager.STATUS_RUNNING -> {

                        }
                        else -> {

                        }
                    }
                    cursor.close()
                }
            }
        } catch (e: Exception) {

        }
    }

    private fun openFile(file: File) {
        val uri = FileProvider.getUriForFile(
            this, applicationContext.packageName + ".provider", file
        )
        val intent = Intent(this, PdfViewerActivity::class.java)
        intent.putExtra("fileUri", uri)
        startActivity(intent)
    }

    fun Cursor.column(which: String) = this.getColumnIndex(which)
    fun Cursor.intValue(which: String): Int = this.getInt(column(which))
    fun Cursor.floatValue(which: String): Float = this.getFloat(column(which))
    fun Cursor.stringValue(which: String): String = this.getString(column(which))
    fun Cursor.doubleValue(which: String): Double = this.getDouble(column(which))


}