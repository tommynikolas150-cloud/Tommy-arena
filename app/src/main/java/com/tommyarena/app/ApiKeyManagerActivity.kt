package com.tommyarena.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class ApiKeyManagerActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_apikeys"
    private val key = "keys_json"
    private data class KeyEntry(var name: String, var value: String)
    private val keys = mutableListOf<KeyEntry>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_key_manager)
        load()

        val listView = findViewById<ListView>(R.id.keyList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            cm.setPrimaryClip(ClipData.newPlainText("api key", keys[position].value))
            Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
        }
        listView.setOnItemLongClickListener { _, _, position, _ -> keys.removeAt(position); refresh(); true }

        val nameInput = findViewById<EditText>(R.id.keyNameInput)
        val valueInput = findViewById<EditText>(R.id.keyValueInput)
        findViewById<Button>(R.id.btnSaveKey).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val value = valueInput.text.toString().trim()
            if (name.isEmpty() || value.isEmpty()) return@setOnClickListener
            keys.add(KeyEntry(name, value))
            nameInput.text.clear(); valueInput.text.clear()
            refresh()
        }
    }

    private fun displayList() = keys.map { "${it.name}: ${it.value.take(4)}${"*".repeat((it.value.length - 4).coerceAtLeast(0))}" }
    private fun refresh() { adapter.clear(); adapter.addAll(displayList()); adapter.notifyDataSetChanged(); save() }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) { val o = arr.getJSONObject(i); keys.add(KeyEntry(o.getString("name"), o.getString("value"))) }
    }
    private fun save() {
        val arr = JSONArray()
        for (k in keys) { val o = JSONObject(); o.put("name", k.name); o.put("value", k.value); arr.put(o) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
