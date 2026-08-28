package com.tommyarena.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class ClipboardActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_clipboard"
    private val key = "clip_history"
    private lateinit var history: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var clipboardManager: ClipboardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clipboard)

        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        history = mutableListOf()
        for (i in 0 until arr.length()) history.add(arr.getString(i))

        val listView = findViewById<ListView>(R.id.clipHistoryList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
        listView.adapter = adapter

        val currentText = findViewById<TextView>(R.id.currentClipText)
        fun refreshCurrent() {
            val clip = clipboardManager.primaryClip()
            currentText.text = clip ?: "(clipboard empty)"
        }
        refreshCurrent()

        findViewById<Button>(R.id.btnSaveClip).setOnClickListener {
            val clip = clipboardManager.primaryClip()
            if (clip != null && clip.isNotBlank()) {
                history.add(0, clip)
                adapter.notifyDataSetChanged()
                save()
                Toast.makeText(this, "Saved to history", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val text = history[position]
            clipboardManager.setPrimaryClip(ClipData.newPlainText("Tommy Arena", text))
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show()
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            history.removeAt(position)
            adapter.notifyDataSetChanged()
            save()
            true
        }
    }

    private fun ClipboardManager.primaryClip(): String? {
        val clip = this.primaryClip
        if (clip == null || clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(this@ClipboardActivity).toString()
    }

    private fun save() {
        val arr = JSONArray()
        history.forEach { arr.put(it) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
