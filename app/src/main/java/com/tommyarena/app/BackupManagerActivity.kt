package com.tommyarena.app

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BackupManagerActivity : AppCompatActivity() {

    private val backupPrefsSets = listOf(
        "tommy_arena_tasks", "tommy_arena_reminders", "tommy_arena_calendar", "tommy_arena_clipboard",
        "tommy_arena_notes", "tommy_arena_expenses", "tommy_arena_inventory", "tommy_arena_links",
        "tommy_arena_books", "tommy_arena_finance"
    )
    private val metaPrefsName = "tommy_arena_backup_meta"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_backup_manager)

        val lastBackupText = findViewById<TextView>(R.id.lastBackupText)
        val metaPrefs = getSharedPreferences(metaPrefsName, MODE_PRIVATE)
        lastBackupText.text = "Last backup: ${metaPrefs.getString("last_backup", "Never")}"

        val autoSwitch = findViewById<Switch>(R.id.autoBackupSwitch)
        autoSwitch.isChecked = metaPrefs.getBoolean("auto_backup_enabled", false)
        autoSwitch.setOnCheckedChangeListener { _, checked ->
            metaPrefs.edit().putBoolean("auto_backup_enabled", checked).apply()
        }

        findViewById<Button>(R.id.btnBackupNow).setOnClickListener {
            val file = createBackupFile()
            metaPrefs.edit().putString("last_backup", SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())).apply()
            lastBackupText.text = "Last backup: ${metaPrefs.getString("last_backup", "")}"

            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "application/json"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(intent, "Share Backup"))
        }
    }

    private fun createBackupFile(): File {
        val root = JSONObject()
        for (prefName in backupPrefsSets) {
            val prefs: SharedPreferences = getSharedPreferences(prefName, MODE_PRIVATE)
            val entry = JSONObject()
            for ((k, v) in prefs.all) {
                entry.put(k, v.toString())
            }
            root.put(prefName, entry)
        }
        val file = File(cacheDir, "tommy_arena_backup.json")
        FileWriter(file).use { it.write(root.toString(2)) }
        return file
    }
}
