package com.example.datahive.introslider

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.datahive.databinding.FragmentLobbyBinding
import com.example.datahive.holder.MainNavigation
import com.example.datahive.login.LogInActivity
import com.example.datahive.login.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LobbyFragment : Fragment() {

    private var _binding: FragmentLobbyBinding? = null
    private val binding get() = _binding!!

    private lateinit var dataHiveAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLobbyBinding.inflate(inflater, container, false)

        dataHiveAuth = FirebaseAuth.getInstance()


        binding.getStartedButton.setOnClickListener {
            dataHiveAuth.signInAnonymously()
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "signInAnonymously:success")
                    } else {
                        Log.w(TAG, "signInAnonymously:failure", task.exception)
                        Toast.makeText(
                            requireContext(),
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

            requireActivity().run {
                startActivity(Intent(this, MainNavigation::class.java))
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