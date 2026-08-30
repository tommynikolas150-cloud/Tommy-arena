package com.tommyarena.app

import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoManagerActivity : AppCompatActivity() {
    private val uris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_manager)

        val videoView = findViewById<VideoView>(R.id.videoPlayer)
        videoView.setMediaController(MediaController(this))

        val listView = findViewById<ListView>(R.id.videoList)
        val names = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter

        findViewById<Button>(R.id.btnLoadVideos).setOnClickListener {
            names.clear(); uris.clear()
            val projection = arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME)
            val cursor = contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getLong(0)
                    names.add(it.getString(1) ?: "Unknown")
                    uris.add(ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id))
                }
            }
            adapter.notifyDataSetChanged()
            if (names.isEmpty()) Toast.makeText(this, "No videos found", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            videoView.setVideoURI(uris[position])
            videoView.start()
        }
    }
}
