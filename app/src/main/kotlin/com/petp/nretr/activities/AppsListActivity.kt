package com.petp.nretr.activities

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.petp.nretr.R
import com.petp.nretr.activities.adapters.AppsAdapter
import com.petp.nretr.com.petp.nretr.repository.CheckedAppsRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AppsListActivity : AppCompatActivity() {

    @Inject
    lateinit var checkedAppsRepository: CheckedAppsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_list)

        val allApps = getAllApps()
        val checkedApps = loadCheckedApps()

        listViewOfCheckedAppsInit(allApps, checkedApps)
        backButtonInit()

        Log.d("AppsListActivity", checkedApps.toString())
    }

    private fun getAllApps(): MutableList<ApplicationInfo> {
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return allApps.filter { app ->
            (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        }.sortedBy { app -> app.loadLabel(packageManager).toString() }.toMutableList()
    }

    private fun backButtonInit() {
        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun listViewOfCheckedAppsInit(
        apps: MutableList<ApplicationInfo>,
        checkedApps: MutableSet<String>
    ) {
        val listView = findViewById<ListView>(R.id.appsListView)
        val adapter = AppsAdapter(this, apps, checkedApps)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val app = apps[position]
            Log.d("AppsListActivity", "Clicked on ${app.loadLabel(packageManager)}")
            if (checkedApps.contains(app.packageName)) {
                checkedApps.remove(app.packageName)
            } else {
                checkedApps.add(app.packageName)
            }
            saveCheckedApps(checkedApps.toSet())
            adapter.notifyDataSetChanged()
        }
    }

    private fun loadCheckedApps(): MutableSet<String> {
        return checkedAppsRepository.loadCheckedApps()
    }

    private fun saveCheckedApps(checkedApps: Set<String>) {
        checkedAppsRepository.saveCheckedApps(checkedApps)
    }
}