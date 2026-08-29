package com.tommyarena.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DataAnalyzerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_analyzer)

        val input = findViewById<EditText>(R.id.numbersInput)
        val result = findViewById<TextView>(R.id.analysisResult)

        findViewById<Button>(R.id.btnAnalyze).setOnClickListener {
            val numbers = input.text.toString()
                .split(Regex("[,\\n\\s]+"))
                .mapNotNull { it.trim().toDoubleOrNull() }

            if (numbers.isEmpty()) {
                result.text = "Enter valid numbers first."
                return@setOnClickListener
            }

            val sum = numbers.sum()
            val avg = sum / numbers.size
            val min = numbers.min()
            val max = numbers.max()
            val sorted = numbers.sorted()
            val median = if (sorted.size % 2 == 0) {
                (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2
            } else sorted[sorted.size / 2]
            val variance = numbers.map { (it - avg) * (it - avg) }.sum() / numbers.size
            val stdDev = Math.sqrt(variance)

            result.text = """
                Count: ${numbers.size}
                Sum: %.2f
                Average: %.2f
                Median: %.2f
                Min: %.2f
                Max: %.2f
                Std Dev: %.2f
            """.trimIndent().format(sum, avg, median, min, max, stdDev)
        }
    }
}
