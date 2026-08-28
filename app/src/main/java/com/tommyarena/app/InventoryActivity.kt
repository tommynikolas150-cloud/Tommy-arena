package com.tommyarena.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class InventoryActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_inventory"
    private val key = "items_json"
    private data class Item(var name: String, var qty: Int)
    private val items = mutableListOf<Item>()
    private lateinit var adapter: InventoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)

        load()

        val listView = findViewById<ListView>(R.id.inventoryList)
        adapter = InventoryAdapter()
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            items.removeAt(position)
            adapter.notifyDataSetChanged()
            save()
            true
        }

        val input = findViewById<EditText>(R.id.itemNameInput)
        findViewById<Button>(R.id.btnAddItem).setOnClickListener {
            val name = input.text.toString().trim()
            if (name.isEmpty()) return@setOnClickListener
            items.add(Item(name, 0))
            input.text.clear()
            adapter.notifyDataSetChanged()
            save()
        }
    }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            items.add(Item(obj.getString("name"), obj.getInt("qty")))
        }
    }

    private fun save() {
        val arr = JSONArray()
        for (it in items) {
            val obj = JSONObject()
            obj.put("name", it.name)
            obj.put("qty", it.qty)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }

    inner class InventoryAdapter : ArrayAdapter<Item>(this@InventoryActivity, 0, items) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_inventory_row, parent, false)
            val item = items[position]
            view.findViewById<TextView>(R.id.itemName).text = item.name
            view.findViewById<TextView>(R.id.itemQty).text = item.qty.toString()
            view.findViewById<Button>(R.id.btnMinus).setOnClickListener {
                if (item.qty > 0) item.qty--
                notifyDataSetChanged()
                save()
            }
            view.findViewById<Button>(R.id.btnPlus).setOnClickListener {
                item.qty++
                notifyDataSetChanged()
                save()
            }
            return view
        }
    }
}
