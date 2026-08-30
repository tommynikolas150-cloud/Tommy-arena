package com.tommyarena.app

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class KnowledgeBaseActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_kb"
    private val key = "kb_json"
    private data class Entry(var title: String, var content: String)
    private val entries = mutableListOf<Entry>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge_base)
        load()

        val listView = findViewById<ListView>(R.id.kbList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter
        listView.setOnItemLongClickListener { _, _, position, _ -> entries.removeAt(position); refresh(); true }

        findViewById<EditText>(R.id.kbSearchInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = filterList(s.toString())
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val titleInput = findViewById<EditText>(R.id.kbTitleInput)
        val contentInput = findViewById<EditText>(R.id.kbContentInput)
        findViewById<Button>(R.id.btnAddKb).setOnClickListener {
            val title = titleInput.text.toString().trim()
            if (title.isEmpty()) return@setOnClickListener
            entries.add(0, Entry(title, contentInput.text.toString().trim()))
            titleInput.text.clear(); contentInput.text.clear()
            refresh()
        }
    }

    private fun displayList() = entries.map { "${it.title}\n${it.content}" }
    private fun filterList(query: String) {
        val filtered = if (query.isBlank()) entries else entries.filter { it.title.contains(query, true) || it.content.contains(query, true) }
        adapter.clear(); adapter.addAll(filtered.map { "${it.title}\n${it.content}" }); adapter.notifyDataSetChanged()
    }
    private fun refresh() { adapter.clear(); adapter.addAll(displayList()); adapter.notifyDataSetChanged(); save() }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); entries.add(Entry(o.getString("title"), o.getString("content"))) }
    }
    private fun save() {
        val arr = JSONArray()
        for (e in entries) { val o = JSONObject(); o.put("title", e.title); o.put("content", e.content); arr.put(o) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
