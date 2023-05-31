package com.example.datahive.holder

import android.app.AppOpsManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.datahive.R
import com.example.datahive.databinding.FragmentAppUsageBinding

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.provider.Settings
import android.view.Menu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datahive.FilterBottomSheet
import com.example.datahive.app_usage.AppDataAdapter
import com.example.datahive.app_usage.AppDetails
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.collections.ArrayList
import dev.jahidhasanco.networkusage.*
import com.example.datahive.profile.ProfileFragment
import java.text.SimpleDateFormat


class NavInstalled : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentAppUsageBinding? = null
    private val binding get() = _binding!!

    private var appDataUsageList = ArrayList<AppDetails>()
    private var todayAppDataUsageList = ArrayList<AppDetails>()
    private lateinit var appDataAdapter: AppDataAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var dataHiveAuth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppUsageBinding.inflate(inflater, container, false)


        //Fab
        binding.fab.setOnClickListener {
            showFilterSheet()
        }

        dataHiveAuth = FirebaseAuth.getInstance()

        //search view
        binding.appUsageSearchView.setOnQueryTextListener(this)

        //Load Ads
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        binding.appUsageTopAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.profile -> {
                    findNavController().navigate(R.id.action_appUsageFragment_to_profileFragment)
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
    }/*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        this.menu = menu
        val inflater: MenuInflater = requireActivity().menuInflater
        inflater.inflate(R.menu.app_usage_menu, menu)
        val searchItem = menu.findItem(R.id.app_usage_search_view)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }*/


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
            // Check if the app is not a system app
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
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
                appDataUsageList.add(appDetails)

                val todayDate = getCurrentDateTime()
                val todayDateInString = todayDate.toString("dd/M/yyyy")

                val todayAppDetails = AppDetails(
                    appName, totalDataUsage = totalDataUsage, date = todayDateInString
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
        val appDataRecyclerView: RecyclerView = requireView().findViewById(R.id.listView)
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