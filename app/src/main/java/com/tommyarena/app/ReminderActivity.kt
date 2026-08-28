package com.tommyarena.app

import android.Manifest
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderActivity : AppCompatActivity() {

    private val prefsName = "tommy_arena_reminders"
    private val key = "reminders_json"
    private data class Reminder(var text: String, var timeMillis: Long, var requestCode: Int)
    private val reminders = mutableListOf<Reminder>()
    private lateinit var adapter: ArrayAdapter<String>
    private var pickedCalendar: Calendar? = null
    private val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        load()

        val listView = findViewById<ListView>(R.id.reminderList)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayList())
        listView.adapter = adapter

        listView.setOnItemLongClickListener { _, _, position, _ ->
            cancelAlarm(reminders[position])
            reminders.removeAt(position)
            refresh()
            true
        }

        val input = findViewById<EditText>(R.id.reminderInput)

        findViewById<Button>(R.id.btnPickDateTime).setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                TimePickerDialog(this, { _, hour, minute ->
                    val c = Calendar.getInstance()
                    c.set(year, month, day, hour, minute, 0)
                    pickedCalendar = c
                    Toast.makeText(this, "Picked: ${fmt.format(c.time)}", Toast.LENGTH_SHORT).show()
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        findViewById<Button>(R.id.btnSetReminder).setOnClickListener {
            val text = input.text.toString().trim()
            val cal = pickedCalendar
            if (text.isEmpty() || cal == null) {
                Toast.makeText(this, "Enter text and pick a date/time first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val requestCode = System.currentTimeMillis().toInt()
            val reminder = Reminder(text, cal.timeInMillis, requestCode)
            scheduleAlarm(reminder)
            reminders.add(reminder)
            reminders.sortBy { it.timeMillis }
            input.text.clear()
            pickedCalendar = null
            refresh()
        }
    }

    private fun displayList() = reminders.map { "${fmt.format(it.timeMillis)} — ${it.text}" }

    private fun refresh() {
        adapter.clear()
        adapter.addAll(displayList())
        adapter.notifyDataSetChanged()
        save()
    }

    private fun scheduleAlarm(reminder: Reminder) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("text", reminder.text)
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminder.requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminder.timeMillis, pendingIntent)
    }

    private fun cancelAlarm(reminder: Reminder) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this, reminder.requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun load() {
        val prefs = getSharedPreferences(prefsName, MODE_PRIVATE)
        val arr = JSONArray(prefs.getString(key, "[]") ?: "[]")
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            reminders.add(Reminder(obj.getString("text"), obj.getLong("timeMillis"), obj.getInt("requestCode")))
        }
        reminders.sortBy { it.timeMillis }
    }

    private fun save() {
        val arr = JSONArray()
        for (r in reminders) {
            val obj = JSONObject()
            obj.put("text", r.text)
            obj.put("timeMillis", r.timeMillis)
            obj.put("requestCode", r.requestCode)
            arr.put(obj)
        }
        getSharedPreferences(prefsName, MODE_PRIVATE).edit().putString(key, arr.toString()).apply()
    }
}
