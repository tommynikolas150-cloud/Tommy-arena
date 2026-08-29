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

class LinkManagerActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_links"
    private val key = "links_json"
    private data class Link(var title: String, var url: String)
    private val links = mutableListOf<Link>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_manager)

        load()

        val listView = findViewById<ListView>(R.id.linkList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val url = links[position].url
            val fixedUrl = if (!url.startsWith("http")) "https://$url" else url
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fixedUrl)))
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            links.removeAt(position)
            refresh()
            true
        }

        val titleInput = findViewById<EditText>(R.id.linkTitleInput)
        val urlInput = findViewById<EditText>(R.id.linkUrlInput)

        findViewById<Button>(R.id.btnSaveLink).setOnClickListener {
            val url = urlInput.text.toString().trim()
            if (url.isEmpty()) return@setOnClickListener
            val title = titleInput.text.toString().trim().ifEmpty { url }
            links.add(0, Link(title, url))
            titleInput.text.clear()
            urlInput.text.clear()
            refresh()
        }
    }

    private fun displayList() = links.map { "${it.title}\n${it.url}" }

    private fun refresh() {
        adapter.clear()
        adapter.addAll(displayList())
        adapter.notifyDataSetChanged()
        save()
    }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            links.add(Link(obj.getString("title"), obj.getString("url")))
        }
    }

    private fun save() {
        val arr = JSONArray()
        for (l in links) {
            val obj = JSONObject()
            obj.put("title", l.title)
            obj.put("url", l.url)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
