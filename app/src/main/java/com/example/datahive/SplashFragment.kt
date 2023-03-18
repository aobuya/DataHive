package com.example.datahive

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.datahive.databinding.FragmentSplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)


        Handler(Looper.getMainLooper()).postDelayed({
                if (finishedOnBoarding()) {
                    findNavController().navigate(R.id.action_viewPager_to_LobbyFragment)
                } else {
                    findNavController().navigate(R.id.action_splashFragment_to_viewPager)

                }
            }, 2000)
            return binding.root

        }


    private fun finishedOnBoarding(): Boolean {

        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPref.getBoolean("Finished", false)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}