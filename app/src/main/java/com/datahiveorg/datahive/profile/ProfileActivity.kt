package com.datahiveorg.datahive.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.datahiveorg.datahive.BottomSheetFragment
import com.datahiveorg.datahive.R
import com.datahiveorg.datahive.databinding.ActivityProfileBinding
import com.datahiveorg.datahive.login.LogInActivity
import com.datahiveorg.datahive.login.RegisterActivity
import com.datahiveorg.datahive.termsofservice.PrivacyPolicy
import com.datahiveorg.datahive.termsofservice.TOC
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity(), ProfileModalBottomSheet.WriteToRoomDBListener {


    private lateinit var binding: ActivityProfileBinding
    private lateinit var dataHiveAuth: FirebaseAuth
    private lateinit var dataHiveUserViewModel: UserViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataHiveAuth = FirebaseAuth.getInstance()
        dataHiveUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        dataHiveUserViewModel.readAllData.observe(this) { user_table ->
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
        /**val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)**/

        binding.signOutButton.setOnClickListener {
            dataHiveAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            finishAffinity()

        }

        binding.signUpRedirect.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finishAffinity()
        }
        //open Bottomsheet for server configuration
        binding.cardConfig.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, "BottomSheetDialog")
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
        binding.cardStandards.setOnClickListener {
            val intent = Intent(this, TOC::class.java)
            startActivity(intent)

        }
        //POC
        binding.cardStandards2.setOnClickListener {
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