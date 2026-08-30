package com.tommyarena.app

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DownloadManagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_manager)

        val urlInput = findViewById<EditText>(R.id.downloadUrlInput)
        val nameInput = findViewById<EditText>(R.id.downloadNameInput)

        findViewById<Button>(R.id.btnStartDownload).setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener
            try {
                val fileName = nameInput.text.toString().trim().ifEmpty { url.substringAfterLast("/") }
                val request = DownloadManager.Request(Uri.parse(url))
                request.setTitle(fileName)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "Download started", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
