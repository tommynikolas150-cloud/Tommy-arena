package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckedTextView
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class TaskManagerActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_tasks"
    private val key = "tasks_json"
    private data class Task(var text: String, var done: Boolean)
    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_manager)

        loadTasks()

        val listView = findViewById<ListView>(R.id.taskList)
        adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, displayList()) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val ctv = view as CheckedTextView
                ctv.isChecked = tasks[position].done
                ctv.setTextColor(if (tasks[position].done) 0xFF666666.toInt() else 0xFFFFFFFF.toInt())
                return view
            }
        }
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            tasks[position].done = !tasks[position].done
            refresh()
        }
        listView.setOnItemLongClickListener { _, _, position, _ ->
            tasks.removeAt(position)
            refresh()
            true
        }

        val input = findViewById<EditText>(R.id.taskInput)
        findViewById<Button>(R.id.btnAddTask).setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            tasks.add(Task(text, false))
            input.text.clear()
            refresh()
        }
    }

    private fun displayList() = tasks.map { it.text }

    private fun refresh() {
        adapter.clear()
        adapter.addAll(displayList())
        adapter.notifyDataSetChanged()
        saveTasks()
    }

    private fun loadTasks() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val json = prefs.getString(key, "[]") ?: "[]"
        val arr = JSONArray(json)
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            tasks.add(Task(obj.getString("text"), obj.getBoolean("done")))
        }
    }

    private fun saveTasks() {
        val arr = JSONArray()
        for (t in tasks) {
            val obj = JSONObject()
            obj.put("text", t.text)
            obj.put("done", t.done)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
