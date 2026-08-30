package com.tommyarena.app

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class WebServerActivity : AppCompatActivity() {

    private var serverSocket: ServerSocket? = null
    private var serverThread: Thread? = null
    private var running = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_server)

        val statusText = findViewById<TextView>(R.id.serverStatusText)
        statusText.text = "Server stopped"

        findViewById<Button>(R.id.btnStartServer).setOnClickListener {
            if (running) return@setOnClickListener
            running = true
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ip = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
            runOnUiThread { statusText.text = "Running at http://$ip:8080\n(same Wi-Fi network only)" }

            serverThread = Thread {
                try {
                    serverSocket = ServerSocket(8080)
                    while (running) {
                        val client: Socket = serverSocket!!.accept()
                        handleClient(client)
                    }
                } catch (e: Exception) {
                    // socket closed on stop, expected
                }
            }
            serverThread?.start()
        }

        findViewById<Button>(R.id.btnStopServer).setOnClickListener {
            running = false
            serverSocket?.close()
            statusText.text = "Server stopped"
        }
    }

    private fun handleClient(client: Socket) {
        Thread {
            try {
                val output = PrintWriter(client.getOutputStream(), true)
                val body = "<html><body style='background:#0D0D0D;color:#fff;font-family:sans-serif;text-align:center;padding-top:50px'>" +
                        "<h1>Tommy Arena Server</h1><p>Your phone is serving this page over local Wi-Fi.</p></body></html>"
                output.print("HTTP/1.1 200 OK\r\n")
                output.print("Content-Type: text/html\r\n")
                output.print("Content-Length: ${body.length}\r\n")
                output.print("\r\n")
                output.print(body)
                output.flush()
                client.close()
            } catch (e: Exception) {
                // ignore
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        running = false
        serverSocket?.close()
    }
}
