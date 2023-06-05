package com.datahiveorg.datahive.holder

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.datahiveorg.datahive.DataUsagesAdapter
import com.datahiveorg.datahive.UsagesData

import android.provider.Settings
import android.content.Context.*
import android.os.Handler
import android.os.Process
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahidhasanco.networkusage.*
import android.Manifest
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.datahiveorg.datahive.databinding.FragmentNavDashboardBinding

import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.ads.MobileAds
import android.R
import com.datahiveorg.datahive.profile.ProfileActivity


class NavDashboard : Fragment() {
    private var _binding: FragmentNavDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataUsagesAdapter: DataUsagesAdapter
    private var usagesDataList = ArrayList<UsagesData>()

    private lateinit var dataHiveAuth: FirebaseAuth
    private val PERMISSION_REQUEST_CODE = 34


    @SuppressLint("HardwareIds")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavDashboardBinding.inflate(inflater, container, false)
        //Load Ads
        MobileAds.initialize(requireContext())
        /**val adView = binding.adView
        var adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        usagesDataList.clear()

        //add2
        val adView2 = binding.adView2
        adRequest = AdRequest.Builder().build()
        adView2.loadAd(adRequest)**/

        dataHiveAuth = FirebaseAuth.getInstance()

        binding.dashboardTopAppBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId) {
                com.datahiveorg.datahive.R.id.profile -> {
                    requireActivity().run {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                showPermissionExplanationDialog()
            } else {
                requestPermission()
            }
        } else {
            if (checkUsagePermission()) {
                //Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionExplanationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Permission Required")
            .setMessage("The app requires access to read phone state for tracking data usages. Please grant the permission.")
            .setPositiveButton("Grant Permission") { _, _ ->
                requestPermission()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            PERMISSION_REQUEST_CODE
        )
    }

    private fun checkUsagePermission(): Boolean {
        val appOps = context?.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = context?.let {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                it.packageName
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkUsagePermission()) {
                    Toast.makeText(requireContext(), "Permissions granted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }


}


