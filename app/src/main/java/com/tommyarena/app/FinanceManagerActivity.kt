package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class FinanceManagerActivity : AppCompatActivity() {
    private val prefsName = "tommy_arena_finance"
    private val key = "categories_json"
    private data class Cat(var name: String, var budget: Double, var spent: Double)
    private val categories = mutableListOf<Cat>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_manager)
        load()
        val listView = findViewById<ListView>(R.id.categoryList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            categories[position].spent += 1
            refresh()
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            categories.removeAt(position); refresh(); true
        }
        val nameInput = findViewById<EditText>(R.id.categoryInput)
        val budgetInput = findViewById<EditText>(R.id.budgetInput)
        findViewById<Button>(R.id.btnAddCategory).setOnClickListener {
            val name = nameInput.text.toString().trim()
            val budget = budgetInput.text.toString().toDoubleOrNull() ?: 0.0
            if (name.isEmpty()) return@setOnClickListener
            categories.add(Cat(name, budget, 0.0))
            nameInput.text.clear(); budgetInput.text.clear()
            refresh()
        }
    }

    private fun displayList() = categories.map {
        val pct = if (it.budget > 0) (it.spent / it.budget * 100).toInt() else 0
        "${it.name}: %.2f / %.2f (${pct}%%)".format(it.spent, it.budget)
    }
    private fun refresh() { adapter.clear(); adapter.addAll(displayList()); adapter.notifyDataSetChanged(); save() }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            categories.add(Cat(o.getString("name"), o.getDouble("budget"), o.getDouble("spent")))
        }
    }
    private fun save() {
        val arr = JSONArray()
        for (c in categories) { val o = JSONObject(); o.put("name", c.name); o.put("budget", c.budget); o.put("spent", c.spent); arr.put(o) }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
