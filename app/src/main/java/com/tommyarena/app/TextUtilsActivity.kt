package com.tommyarena.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class TextUtilsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_utils)

        val input = findViewById<EditText>(R.id.textInput)
        val stats = findViewById<TextView>(R.id.statsText)
        val result = findViewById<TextView>(R.id.resultText)

        fun updateStats() {
            val text = input.text.toString()
            val chars = text.length
            val words = if (text.isBlank()) 0 else text.trim().split(Regex("\\s+")).size
            val sentences = text.split(Regex("[.!?]+")).count { it.isNotBlank() }
            stats.text = "Characters: $chars   Words: $words   Sentences: $sentences"
        }

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = updateStats()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        updateStats()

        findViewById<Button>(R.id.btnUpper).setOnClickListener {
            result.text = input.text.toString().uppercase(Locale.getDefault())
        }
        findViewById<Button>(R.id.btnLower).setOnClickListener {
            result.text = input.text.toString().lowercase(Locale.getDefault())
        }
        findViewById<Button>(R.id.btnTitle).setOnClickListener {
            result.text = input.text.toString().split(" ").joinToString(" ") { word ->
                if (word.isEmpty()) word else word[0].uppercase() + word.substring(1).lowercase()
            }
        }
        findViewById<Button>(R.id.btnReverse).setOnClickListener {
            result.text = input.text.toString().reversed()
        }
    }
}
