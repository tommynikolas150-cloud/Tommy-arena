package com.tommyarena.app

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.StatFs
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SystemMonitorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_monitor)

        val infoText = findViewById<TextView>(R.id.systemInfoText)
        findViewById<Button>(R.id.btnRefresh).setOnClickListener { infoText.text = buildInfo() }
        infoText.text = buildInfo()
    }

    private fun buildInfo(): String {
        val sb = StringBuilder()

        sb.append("📱 DEVICE\n")
        sb.append("Model: ${Build.MANUFACTURER} ${Build.MODEL}\n")
        sb.append("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\n\n")

        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)
        sb.append("🧠 MEMORY\n")
        sb.append("Total RAM: ${totalRamMb} MB\n")
        sb.append("Available RAM: ${availRamMb} MB\n")
        sb.append("Low memory: ${if (memInfo.lowMemory) "Yes" else "No"}\n\n")

        val internalStat = StatFs(filesDir.path)
        val internalTotal = internalStat.totalBytes / (1024 * 1024)
        val internalFree = internalStat.availableBytes / (1024 * 1024)
        sb.append("💾 INTERNAL STORAGE\n")
        sb.append("Total: ${internalTotal} MB\n")
        sb.append("Free: ${internalFree} MB\n")
        sb.append("Used: ${internalTotal - internalFree} MB\n")

        return sb.toString()
    }
}
