package com.example.datahive.introslider

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.datahive.Dashboard
import com.example.datahive.login.SignUpActivity
import com.example.datahive.databinding.FragmentLobbyBinding
import com.example.datahive.holder.MainNavigation
import com.example.datahive.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        // Get the current user from Firebase
        val user = FirebaseAuth.getInstance().currentUser


        // Check if the user is null or not
        if (user != null) {

            // The user is logged in, move to the dashboard activity
            val intent = Intent(requireActivity(), MainNavigation::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            // The user is not logged in, show the lobby fragment

            binding.getStartedButton.setOnClickListener {
                requireActivity().run {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    finish()
                }
            }
            binding.passRedirect.setOnClickListener {
                requireActivity().run {
                    startActivity(Intent(this, SignUpActivity::class.java))
                    finish()
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}