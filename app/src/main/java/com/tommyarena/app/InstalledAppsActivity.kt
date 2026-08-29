package com.tommyarena.app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class InstalledAppsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installed_apps)

        val pm = packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val userApps = apps.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
        val names = userApps.map { pm.getApplicationLabel(it).toString() }.sorted()

        findViewById<TextView>(R.id.appCountText).text = "${names.size} user-installed apps"
        findViewById<ListView>(R.id.appsList).adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, names)
    }
}
