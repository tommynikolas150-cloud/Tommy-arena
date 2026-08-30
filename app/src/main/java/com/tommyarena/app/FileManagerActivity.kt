package com.tommyarena.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class FileManagerActivity : AppCompatActivity() {

    private data class Entry(val name: String, val isDir: Boolean)
    private var allEntries = listOf<Entry>()
    private lateinit var adapter: ArrayAdapter<String>

    private val pickFolderLauncher = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            findViewById<TextView>(R.id.currentPathText).text = uri.path ?: uri.toString()
            loadFolder(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)

        val listView = findViewById<ListView>(R.id.fileList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        findViewById<Button>(R.id.btnPickFolder).setOnClickListener {
            pickFolderLauncher.launch(null)
        }

        val searchInput = findViewById<EditText>(R.id.searchInput)
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = applyFilter(s.toString())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun loadFolder(treeUri: Uri) {
        try {
            val docId = DocumentsContract.getTreeDocumentId(treeUri)
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, docId)
            val cursor = contentResolver.query(
                childrenUri, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE),
                null, null, null
            )
            val entries = mutableListOf<Entry>()
            cursor?.use {
                while (it.moveToNext()) {
                    val name = it.getString(0)
                    val mime = it.getString(1)
                    entries.add(Entry(name, mime == DocumentsContract.Document.MIME_TYPE_DIR))
                }
            }
            allEntries = entries.sortedBy { it.name.lowercase() }
            applyFilter(findViewById<EditText>(R.id.searchInput).text.toString())
        } catch (e: Exception) {
            Toast.makeText(this, "Couldn't read folder: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun applyFilter(query: String) {
        val filtered = if (query.isBlank()) allEntries else allEntries.filter { it.name.contains(query, ignoreCase = true) }
        adapter.clear()
        adapter.addAll(filtered.map { "${if (it.isDir) "📁" else "📄"} ${it.name}" })
        adapter.notifyDataSetChanged()
    }
}
