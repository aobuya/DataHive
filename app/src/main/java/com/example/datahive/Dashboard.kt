package com.example.datahive

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.datahive.databinding.ActivityDashboardBinding
import java.util.*
import android.widget.ArrayAdapter
import android.os.Build


class Dashboard : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var packageManager: PackageManager
    private lateinit var usageStatsManager: UsageStatsManager
    private val myPermissions = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //val listView = findViewById<ListView>(R.id.listView)
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        packageManager = applicationContext.packageManager


        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.PACKAGE_USAGE_STATS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.PACKAGE_USAGE_STATS),
                myPermissions
            )
        } else {
            getUsageStats()
        }
    }


    private fun getUsageStats() {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, -7) // get stats for last 7 days
            val startTime = calendar.timeInMillis
            val endTime = System.currentTimeMillis()

            val usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)

            if (usageStatsList.isNotEmpty()) {
                usageStatsList.sortByDescending { usageStats ->
                    try {
                        val fieldRx = UsageStats::class.java.getDeclaredField("totalRxBytes")
                        val fieldTx = UsageStats::class.java.getDeclaredField("totalTxBytes")
                        fieldRx.isAccessible = true
                        fieldTx.isAccessible = true
                        (fieldRx.getLong(usageStats) + fieldTx.getLong(usageStats)).compareTo(
                            fieldRx.getLong(usageStatsList[0]) + fieldTx.getLong(usageStatsList[0])
                        )
                    } catch (e: Exception) {
                        0
                    }
                }

                val topApps = mutableListOf<Pair<String, Long>>()
                for (i in 0 until minOf(usageStatsList.size, 5)) {
                    val usageStats = usageStatsList[i]
                           try {
                        val applicationInfo = packageManager.getApplicationInfo(usageStats.packageName, 0)
                        val appName = applicationInfo.loadLabel(packageManager).toString()
                        val dataUsage = try {
                            val fieldRx = UsageStats::class.java.getDeclaredField("totalRxBytes")
                            val fieldTx = UsageStats::class.java.getDeclaredField("totalTxBytes")
                            fieldRx.isAccessible = true
                            fieldTx.isAccessible = true
                            fieldRx.getLong(usageStats) + fieldTx.getLong(usageStats)
                        } catch (e: Exception) {
                            0L
                        }
                        topApps.add(Pair(appName, dataUsage))
                    } catch (e: PackageManager.NameNotFoundException) {
                        e.printStackTrace()
                    }
                }
                binding.appsList.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, topApps)

            }
        }
}


//backup ---- code :)
/**usageStatsList = mutableListOf()
        packageManager = packageManager
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val listView = binding.listView

        if (hasUsageStatsPermission()) {
            getUsageStats()?.let { usageStatsList ->
                val sortedUsageStats = usageStatsList.sortedByDescending { it.totalTimeInForeground }
                val top5Apps = sortedUsageStats.take(5)
                val appNames = top5Apps.map { appUsage -> packageManager.getApplicationLabel(packageManager.getApplicationInfo(appUsage.packageName, PackageManager.GET_META_DATA)) }
                val appDataUsage = top5Apps.map {
                        appUsage -> TrafficStats.getUidRxBytes(appUsage.uid) + TrafficStats.getUidTxBytes(appUsage.uid) }
                //val appDataUsage = top5Apps.map { appUsage -> TrafficStats.getUidRxBytes(appUsage.uid) + TrafficStats.getUidTxBytes(appUsage.uid) }.toIntArray()

                val appList = appNames.zip(appDataUsage)
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, appList)
                listView.adapter = adapter
            }
        } else {
            requestUsageStatsPermission()
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.PACKAGE_USAGE_STATS),
                MyPermissions
            )
        }
    }

    private fun getUsageStats(): List<UsageStats>? {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val startTime = calendar.timeInMillis
        val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        return usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)?.filter { usageStats ->
            (usageStats.totalTimeInForeground > 0L) && !isSystemPackage(usageStats.packageName)
        }
    }

    private fun isSystemPackage(packageName: String): Boolean {
        val packageManager = packageManager
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } catch (e: PackageManager.NameNotFoundException) {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MyPermissions) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUsageStats()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}**/