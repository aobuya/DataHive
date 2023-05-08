package com.example.datahive.holder

import com.example.datahive.profile.ProfileModalBottomSheet
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.datahive.BottomSheetFragment
import com.example.datahive.databinding.FragmentNavProfileBinding
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.RegisterActivity
import com.example.datahive.profile.User
import com.example.datahive.profile.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

import com.google.firebase.auth.FirebaseAuth


class NavProfile : Fragment() {

    private var _binding: FragmentNavProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataHiveAuth: FirebaseAuth
    private lateinit var dataHiveUserViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavProfileBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        dataHiveAuth = FirebaseAuth.getInstance()
        dataHiveUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        dataHiveUserViewModel.readAllData.observe(viewLifecycleOwner) { user_table ->
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
        MobileAds.initialize(requireContext())
        val adView = binding.adView
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        binding.signOutButton.setOnClickListener {
            dataHiveAuth.signOut()

            requireActivity().run {
                startActivity(Intent(this, LogInActivity::class.java))
                finishAffinity()
            }

        }

        binding.signUpRedirect.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, RegisterActivity::class.java))
                finishAffinity()
            }
        }
        //open Bottomsheet for server configuration
        binding.cardConfig.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(childFragmentManager, "BottomSheetDialog")
        }
        //open the Gmail and send a report
        binding.cardReport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("datahive07@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "DataHive - Report")
            }
            if (intent.resolveActivity(requireContext().packageManager) != null) {
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
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                startActivity(intent)
            }
        }

        //toggle bottom sheet
        binding.editProfileFab.setOnClickListener {
            showProfileModalBottomSheet()
        }


        return binding.root
    }

    private fun showProfileModalBottomSheet() {
        val modalBottomSheet = ProfileModalBottomSheet()
        modalBottomSheet.show(requireActivity().supportFragmentManager, ProfileModalBottomSheet.TAG)
    }

    override fun onResume() {
        super.onResume()
        dataHiveUserViewModel.readAllData.observe(viewLifecycleOwner) { user_table ->
            user_table.forEach {
                binding.profileTopAppBar.title = it.username
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




