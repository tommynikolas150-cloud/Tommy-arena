package com.tommyarena.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class EmailAutomationActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_email_templates"
    private val key = "templates_json"
    private data class Template(var name: String, var subject: String, var body: String)
    private val templates = mutableListOf<Template>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_automation)
        load()

        val listView = findViewById<ListView>(R.id.templateList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, templates.map { it.name })
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val t = templates[position]
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_SUBJECT, t.subject)
            intent.putExtra(Intent.EXTRA_TEXT, t.body)
            startActivity(Intent.createChooser(intent, "Send Email"))
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            templates.removeAt(position); refresh(); true
        }

        val nameInput = findViewById<EditText>(R.id.templateNameInput)
        val subjectInput = findViewById<EditText>(R.id.templateSubjectInput)
        val bodyInput = findViewById<EditText>(R.id.templateBodyInput)
        findViewById<Button>(R.id.btnSaveTemplate).setOnClickListener {
            val name = nameInput.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            templates.add(Template(name, subjectInput.text.toString(), bodyInput.text.toString()))
            nameInput.text.clear(); subjectInput.text.clear(); bodyInput.text.clear()
            refresh()
        }
    }

    private fun refresh() { adapter.clear(); adapter.addAll(templates.map { it.name }); adapter.notifyDataSetChanged(); save() }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            templates.add(Template(o.getString("name"), o.getString("subject"), o.getString("body")))
        }
    }
    private fun save() {
        val arr = JSONArray()
        for (t in templates) { val o = JSONObject(); o.put("name", t.name); o.put("subject", t.subject); o.put("body", t.body); arr.put(o) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
