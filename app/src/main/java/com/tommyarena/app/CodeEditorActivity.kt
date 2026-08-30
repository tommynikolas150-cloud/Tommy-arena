package com.tommyarena.app

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class CodeEditorActivity : AppCompatActivity() {
    private var currentUri: Uri? = null

    private val openLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            currentUri = uri
            try {
                val reader = BufferedReader(InputStreamReader(contentResolver.openInputStream(uri)))
                val text = reader.readText()
                reader.close()
                findViewById<EditText>(R.id.codeEditorInput).setText(text)
            } catch (e: Exception) {
                Toast.makeText(this, "Couldn't open file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_editor)

        findViewById<Button>(R.id.btnOpenFile).setOnClickListener {
            openLauncher.launch(arrayOf("text/*"))
        }

        findViewById<Button>(R.id.btnSaveFile).setOnClickListener {
            val uri = currentUri
            if (uri == null) {
                Toast.makeText(this, "Open a file first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            try {
                val writer = OutputStreamWriter(contentResolver.openOutputStream(uri, "wt"))
                writer.write(findViewById<EditText>(R.id.codeEditorInput).text.toString())
                writer.close()
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Save failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun BufferedReader.readText(): String {
    val sb = StringBuilder()
    var line: String?
    while (this.readLine().also { line = it } != null) sb.append(line).append("\n")
    return sb.toString()
}
