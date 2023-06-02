package com.example.datahive.holder

import android.app.AppOpsManager
import android.app.usage.NetworkStats
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
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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
    private lateinit var usageStatsPermissionDialog: AlertDialog

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
                    findNavController().navigate(R.id.action_systemFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display permission explanation dialog if permission is not granted
        if (!hasUsageStatsPermission()) {
            showUsageStatsPermissionDialog()
        } else {
            displayAppDataUsage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (::usageStatsPermissionDialog.isInitialized && usageStatsPermissionDialog.isShowing) {
            usageStatsPermissionDialog.dismiss()
        }
    }

    private fun showFilterSheet() {
        val filterSheet = FilterBottomSheet()
        filterSheet.show(childFragmentManager, "BottomSheetDialog")
    }

    private fun showUsageStatsPermissionDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_permission_explanation, null)

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
        usageStatsPermissionDialog = builder.create()

        val btnGrantPermission = dialogView.findViewById<Button>(R.id.btnGrantPermission)
        btnGrantPermission.setOnClickListener {
            requestUsageStatsPermission()
            usageStatsPermissionDialog.dismiss()
        }

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        btnCancel.setOnClickListener {
            usageStatsPermissionDialog.dismiss()
            // Handle permission denial (e.g., show error message)
        }

        usageStatsPermissionDialog.show()
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOpsManager =
            requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            requireContext().packageName
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
                        ConnectivityManager.TYPE_WIFI, null, 0, System.currentTimeMillis(), uid
                    )

                    var totalDataUsage = 0L
                    val bucket = NetworkStats.Bucket()
                    while (networkStats.hasNextBucket()) {
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

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
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




