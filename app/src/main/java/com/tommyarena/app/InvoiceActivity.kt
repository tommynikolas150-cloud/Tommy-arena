package com.tommyarena.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InvoiceActivity : AppCompatActivity() {

    private var currentInvoiceText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invoice)

        val clientInput = findViewById<EditText>(R.id.clientName)
        val itemsInput = findViewById<EditText>(R.id.itemsInput)
        val preview = findViewById<TextView>(R.id.invoicePreview)

        findViewById<Button>(R.id.btnGenerate).setOnClickListener {
            val client = clientInput.text.toString().trim().ifEmpty { "Client" }
            val lines = itemsInput.text.toString().split("\n").filter { it.isNotBlank() }

            var total = 0.0
            val sb = StringBuilder()
            sb.append("INVOICE\n")
            sb.append("Date: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}\n")
            sb.append("Bill To: $client\n")
            sb.append("----------------------------\n")

            for (line in lines) {
                val parts = line.split("-")
                if (parts.size >= 2) {
                    val desc = parts.dropLast(1).joinToString("-").trim()
                    val amount = parts.last().trim().toDoubleOrNull() ?: 0.0
                    total += amount
                    sb.append("%-20s %8.2f\n".format(desc, amount))
                } else {
                    sb.append("$line\n")
                }
            }
            sb.append("----------------------------\n")
            sb.append("%-20s %8.2f\n".format("TOTAL", total))

            currentInvoiceText = sb.toString()
            preview.text = currentInvoiceText
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
            if (currentInvoiceText.isBlank()) return@setOnClickListener
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, currentInvoiceText)
            startActivity(Intent.createChooser(intent, "Share Invoice"))
        }
    }
}
