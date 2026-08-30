package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class AiAssistantActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_ai"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ai_assistant)

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val keyInput = findViewById<EditText>(R.id.apiKeyInput)
        keyInput.setText(prefs.getString("api_key", ""))

        val promptInput = findViewById<EditText>(R.id.aiPromptInput)
        val responseText = findViewById<TextView>(R.id.aiResponseText)
        val handler = Handler(Looper.getMainLooper())

        findViewById<Button>(R.id.btnAskAi).setOnClickListener {
            val key = keyInput.text.toString().trim()
            val prompt = promptInput.text.toString().trim()
            if (key.isEmpty() || prompt.isEmpty()) {
                responseText.text = "Enter an API key and a question."
                return@setOnClickListener
            }
            prefs.edit().putString("api_key", key).apply()
            responseText.text = "Thinking..."

            Thread {
                try {
                    val url = URL("https://api.groq.com/openai/v1/chat/completions")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Authorization", "Bearer $key")
                    conn.setRequestProperty("Content-Type", "application/json")
                    conn.doOutput = true
                    conn.connectTimeout = 15000
                    conn.readTimeout = 20000

                    val body = JSONObject()
                    body.put("model", "openai/gpt-oss-120b")
                    val messages = JSONArray()
                    val msg = JSONObject()
                    msg.put("role", "user")
                    msg.put("content", prompt)
                    messages.put(msg)
                    body.put("messages", messages)

                    val writer = OutputStreamWriter(conn.outputStream)
                    writer.write(body.toString())
                    writer.flush()
                    writer.close()

                    val code = conn.responseCode
                    val stream = if (code in 200..299) conn.inputStream else conn.errorStream
                    val reader = BufferedReader(InputStreamReader(stream))
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) sb.append(line)
                    reader.close()

                    if (code in 200..299) {
                        val json = JSONObject(sb.toString())
                        val content = json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                        handler.post { responseText.text = content }
                    } else {
                        handler.post { responseText.text = "Error ($code): ${sb}" }
                    }
                } catch (e: Exception) {
                    handler.post { responseText.text = "Failed: ${e.message}" }
                }
            }.start()
        }
    }
}
