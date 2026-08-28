package com.tommyarena.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StopwatchActivity : AppCompatActivity() {

    private var running = false
    private var startTime = 0L
    private var elapsed = 0L
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeText: TextView

    private val tick = object : Runnable {
        override fun run() {
            if (running) {
                val total = elapsed + (System.currentTimeMillis() - startTime)
                timeText.text = formatTime(total)
                handler.postDelayed(this, 200)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stopwatch)

        timeText = findViewById(R.id.timeText)
        val startPauseBtn = findViewById<Button>(R.id.btnStartPause)

        startPauseBtn.setOnClickListener {
            if (!running) {
                running = true
                startTime = System.currentTimeMillis()
                startPauseBtn.text = "Pause"
                handler.post(tick)
            } else {
                running = false
                elapsed += System.currentTimeMillis() - startTime
                startPauseBtn.text = "Start"
            }
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            running = false
            elapsed = 0
            startPauseBtn.text = "Start"
            timeText.text = formatTime(0)
        }
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(tick)
    }
}
