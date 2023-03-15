package com.example.datahive.introslider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datahive.R
import com.example.datahive.databinding.FragmentThirdScreenBinding


class ThirdScreen : Fragment() {
    
    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        //inflate layout for this fragment
        _binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        finishedOnBoarding()
        
        
        
        
        return binding.root
    }
    private fun finishedOnBoarding() {
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putBoolean("Finished", true)
        editor.apply()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
   
}