package com.tommyarena.app

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SqliteManagerActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sqlite_manager)

        db = openOrCreateDatabase("tommy_arena_demo.db", MODE_PRIVATE, null)

        val input = findViewById<EditText>(R.id.sqlInput)
        val result = findViewById<TextView>(R.id.sqlResultText)

        findViewById<Button>(R.id.btnRunSql).setOnClickListener {
            val sql = input.text.toString().trim()
            if (sql.isEmpty()) return@setOnClickListener
            try {
                if (sql.trim().startsWith("select", true)) {
                    val cursor = db.rawQuery(sql, null)
                    val sb = StringBuilder()
                    sb.append(cursor.columnNames.joinToString(" | ")).append("\n")
                    while (cursor.moveToNext()) {
                        val row = (0 until cursor.columnCount).map { cursor.getString(it) ?: "NULL" }
                        sb.append(row.joinToString(" | ")).append("\n")
                    }
                    cursor.close()
                    result.text = sb.toString()
                } else {
                    db.execSQL(sql)
                    result.text = "OK"
                }
            } catch (e: Exception) {
                result.text = "Error: ${e.message}"
            }
        }
    }
}
