package com.example.datahive.holder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.datahive.R
import com.example.datahive.databinding.FragmentAppUsageBinding
import com.example.datahive.databinding.FragmentNavDashboardBinding


class NavUsage : Fragment() {
    private var _binding: FragmentAppUsageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAppUsageBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}