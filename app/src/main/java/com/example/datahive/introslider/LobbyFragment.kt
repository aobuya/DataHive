package com.example.datahive.introslider

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.SignUpActivity

import com.example.datahive.databinding.FragmentLobbyBinding
import com.example.datahive.login.RegisterActivity

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)


        binding.getStartedButton.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, RegisterActivity::class.java))
                finish()
            }
        }
        binding.passRedirect.setOnClickListener {
            requireActivity().run {
                startActivity(Intent(this, LogInActivity::class.java))
                finish()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}