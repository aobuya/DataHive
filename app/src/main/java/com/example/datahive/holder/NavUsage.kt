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

import android.app.usage.NetworkStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.datahive.BottomSheetFragment
import com.example.datahive.FilterBottomSheet
import com.example.datahive.UsagesData
import com.example.datahive.app_usage.AppDataAdapter
import com.example.datahive.app_usage.AppDetails
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList
import dev.jahidhasanco.networkusage.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class NavUsage : Fragment(), SearchView.OnQueryTextListener {
    private var _binding: FragmentAppUsageBinding? = null
    private val binding get() = _binding!!

    private var appDataUsageList = ArrayList<AppDetails>()
    private lateinit var appDataAdapter: AppDataAdapter
    private lateinit var progressBar: ProgressBar

    private lateinit var dataHiveAuth : FirebaseAuth



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAppUsageBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        //Fab
        binding.fab.setOnClickListener {
            showFilterSheet()
        }

        dataHiveAuth = FirebaseAuth.getInstance()

        binding.appUsageSearchView.setOnQueryTextListener(this)
        //Load Ads
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        val adRequest = AdRequest.Builder()
            .build()
        adView.loadAd(adRequest)

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
            "android:get_usage_stats",
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
        
        val networkUsage = NetworkUsageManager(requireContext(), Util.getSubscriberId(requireContext()))
        progress.visibility = View.VISIBLE

        for (appInfo in installedApps) {
            // Check if the app is not a system app
            if (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
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

                    val todayDataUsage = networkUsage.getUsage(Interval.today, NetworkType.ALL)

                    val appDetails = AppDetails(appName, appIcon, totalDataUsage, todayDataUsage.toString())
                    appDataUsageList.add(appDetails)

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

        addUsageDataToFirestore(appDataUsageList)


        /*share appDataUsagelist with navdashboard fragment
        val bundle = Bundle()
        bundle.putStringArrayList("fromNavUsage",appDataUsageList)

        val navUsageFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.dashboardFragment)
        navUsageFragment?.let{
            navUsageFragment.arguments = bundle
        }
        */


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

    private fun addUsageDataToFirestore(usageData: ArrayList<AppDetails>) {
        val getCurrentUser = dataHiveAuth.currentUser
        val dataHiveDB = Firebase.firestore

       usageData.forEach {app ->
           app.icon = null
       }

        val userDataMap = usageData.associateBy { it.date }

        getCurrentUser?.let {
            val currentUserEmail = it.email.toString()

            //for ((key, value) in userDataMap) {
            dataHiveDB.collection("users").document(currentUserEmail)
                .set(userDataMap, SetOptions.merge())
                .addOnSuccessListener { Log.d("Firestore DataHive", "Data written successfully") }
                .addOnFailureListener { e ->
                    Log.w(
                        "Firestore DataHive", "Error writing document", e
                    )
                }
            //}
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


}

private fun Bundle.putStringArrayList(s: String, appDataUsageList: ArrayList<AppDetails>) {

}
