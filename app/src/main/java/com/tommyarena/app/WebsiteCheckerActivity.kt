package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.HttpURLConnection
import java.net.URL

class WebsiteCheckerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_website_checker)

        val input = findViewById<EditText>(R.id.siteUrlInput)
        val result = findViewById<TextView>(R.id.siteStatusResult)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnCheckSite).setOnClickListener {
            var site = input.text.toString().trim()
            if (site.isEmpty()) return@setOnClickListener
            if (!site.startsWith("http")) site = "https://$site"
            result.text = "Checking..."

            Thread {
                try {
                    val start = System.currentTimeMillis()
                    val url = URL(site)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "HEAD"
                    conn.connectTimeout = 8000
                    conn.readTimeout = 8000
                    val code = conn.responseCode
                    val elapsed = System.currentTimeMillis() - start
                    handler.post {
                        result.text = if (code in 200..399) {
                            "✅ Online\nStatus: $code\nResponse time: ${elapsed}ms"
                        } else {
                            "⚠️ Reachable but returned status $code"
                        }
                    }
                } catch (e: Exception) {
                    handler.post { result.text = "❌ Site unreachable or offline" }
                }
            }.start()
        }
    }
}
