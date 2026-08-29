package com.tommyarena.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class UrlShortenerActivity : AppCompatActivity() {

    private var lastShortUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_shortener)

        val input = findViewById<EditText>(R.id.longUrlInput)
        val result = findViewById<TextView>(R.id.shortUrlResult)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnShorten).setOnClickListener {
            val longUrl = input.text.toString().trim()
            if (longUrl.isEmpty()) return@setOnClickListener
            result.text = "Shortening..."

            Thread {
                try {
                    val encoded = URLEncoder.encode(longUrl, "UTF-8")
                    val url = URL("https://tinyurl.com/api-create.php?url=$encoded")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = reader.readText()
                    reader.close()
                    handler.post {
                        lastShortUrl = response
                        result.text = response
                    }
                } catch (e: Exception) {
                    handler.post { result.text = "Failed to shorten. Check your connection." }
                }
            }.start()
        }

        findViewById<Button>(R.id.btnCopyShort).setOnClickListener {
            if (lastShortUrl.isBlank()) return@setOnClickListener
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("short url", lastShortUrl))
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun BufferedReader.readText(): String {
    val sb = StringBuilder()
    var line: String?
    while (this.readLine().also { line = it } != null) sb.append(line)
    return sb.toString()
}
