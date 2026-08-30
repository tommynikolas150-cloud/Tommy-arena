package com.tommyarena.app

import android.content.ContentUris
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MusicManagerActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private val uris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_manager)

        val listView = findViewById<ListView>(R.id.musicList)
        val names = mutableListOf<String>()
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
        listView.adapter = adapter

        findViewById<Button>(R.id.btnLoadMusic).setOnClickListener {
            names.clear(); uris.clear()
            val projection = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST)
            val cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, "${MediaStore.Audio.Media.IS_MUSIC} != 0", null, null)
            cursor?.use {
                while (it.moveToNext()) {
                    val id = it.getLong(0)
                    val title = it.getString(1) ?: "Unknown"
                    val artist = it.getString(2) ?: "Unknown"
                    names.add("$title — $artist")
                    uris.add(ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id))
                }
            }
            adapter.notifyDataSetChanged()
            if (names.isEmpty()) Toast.makeText(this, "No music found", Toast.LENGTH_SHORT).show()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer.create(this, uris[position])
                mediaPlayer?.start()
            } catch (e: Exception) {
                Toast.makeText(this, "Playback failed", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnStopMusic).setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
