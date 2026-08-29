package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiTesterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_tester)

        val methodSpinner = findViewById<Spinner>(R.id.methodSpinner)
        methodSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("GET", "POST", "PUT", "DELETE"))

        val urlInput = findViewById<EditText>(R.id.apiUrlInput)
        val bodyInput = findViewById<EditText>(R.id.apiBodyInput)
        val responseText = findViewById<TextView>(R.id.apiResponseText)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnSendRequest).setOnClickListener {
            val urlStr = urlInput.text.toString().trim()
            if (urlStr.isEmpty()) return@setOnClickListener
            val method = methodSpinner.selectedItem as String
            val body = bodyInput.text.toString()
            responseText.text = "Sending..."

            Thread {
                try {
                    val url = URL(urlStr)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = method
                    conn.connectTimeout = 10000
                    conn.readTimeout = 10000
                    if (method == "POST" || method == "PUT") {
                        conn.doOutput = true
                        conn.setRequestProperty("Content-Type", "application/json")
                        val writer = OutputStreamWriter(conn.outputStream)
                        writer.write(body)
                        writer.flush()
                        writer.close()
                    }
                    val code = conn.responseCode
                    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                    val reader = BufferedReader(InputStreamReader(stream))
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) sb.append(line).append("\n")
                    reader.close()
                    handler.post { responseText.text = "Status: $code\n\n${sb}" }
                } catch (e: Exception) {
                    handler.post { responseText.text = "Error: ${e.message}" }
                }
            }.start()
        }
    }
}
