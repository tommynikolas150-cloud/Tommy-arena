package com.tommyarena.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<android.widget.Button>(R.id.btnCalculator).setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnUnitConverter).setOnClickListener {
            startActivity(Intent(this, UnitConverterActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnStopwatch).setOnClickListener {
            startActivity(Intent(this, StopwatchActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnQrCode).setOnClickListener {
            startActivity(Intent(this, QrCodeActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnNotes).setOnClickListener {
            startActivity(Intent(this, NotesActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnTasks).setOnClickListener {
            startActivity(Intent(this, TaskManagerActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnReminders).setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnCalendar).setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnClipboard).setOnClickListener {
            startActivity(Intent(this, ClipboardActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnTextUtils).setOnClickListener {
            startActivity(Intent(this, TextUtilsActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnSystemMonitor).setOnClickListener {
            startActivity(Intent(this, SystemMonitorActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnBatteryMonitor).setOnClickListener {
            startActivity(Intent(this, BatteryMonitorActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnExpenseTracker).setOnClickListener {
            startActivity(Intent(this, ExpenseTrackerActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnInventory).setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnInvoice).setOnClickListener {
            startActivity(Intent(this, InvoiceActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnMarkdown).setOnClickListener {
            startActivity(Intent(this, MarkdownActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnLinkManager).setOnClickListener {
            startActivity(Intent(this, LinkManagerActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnUrlShortener).setOnClickListener {
            startActivity(Intent(this, UrlShortenerActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnWebsiteChecker).setOnClickListener {
            startActivity(Intent(this, WebsiteCheckerActivity::class.java))
        }
        findViewById<android.widget.Button>(R.id.btnSpeedTest).setOnClickListener {
            startActivity(Intent(this, SpeedTestActivity::class.java))
        }
    }
}
