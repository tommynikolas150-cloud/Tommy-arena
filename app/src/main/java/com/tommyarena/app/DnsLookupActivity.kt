package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.InetAddress

class DnsLookupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dns_lookup)

        val input = findViewById<EditText>(R.id.hostnameInput)
        val result = findViewById<TextView>(R.id.dnsResultText)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnLookup).setOnClickListener {
            val host = input.text.toString().trim()
            if (host.isEmpty()) return@setOnClickListener
            result.text = "Looking up..."
            Thread {
                try {
                    val addresses = InetAddress.getAllByName(host)
                    val text = addresses.joinToString("\n") { it.hostAddress ?: "" }
                    handler.post { result.text = text }
                } catch (e: Exception) {
                    handler.post { result.text = "Lookup failed: ${e.message}" }
                }
            }.start()
        }
    }
}
