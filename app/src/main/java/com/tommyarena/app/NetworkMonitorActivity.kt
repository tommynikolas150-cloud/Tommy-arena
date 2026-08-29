package com.tommyarena.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NetworkMonitorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_monitor)

        val infoText = findViewById<TextView>(R.id.networkInfoText)
        findViewById<Button>(R.id.btnRefreshNetwork).setOnClickListener { infoText.text = buildInfo() }
        infoText.text = buildInfo()
    }

    private fun buildInfo(): String {
        val sb = StringBuilder()
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        val caps = network?.let { cm.getNetworkCapabilities(it) }

        sb.append("🌐 CONNECTION\n")
        if (caps == null) {
            sb.append("No active connection\n\n")
        } else {
            val type = when {
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Mobile Data"
                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                else -> "Unknown"
            }
            sb.append("Type: $type\n")
            sb.append("Validated internet: ${caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)}\n\n")
        }

        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        sb.append("📶 WI-FI DETAILS\n")
        if (wifiInfo != null && wifiInfo.networkId != -1) {
            sb.append("SSID: ${wifiInfo.ssid}\n")
            sb.append("Link speed: ${wifiInfo.linkSpeed} Mbps\n")
            sb.append("Signal: ${wifiInfo.rssi} dBm\n")
            sb.append("IP address: ${Formatter.formatIpAddress(wifiInfo.ipAddress)}\n")
        } else {
            sb.append("Not connected to Wi-Fi\n")
        }

        return sb.toString()
    }
}
