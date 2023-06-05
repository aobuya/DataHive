package com.datahiveorg.datahive.holder

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.datahiveorg.datahive.app_usage.AppDataAdapter
import com.datahiveorg.datahive.app_usage.AppDetails
import com.datahiveorg.datahive.databinding.FragmentNavSystemBinding


import com.google.android.gms.ads.MobileAds

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import com.datahiveorg.datahive.R
import com.datahiveorg.datahive.login.RegisterActivity
import com.datahiveorg.datahive.profile.ProfileActivity


class NavSystem : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentNavSystemBinding? = null
    private val binding get() = _binding!!

    private var appDataUsageList = ArrayList<AppDetails>()
    //private var todayAppDataUsageList = ArrayList<AppDetails>()
    private lateinit var appDataAdapter: AppDataAdapter

    private lateinit var dataHiveAuth: FirebaseAuth
    private var usageStatsPermissionDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNavSystemBinding.inflate(inflater, container, false)

        dataHiveAuth = FirebaseAuth.getInstance()

        // Search view
        binding.appUsageSearchView.setOnQueryTextListener(this)

        // Load Ads
        MobileAds.initialize(requireContext())
        /**val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)**/

        binding.profileTopAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    requireActivity().run {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }

        loadAppDataUsage()

        return binding.root
    }
    private fun loadAppDataUsage() {
        val progress = binding.progressBar
        progress.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.IO).launch {
            val installedApps = getInstalledApplications()
            val appDataUsageList = getAppDataUsage(installedApps)

            withContext(Dispatchers.Main) {
                this@NavSystem.appDataUsageList = appDataUsageList
                updateAppDataAdapter()
                progress.visibility = View.GONE
            }
        }
    }

    private suspend fun getInstalledApplications(): List<ApplicationInfo> = withContext(Dispatchers.IO) {
        val packageManager = requireContext().packageManager
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    private suspend fun getAppDataUsage(installedApps: List<ApplicationInfo>): ArrayList<AppDetails> = withContext(Dispatchers.IO) {
        val networkStatsManager =
            requireContext().getSystemService(Context.NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = requireContext().packageManager
        val appDataList = ArrayList<AppDetails>()

        for (appInfo in installedApps) {
            // Check if the app is a system app
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                val uid = appInfo.uid
                val appName = appInfo.loadLabel(packageManager).toString()
                val appIcon = appInfo.loadIcon(packageManager)

                try {
                    val networkStats = networkStatsManager.queryDetailsForUid(
                        ConnectivityManager.TYPE_WIFI, null, 0, System.currentTimeMillis(), uid
                    )

                    var totalDataUsage = 0L
                    while (networkStats.hasNextBucket()) {
                        val bucket = android.app.usage.NetworkStats.Bucket()
                        networkStats.getNextBucket(bucket)
                        totalDataUsage += bucket.rxBytes + bucket.txBytes
                    }

                    val appDetails = AppDetails(appName, appIcon, totalDataUsage)
                    appDataList.add(appDetails)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        appDataList.sortByDescending { it.totalDataUsage }
        appDataList
    }

    private fun updateAppDataAdapter() {
        val layoutManager = LinearLayoutManager(context)
        val appDataRecyclerView: RecyclerView = binding.listViewsystem
        appDataRecyclerView.layoutManager = layoutManager

        if (!::appDataAdapter.isInitialized) {
            appDataAdapter = AppDataAdapter(appDataUsageList)
            appDataRecyclerView.adapter = appDataAdapter
        } else {
            appDataAdapter.notifyDataSetChanged()
        }
    }

    private fun filterList(text: String) {
        val filteredList = ArrayList<AppDetails>()
        for (app in appDataUsageList) {
            val appName = app.app
            if (appName.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                filteredList.add(app)
            }
        }

        if (filteredList.isNotEmpty()) {
            appDataAdapter.setFilteredList(filteredList)
        } else {
            //Toast.makeText(requireContext(), "App not found", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            filterList(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            filterList(query)
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}




