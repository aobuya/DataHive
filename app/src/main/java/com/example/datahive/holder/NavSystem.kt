package com.example.datahive.holder

import android.app.AppOpsManager
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datahive.FilterBottomSheet
import com.example.datahive.R
import com.example.datahive.app_usage.AppDataAdapter
import com.example.datahive.app_usage.AppDetails
import com.example.datahive.databinding.FragmentNavSystemBinding
import com.example.datahive.profile.ProfileFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

import com.google.firebase.auth.FirebaseAuth
import dev.jahidhasanco.networkusage.NetworkUsageManager
import dev.jahidhasanco.networkusage.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class NavSystem : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentNavSystemBinding? = null
    private val binding get() = _binding!!

    private var appDataUsageList = ArrayList<AppDetails>()
    private var todayAppDataUsageList = ArrayList<AppDetails>()
    private lateinit var appDataAdapter: AppDataAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var menu: Menu

    private lateinit var dataHiveAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNavSystemBinding.inflate(inflater, container, false)


        dataHiveAuth = FirebaseAuth.getInstance()

        //search view
        binding.appUsageSearchView.setOnQueryTextListener(this)

        //Load Ads
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        binding.profileTopAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.nav_host_fragment, ProfileFragment()).addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }

        return binding.root
    }
    private fun showFilterSheet() {
        val filterSheet = FilterBottomSheet()
        filterSheet.show(childFragmentManager, "BottomSheetDialog")
    }

    override fun onResume() {
        super.onResume()
        if (hasUsageStatsPermission()) {
            displayAppDataUsage()
        } else {
            requestUsageStatsPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager =
            requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            "android:get_usage_stats", android.os.Process.myUid(), requireContext().packageName
        )
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
        val progress = binding.progressBar

        val networkUsage =
            NetworkUsageManager(requireContext(), Util.getSubscriberId(requireContext()))
        progress.visibility = View.VISIBLE

        for (appInfo in installedApps) {
            // Check if the app is a system app
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                val uid = appInfo.uid
                val appName = appInfo.loadLabel(packageManager).toString()
                val appIcon = appInfo.loadIcon(packageManager)

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
                        totalDataUsage += bucket.rxBytes + bucket.txBytes
                    }

                    val appDetails = AppDetails(appName, appIcon, totalDataUsage)
                    appDataUsageList.add(appDetails)

                    val todayDate = getCurrentDateTime()
                    val todayDateInString = todayDate.toString("dd/M/yyyy")

                    val todayAppDetails = AppDetails(
                        appName,
                        totalDataUsage = totalDataUsage,
                        date = todayDateInString
                    )
                    todayAppDataUsageList.add(todayAppDetails)


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


        // Sort the appDataUsageList by total data usage from largest to smallest
        appDataUsageList = ArrayList(appDataUsageList.sortedByDescending { it.totalDataUsage })


        progress.visibility = View.GONE

        val layoutManager = LinearLayoutManager(context)
        val appDataRecyclerView: RecyclerView = requireView().findViewById(R.id.listViewsystem)
        appDataRecyclerView.layoutManager = layoutManager
        appDataAdapter = AppDataAdapter(appDataUsageList)
        appDataRecyclerView.adapter = appDataAdapter


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



    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
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

}



