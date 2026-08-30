package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.InetSocketAddress
import java.net.Socket

class PortScannerActivity : AppCompatActivity() {
    private val commonPorts = listOf(21, 22, 23, 25, 53, 80, 110, 143, 443, 3306, 3389, 8080)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_port_scanner)

        val input = findViewById<EditText>(R.id.scanHostInput)
        val result = findViewById<TextView>(R.id.portScanResult)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnScanPorts).setOnClickListener {
            val host = input.text.toString().trim()
            if (host.isEmpty()) return@setOnClickListener
            result.text = "Scanning..."
            Thread {
                val sb = StringBuilder()
                for (port in commonPorts) {
                    try {
                        val socket = Socket()
                        socket.connect(InetSocketAddress(host, port), 800)
                        sb.append("Port $port: OPEN\n")
                        socket.close()
                    } catch (e: Exception) {
                        sb.append("Port $port: closed\n")
                    }
                }
                handler.post { result.text = sb.toString() }
            }.start()
        }
    }
}
