package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class SpeedTestActivity : AppCompatActivity() {

    // A ~5MB test file from a public speed-test host
    private val testFileUrl = "https://speed.hetzner.de/5MB.bin"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_test)

        val result = findViewById<TextView>(R.id.speedResultText)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnRunTest).setOnClickListener {
            result.text = "Testing..."

            Thread {
                try {
                    val url = URL(testFileUrl)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.connectTimeout = 10000
                    conn.readTimeout = 15000

                    val start = System.currentTimeMillis()
                    val input = conn.inputStream
                    val buffer = ByteArray(8192)
                    var totalBytes = 0L
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        totalBytes += read
                    }
                    input.close()
                    val elapsedSec = (System.currentTimeMillis() - start) / 1000.0

                    val mbps = if (elapsedSec > 0) (totalBytes * 8 / 1_000_000.0) / elapsedSec else 0.0

                    handler.post {
                        result.text = "%.2f Mbps".format(mbps)
                    }
                } catch (e: Exception) {
                    handler.post { result.text = "Test failed. Check connection." }
                }
            }.start()
        }
    }
}
