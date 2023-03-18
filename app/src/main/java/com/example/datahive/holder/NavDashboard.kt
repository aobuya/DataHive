package com.example.datahive.holder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.datahive.R
import com.example.datahive.databinding.FragmentNavDashboardBinding
import com.example.datahive.databinding.FragmentThirdScreenBinding

class NavDashboard : Fragment() {
    private var _binding: FragmentNavDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavDashboardBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))

        return binding.root

    }


}