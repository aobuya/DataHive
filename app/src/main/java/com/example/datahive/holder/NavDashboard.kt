package com.example.datahive.holder

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.datahive.DataUsagesAdapter
import com.example.datahive.R
import com.example.datahive.UsagesData
import com.example.datahive.databinding.FragmentNavDashboardBinding
import android.provider.Settings
import android.content.Context.*
import android.os.Handler
import android.os.Process
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahidhasanco.networkusage.*
import android.Manifest
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.datahive.app_usage.AppDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class NavDashboard : Fragment() {
    private var _binding: FragmentNavDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataUsagesAdapter: DataUsagesAdapter
    private var usagesDataList = ArrayList<UsagesData>()

    private lateinit var dataHiveAuth: FirebaseAuth


    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavDashboardBinding.inflate(inflater, container, false)
        //Load Ads
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        var adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        usagesDataList.clear()

        //add2
        val adView2 = binding.adView2
        adRequest = AdRequest.Builder().build()
        adView2.loadAd(adRequest)

        dataHiveAuth = FirebaseAuth.getInstance()

        binding.dashboardTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                R.id.profile -> {
                    findNavController().navigate(R.id.action_dashboardFragment_to_profileFragment)
                    true
                }else -> false
            }
        }

        setupPermissions()

        val networkUsage =
            NetworkUsageManager(requireContext(), Util.getSubscriberId(requireContext()))

        val handler = Handler()
        val runnableCode = object : Runnable {
            override fun run() {
                val todayM = networkUsage.getUsage(Interval.today, NetworkType.MOBILE)
                val todayW = networkUsage.getUsage(Interval.today, NetworkType.WIFI)

                binding.wifiUsagesTv.text =
                    Util.formatData(todayW.downloads, todayW.uploads)[2]
                binding.dataUsagesTv.text =
                    Util.formatData(todayM.downloads, todayM.uploads)[2]
                handler.postDelayed(this, 1000)
            }
        }
        runnableCode.run()

        val last30DaysWIFI = networkUsage.getMultiUsage(
            Interval.lastMonthDaily, NetworkType.WIFI
        )

        val last30DaysMobile = networkUsage.getMultiUsage(
            Interval.lastMonthDaily, NetworkType.MOBILE
        )

        for (i in last30DaysWIFI.indices) {
            usagesDataList.add(
                UsagesData(
                    Util.formatData(
                        last30DaysMobile[i].downloads, last30DaysMobile[i].uploads
                    )[2], Util.formatData(
                        last30DaysWIFI[i].downloads, last30DaysWIFI[i].uploads
                    )[2], last30DaysWIFI[i].date
                )
            )
        }

        val last7DaysTotalWIFI = networkUsage.getUsage(
            Interval.last7days, NetworkType.WIFI
        )

        val last7DaysTotalMobile = networkUsage.getUsage(
            Interval.last7days, NetworkType.MOBILE
        )

        val last30DaysTotalWIFI = networkUsage.getUsage(
            Interval.last30days, NetworkType.WIFI
        )

        val last30DaysTotalMobile = networkUsage.getUsage(
            Interval.last30days, NetworkType.MOBILE
        )

        usagesDataList.add(
            UsagesData(
                Util.formatData(
                    last7DaysTotalMobile.downloads, last7DaysTotalMobile.uploads
                )[2], Util.formatData(
                    last7DaysTotalWIFI.downloads, last7DaysTotalWIFI.uploads
                )[2], "Last 7 Days"
            )
        )

        binding.wifiDataThisMonth.text = Util.formatData(
            last30DaysTotalWIFI.downloads, last30DaysTotalWIFI.uploads
        )[2]

        binding.mobileDataThisMonth.text = Util.formatData(
            last30DaysTotalMobile.downloads, last30DaysTotalMobile.uploads
        )[2]
        binding.last7DaysMobile.text = Util.formatData(last7DaysTotalMobile.uploads, last7DaysTotalMobile.downloads)[2]
        binding.last7DaysWifi.text = Util.formatData(last7DaysTotalWIFI.uploads, last7DaysTotalWIFI.downloads)[2]


        dataUsagesAdapter = DataUsagesAdapter(usagesDataList)
        binding.monthlyDataUsagesRv.layoutManager = LinearLayoutManager(requireContext())
        binding.monthlyDataUsagesRv.setHasFixedSize(true)
        binding.monthlyDataUsagesRv.adapter = dataUsagesAdapter

        return binding.root
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_PHONE_STATE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_PHONE_STATE), 34
            )
        }

        if (checkUsagePermission() != true) {
            Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
        }
    }




    private fun checkUsagePermission(): Any {
        val appOps = context?.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = context?.let {
            appOps.checkOpNoThrow(
                "android:get_usage_stats", Process.myUid(), it.packageName
            )
        }
        val granted = mode == AppOpsManager.MODE_ALLOWED
        if (!granted) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            return false
        }
        return true

    }
}