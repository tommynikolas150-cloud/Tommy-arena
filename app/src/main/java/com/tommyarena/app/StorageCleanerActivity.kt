package com.tommyarena.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StorageCleanerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage_cleaner)

        updateCacheSize()

        findViewById<Button>(R.id.btnClearAppCache).setOnClickListener {
            cacheDir.deleteRecursively()
            externalCacheDir?.deleteRecursively()
            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show()
            updateCacheSize()
        }

        findViewById<Button>(R.id.btnOpenStorageSettings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
        }
    }

    private fun updateCacheSize() {
        val size = (dirSize(cacheDir) + (externalCacheDir?.let { dirSize(it) } ?: 0)) / 1024
        findViewById<TextView>(R.id.cacheSizeText).text = "Tommy Arena cache: $size KB"
    }

    private fun dirSize(dir: java.io.File): Long {
        if (!dir.exists()) return 0
        var size = 0L
        dir.listFiles()?.forEach {
            size += if (it.isDirectory) dirSize(it) else it.length()
        }
        return size
    }
}
