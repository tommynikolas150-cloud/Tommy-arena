package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class EbookLibraryActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_books"
    private val key = "books_json"
    private data class Book(var title: String, var author: String, var read: Boolean)
    private val books = mutableListOf<Book>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ebook_library)
        load()
        val listView = findViewById<ListView>(R.id.bookList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            books[position].read = !books[position].read
            refresh()
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            books.removeAt(position); refresh(); true
        }
        val titleInput = findViewById<EditText>(R.id.bookTitleInput)
        val authorInput = findViewById<EditText>(R.id.bookAuthorInput)
        findViewById<Button>(R.id.btnAddBook).setOnClickListener {
            val title = titleInput.text.toString().trim()
            if (title.isEmpty()) return@setOnClickListener
            books.add(Book(title, authorInput.text.toString().trim(), false))
            titleInput.text.clear(); authorInput.text.clear()
            refresh()
        }
    }

    private fun displayList() = books.map { "${if (it.read) "✅" else "📖"} ${it.title}${if (it.author.isNotBlank()) " — ${it.author}" else ""}" }
    private fun refresh() { adapter.clear(); adapter.addAll(displayList()); adapter.notifyDataSetChanged(); save() }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            books.add(Book(o.getString("title"), o.getString("author"), o.getBoolean("read")))
        }
    }
    private fun save() {
        val arr = JSONArray()
        for (b in books) { val o = JSONObject(); o.put("title", b.title); o.put("author", b.author); o.put("read", b.read); arr.put(o) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
