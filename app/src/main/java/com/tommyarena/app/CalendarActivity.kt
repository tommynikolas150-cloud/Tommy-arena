package com.tommyarena.app

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class CalendarActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_calendar"
    private val key = "events_json"
    private data class Event(var date: String, var title: String, var sortKey: Long)
    private val events = mutableListOf<Event>()
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedDateStr: String? = null
    private var selectedSortKey: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        load()

        val listView = findViewById<ListView>(R.id.eventList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            events.removeAt(position)
            refresh()
            true
        }

        val input = findViewById<EditText>(R.id.eventInput)

        findViewById<Button>(R.id.btnPickDate).setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val c = Calendar.getInstance()
                c.set(year, month, day)
                selectedSortKey = c.timeInMillis
                selectedDateStr = "%02d/%02d/%04d".format(day, month + 1, year)
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        findViewById<Button>(R.id.btnAddEvent).setOnClickListener {
            val title = input.text.toString().trim()
            val date = selectedDateStr
            if (title.isEmpty() || date == null) return@setOnClickListener
            events.add(Event(date, title, selectedSortKey))
            events.sortBy { it.sortKey }
            input.text.clear()
            selectedDateStr = null
            refresh()
        }
    }

    private fun displayList() = events.map { "${it.date} — ${it.title}" }

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
            events.add(Event(obj.getString("date"), obj.getString("title"), obj.getLong("sortKey")))
        }
        events.sortBy { it.sortKey }
    }

    private fun save() {
        val arr = JSONArray()
        for (e in events) {
            val obj = JSONObject()
            obj.put("date", e.date)
            obj.put("title", e.title)
            obj.put("sortKey", e.sortKey)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
