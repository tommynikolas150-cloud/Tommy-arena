package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotesActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_notes"
    private val key = "notes_list"
    private lateinit var notes: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val stored = prefs.getStringSet(key, linkedSetOf()) ?: linkedSetOf()
        notes = stored.toMutableList()

        val listView = findViewById<ListView>(R.id.notesList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, notes)
        listView.adapter = adapter

        val input = findViewById<EditText>(R.id.noteInput)

        findViewById<Button>(R.id.btnAddNote).setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            notes.add(text)
            adapter.notifyDataSetChanged()
            input.text.clear()
            save()
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            notes.removeAt(position)
            adapter.notifyDataSetChanged()
            save()
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun save() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        prefs.edit().putStringSet(key, notes.toSet()).apply()
    }
}
