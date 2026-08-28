package com.tommyarena.app

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

class ExpenseTrackerActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_expenses"
    private val key = "transactions_json"
    private data class Tx(var desc: String, var amount: Double)
    private val transactions = mutableListOf<Tx>()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var balanceText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_tracker)

        balanceText = findViewById(R.id.balanceText)
        load()

        val listView = findViewById<ListView>(R.id.transactionList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter
        listView.setOnItemLongClickListener { _, _, position, _ ->
            transactions.removeAt(position)
            refresh()
            true
        }

        val descInput = findViewById<EditText>(R.id.descInput)
        val amountInput = findViewById<EditText>(R.id.amountInput)

        findViewById<Button>(R.id.btnAddIncome).setOnClickListener {
            addTransaction(descInput, amountInput, positive = true)
        }
        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            addTransaction(descInput, amountInput, positive = false)
        }

        updateBalance()
    }

    private fun addTransaction(descInput: EditText, amountInput: EditText, positive: Boolean) {
        val desc = descInput.text.toString().trim()
        val amount = amountInput.text.toString().toDoubleOrNull()
        if (desc.isEmpty() || amount == null) return
        transactions.add(0, Tx(desc, if (positive) amount else -amount))
        descInput.text.clear()
        amountInput.text.clear()
        refresh()
    }

    private fun displayList() = transactions.map {
        val sign = if (it.amount >= 0) "+" else ""
        "${it.desc}: $sign%.2f".format(it.amount)
    }

    private fun refresh() {
        adapter.clear()
        adapter.addAll(displayList())
        adapter.notifyDataSetChanged()
        updateBalance()
        save()
    }

    private fun updateBalance() {
        val total = transactions.sumOf { it.amount }
        balanceText.text = "Balance: %.2f".format(total)
    }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            transactions.add(Tx(obj.getString("desc"), obj.getDouble("amount")))
        }
    }

    private fun save() {
        val arr = JSONArray()
        for (t in transactions) {
            val obj = JSONObject()
            obj.put("desc", t.desc)
            obj.put("amount", t.amount)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
