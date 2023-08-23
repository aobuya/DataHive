package com.datahiveorg.datahive.introslider

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.datahiveorg.datahive.R
import com.datahiveorg.datahive.databinding.FragmentSecondScreenBinding


class SecondScreen : Fragment() {
    
    private var _binding: FragmentSecondScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        
        _binding = FragmentSecondScreenBinding.inflate(inflater, container, false)
        
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager2)
        
        binding.secondScreenSkipButton.setOnClickListener {
            findNavController().navigate(R.id.action_viewPager_to_LobbyFragment)
            finishedOnBoarding()
        }

        binding.secondScreenArrowRight.setOnClickListener {
            viewPager?.currentItem = 2
        }
        
        
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