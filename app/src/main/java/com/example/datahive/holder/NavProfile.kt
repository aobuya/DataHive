package com.example.datahive.holder

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.datahive.R
import com.example.datahive.databinding.FragmentAppUsageBinding
import com.example.datahive.databinding.FragmentNavProfileBinding
import com.example.datahive.login.LogInActivity
import com.google.firebase.auth.FirebaseAuth


class NavProfile : Fragment() {

    private var _binding: FragmentNavProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataHiveAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNavProfileBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.root.findViewById(R.id.toolbar))

        dataHiveAuth = FirebaseAuth.getInstance()

        binding.signOutButton.setOnClickListener {
            dataHiveAuth.signOut()

            requireActivity().run {
                startActivity(Intent(this,LogInActivity::class.java))
                finishAffinity()
            }


        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}