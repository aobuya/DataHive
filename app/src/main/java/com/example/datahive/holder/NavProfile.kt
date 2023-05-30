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
import com.example.datahive.R
import com.example.datahive.databinding.FragmentNavProfileBinding
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.RegisterActivity
import com.example.datahive.profile.ProfileFragment
import com.example.datahive.profile.User
import com.example.datahive.profile.UserViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds

import com.google.firebase.auth.FirebaseAuth


class NavProfile : Fragment() {

    private var _binding: FragmentNavProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataHiveAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavProfileBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        dataHiveAuth = FirebaseAuth.getInstance()


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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




