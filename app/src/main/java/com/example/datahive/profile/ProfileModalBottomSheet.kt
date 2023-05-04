package com.example.datahive.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.datahive.databinding.FragmentProfileModalBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileModalBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentProfileModalBottomSheetBinding

    private lateinit var dataHiveUserViewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileModalBottomSheetBinding.inflate(inflater, container, false)

        dataHiveUserViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.profileBottomsheetButton.setOnClickListener {
            val username = binding.profileUsername.text?.trim().toString().trim()
            insertToRoomDB(username)
        }

        return binding.root
    }

    companion object {
        const val TAG = "ProfileModalBottomSheet"
    }

    private fun insertToRoomDB(username: String) {
        if (inputCheck(username)) {
            val user = User(0, username)
            try {
                dataHiveUserViewModel.addUser(user)
                Toast.makeText(requireContext(), "Changes have been saved", Toast.LENGTH_SHORT)
                    .show()
            } catch (e: Exception) {
                Log.d("Add to ROOM DB", "${e.printStackTrace()}")
                Toast.makeText(
                    requireContext(), "Something went wrong, please try again.", Toast.LENGTH_SHORT
                ).show()

            }
        } else {
            Toast.makeText(requireContext(), "Must not be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(username: String): Boolean {
        return username.isNotEmpty()
    }
}