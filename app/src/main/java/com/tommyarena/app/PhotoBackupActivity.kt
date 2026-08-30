package com.tommyarena.app

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PhotoBackupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_backup)

        findViewById<Button>(R.id.btnRefreshPhotos).setOnClickListener { refresh() }
        findViewById<Button>(R.id.btnOpenGallery).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, MediaStore.Images.Media.EXTERNAL_CONTENT_URI))
        }
        refresh()
    }

    private fun refresh() {
        val projection = arrayOf(MediaStore.Images.Media.SIZE)
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        var count = 0
        var totalBytes = 0L
        cursor?.use {
            count = it.count
            while (it.moveToNext()) totalBytes += it.getLong(0)
        }
        val mb = totalBytes / (1024 * 1024)
        findViewById<TextView>(R.id.photoStatsText).text = "$count photos\n${mb} MB total"
    }
}
