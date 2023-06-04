package com.example.datahive.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.datahive.BottomSheetFragment
import com.example.datahive.R
import com.example.datahive.databinding.ActivityProfileBinding
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.RegisterActivity
import com.example.datahive.termsofservice.PrivacyPolicy
import com.example.datahive.termsofservice.TOC
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity(), ProfileModalBottomSheet.WriteToRoomDBListener {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var dataHiveAuth: FirebaseAuth
    private lateinit var dataHiveUserViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        dataHiveAuth = FirebaseAuth.getInstance()
        dataHiveUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        dataHiveUserViewModel.readAllData.observe(this) {user_table ->
            user_table.forEach {
                binding.profileTopAppBar.title = it.username
            }

        }

        val isUserAnonymous = dataHiveAuth.currentUser!!.isAnonymous
        if (!isUserAnonymous) {
            dataHiveAuth.currentUser?.let {
                binding.email.text = it.email
            }
        }
        if (isUserAnonymous) {
            binding.signOutButton.visibility = View.GONE
        } else {
            binding.signUpRedirect.visibility = View.GONE
        }
        //Load Ads
        MobileAds.initialize(this)
        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        binding.signOutButton.setOnClickListener {
            dataHiveAuth.signOut()
                startActivity(Intent(this, LogInActivity::class.java))
                finishAffinity()
        }

        binding.signUpRedirect.setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
        }
        //open Bottomsheet for server configuration
        binding.cardConfig.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(this.supportFragmentManager, "BottomSheetDialog")
        }
        //open the Gmail and send a report
        binding.cardReport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("datahive07@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "DataHive - Report")
            }
            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
            }

        }
        //open the Gmail and send a support request
        binding.cardSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("datahive07@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "DataHive - Support")
            }
            if (intent.resolveActivity(this.packageManager) != null) {
                startActivity(intent)
            }
        }
        //TOS
        binding.cardStandards.setOnClickListener{
            val intent = Intent(this, TOC::class.java)
            startActivity(intent)

        }
        //POC
        binding.cardStandards2.setOnClickListener{
            val intent = Intent(this, PrivacyPolicy::class.java)
            startActivity(intent)

        }

        //toggle bottom sheet
        binding.editProfileFab.setOnClickListener {
            showProfileModalBottomSheet()
        }

    }
    private fun showProfileModalBottomSheet() {
        val modalBottomSheet = ProfileModalBottomSheet()
        modalBottomSheet.setDataWriteListener(this)
        modalBottomSheet.show(this.supportFragmentManager, ProfileModalBottomSheet.TAG)
    }

    override fun onDataWritten(data: User) {
        dataHiveUserViewModel.readAllData.observe(this) { user_table ->
            user_table.forEach {
                binding.profileTopAppBar.title = it.username
            }
        }
    }
}