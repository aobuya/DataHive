package com.example.datahive.holder

import android.app.AppOpsManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.datahive.R
import com.example.datahive.databinding.FragmentAppUsageBinding
import com.example.datahive.databinding.FragmentNavDashboardBinding

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService


class NavUsage : Fragment() {
    private var _binding: FragmentAppUsageBinding? = null
    private val binding get() = _binding!!
    private lateinit var listView: ListView
    private lateinit var appDataUsageList: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppUsageBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        appDataUsageList = mutableListOf()

        if (hasUsageStatsPermission()) {
            displayAppDataUsage()
        } else {
            requestUsageStatsPermission()
        }

        return binding.root
    }
    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), requireContext().packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun requestUsageStatsPermission() {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        startActivity(intent)
    }

    private fun displayAppDataUsage() {
        val networkStatsManager =
        requireContext().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = requireContext().packageManager
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        for (appInfo in installedApps) {
        val uid = appInfo.uid
        val appName = appInfo.loadLabel(packageManager).toString()

    try {
            val networkStats = networkStatsManager.queryDetailsForUid(
            ConnectivityManager.TYPE_WIFI,
            null,
            0,
            System.currentTimeMillis(),
            uid
            )

        var totalDataUsage = 0L
            while (networkStats.hasNextBucket()) {
            val bucket = android.app.usage.NetworkStats.Bucket()
            networkStats.getNextBucket(bucket)
            totalDataUsage += bucket.rxBytes + bucket.txBytes }

            appDataUsageList.add("$appName: ${totalDataUsage / 1024} KB")

            } catch (e: Exception) {
            e.printStackTrace()
            }
        }
    }

    private fun updateListView() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, appDataUsageList)
        binding.listView.adapter = adapter
    }
    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            displayAppDataUsage()
            updateListView()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


